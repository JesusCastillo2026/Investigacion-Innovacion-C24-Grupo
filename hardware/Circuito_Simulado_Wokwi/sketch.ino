#include <WiFi.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <LiquidCrystal_I2C.h>
#include <ESP32Servo.h>
#include <cstring>
#include <cstdio>

// MQ-2 simulator signal is relative ADC (0-4095), not PM2.5.
constexpr int GAS_INPUT = 34;
constexpr int GAS_DIGITAL = 32;
constexpr int LED_GREEN = 25;
constexpr int LED_AMBER = 26;
constexpr int LED_RED = 27;
constexpr int EXTRACTOR_SERVO = 18;
constexpr int GOOD_MAX = 2100;
constexpr int ALERT_MIN = 3100;
constexpr unsigned long PUBLISH_INTERVAL_MS = 1000;

LiquidCrystal_I2C lcd(0x27, 16, 2);
Servo extractorFlap;
const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";
const char* MQTT_HOST = "broker.hivemq.com";
constexpr uint16_t MQTT_PORT = 1883;
const char* MQTT_TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry";

WiFiClient networkClient;
PubSubClient mqtt(networkClient);
unsigned long lastPublish = 0;
unsigned long sequenceNumber = 0;
unsigned long nextScenarioAt = 0;
int simulatedAdc = 1200;
int targetAdc = 1350;

const char* stateFor(int value) {
  if (value >= ALERT_MIN) return "ALERTA";
  return value <= GOOD_MAX ? "VENTILE" : "ESPERE";
}

// Automatic demonstration: 78% low, 19% intermediate, 3% alert targets.
// The MQ-2 component remains visible, but these are synthetic ADC values,
// not real gas concentration or PM2.5 measurements.
int readGas() {
  const unsigned long now = millis();
  if (now >= nextScenarioAt) {
    const int draw = random(100);
    if (draw < 78) targetAdc = random(900, 1750);
    else if (draw < 97) targetAdc = random(2350, 2750);
    else targetAdc = random(3450, 3750);
    nextScenarioAt = now + random(9000, 16000);
  }
  const int step = constrain(targetAdc - simulatedAdc, -300, 300);
  simulatedAdc = constrain(simulatedAdc + step + random(-30, 31), 0, 4095);
  return simulatedAdc;
}

// One sample drives the LCD, LEDs, serial log, and MQTT payload.
void showState(int value, const char* state) {
  const bool good = strcmp(state, "VENTILE") == 0;
  const bool wait = strcmp(state, "ESPERE") == 0;
  digitalWrite(LED_GREEN, good ? HIGH : LOW);
  digitalWrite(LED_AMBER, wait ? HIGH : LOW);
  digitalWrite(LED_RED, (!good && !wait) ? HIGH : LOW);
  // Visual actuator only: opens a vent flap to represent the extractor command.
  // Wokwi does not model airflow or suction.
  const bool extractorOn = strcmp(state, "ALERTA") == 0;
  extractorFlap.write(extractorOn ? 90 : 0);

  char line1[17];
  char line2[17];
  snprintf(line1, sizeof(line1), "SIM:  %4d ADC", value);
  snprintf(line2, sizeof(line2), "ESTADO: %-7s", state);
  lcd.setCursor(0, 0);
  lcd.print(line1);
  lcd.setCursor(0, 1);
  lcd.print(line2);
}

void connectWiFi() {
  if (WiFi.status() == WL_CONNECTED) return;
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD, 6);
  Serial.print("Conectando a Wokwi-GUEST");
  const unsigned long startedAt = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - startedAt < 15000) {
    delay(250);
    Serial.print('.');
  }
  Serial.println(WiFi.status() == WL_CONNECTED ? " conectado" : " sin conexion");
}

void reconnectMqtt() {
  if (WiFi.status() != WL_CONNECTED || mqtt.connected()) return;
  const String clientId = "VigiAire-Wokwi-" + String((uint32_t)ESP.getEfuseMac(), HEX);
  Serial.print("Conectando al broker MQTT... ");
  if (mqtt.connect(clientId.c_str())) Serial.println("conectado");
  else {
    Serial.print("fallo MQTT ");
    Serial.println(mqtt.state());
  }
}

void publishReading(int value, const char* state) {
  char payload[180];
  snprintf(payload, sizeof(payload),
           R"json({"device":"VigiAire-ESP32","sensor":"MQ-2","simulated":true,"gasAdc":%d,"status":"%s","seq":%lu,"uptimeMs":%lu})json",
           value, state, ++sequenceNumber, millis());
  Serial.println(mqtt.publish(MQTT_TOPIC, payload, false)
      ? "MQTT publicado correctamente."
      : "No se pudo publicar en MQTT.");
}

void setup() {
  Serial.begin(115200);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_AMBER, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  pinMode(GAS_DIGITAL, INPUT_PULLUP);
  analogReadResolution(12);
  randomSeed((uint32_t)analogRead(GAS_INPUT) ^ micros());

  Wire.begin(21, 22);
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("VigiAire ESP32");
  lcd.setCursor(0, 1);
  lcd.print("Iniciando...");

  extractorFlap.setPeriodHertz(50);
  extractorFlap.attach(EXTRACTOR_SERVO, 500, 2400);
  extractorFlap.write(0);

  WiFi.mode(WIFI_STA);
  connectWiFi();
  mqtt.setServer(MQTT_HOST, MQTT_PORT);
  mqtt.setBufferSize(256);
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) connectWiFi();
  if (!mqtt.connected()) reconnectMqtt();
  mqtt.loop();

  if (millis() - lastPublish >= PUBLISH_INTERVAL_MS) {
    lastPublish = millis();
    const int value = readGas();
    const char* state = stateFor(value);

    // The LCD, status LEDs, serial output, and Android all use this exact sample.
    showState(value, state);
    Serial.printf("Lectura MQ-2: %d ADC | Estado: %s\n", value, state);
    if (mqtt.connected()) publishReading(value, state);
    else Serial.println("Lectura local disponible; esperando conexión MQTT.");
  }

  delay(20);
}
