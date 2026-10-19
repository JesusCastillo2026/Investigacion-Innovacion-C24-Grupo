#include <WiFi.h>
#include <Wire.h>
#include <PubSubClient.h>
#include <LiquidCrystal_I2C.h>
#include <cstring>
#include <cstdio>

// MQ-2 simulator signal is relative ADC (0-4095), not PM2.5.
constexpr int GAS_INPUT = 34;
constexpr int GAS_DIGITAL = 32;
constexpr int LED_GREEN = 25;
constexpr int LED_AMBER = 26;
constexpr int LED_RED = 27;
constexpr int GOOD_MAX = 2100;
constexpr int ADC_SAMPLES = 20;
constexpr unsigned long PUBLISH_INTERVAL_MS = 1000;

LiquidCrystal_I2C lcd(0x27, 16, 2);
const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";
const char* MQTT_HOST = "broker.hivemq.com";
constexpr uint16_t MQTT_PORT = 1883;
const char* MQTT_TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry";

WiFiClient networkClient;
PubSubClient mqtt(networkClient);
unsigned long lastPublish = 0;
unsigned long sequenceNumber = 0;

const char* stateFor(int value, bool alarm) {
  if (alarm) return "ALERTA";
  return value <= GOOD_MAX ? "VENTILE" : "ESPERE";
}

int readGas() {
  uint32_t total = 0;
  for (int i = 0; i < ADC_SAMPLES; ++i) {
    total += analogRead(GAS_INPUT);
    delay(5);
  }
  return total / ADC_SAMPLES;
}

// One sample drives the LCD, LEDs, serial log, and MQTT payload.
void showState(int value, const char* state) {
  const bool good = strcmp(state, "VENTILE") == 0;
  const bool wait = strcmp(state, "ESPERE") == 0;
  digitalWrite(LED_GREEN, good ? HIGH : LOW);
  digitalWrite(LED_AMBER, wait ? HIGH : LOW);
  digitalWrite(LED_RED, (!good && !wait) ? HIGH : LOW);

  char line1[17];
  char line2[17];
  snprintf(line1, sizeof(line1), "MQ-2: %4d ADC", value);
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
           R"json({"device":"VigiAire-ESP32","sensor":"MQ-2","gasAdc":%d,"status":"%s","seq":%lu,"uptimeMs":%lu})json",
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

  Wire.begin(21, 22);
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("VigiAire ESP32");
  lcd.setCursor(0, 1);
  lcd.print("Iniciando...");

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
    const char* state = stateFor(value, digitalRead(GAS_DIGITAL) == LOW);

    // The LCD, status LEDs, serial output, and Android all use this exact sample.
    showState(value, state);
    Serial.printf("Lectura MQ-2: %d ADC | Estado: %s\n", value, state);
    if (mqtt.connected()) publishReading(value, state);
    else Serial.println("Lectura local disponible; esperando conexión MQTT.");
  }

  delay(20);
}
