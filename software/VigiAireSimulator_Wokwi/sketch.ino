#include <WiFi.h>
#include <PubSubClient.h>
#include <LiquidCrystal.h>
#include <cstring>

// VigiAire MQTT demo: the Wokwi MQ-2 reports a relative gas/smoke signal,
// not PM2.5. This project is a connectivity prototype, not an air monitor.
constexpr int GAS_INPUT = 34;  // ADC1 input; usable while Wi-Fi is active.
constexpr int GAS_DIGITAL = 32;  // Supports INPUT_PULLUP; GPIO35 does not.
constexpr int LED_GREEN = 25;
constexpr int LED_AMBER = 26;
constexpr int LED_RED = 27;
// Wokwi MQ-2 demo calibration: average the noisy analog signal and use DOUT
// for a separate high-gas alarm. These are simulation thresholds, not PM2.5.
constexpr int GOOD_MAX = 2100;
constexpr int ADC_SAMPLES = 20;
constexpr unsigned long PUBLISH_INTERVAL_MS = 2000;

// LCD pins: RS, E, D4, D5, D6, D7.
LiquidCrystal lcd(23, 22, 21, 19, 18, 17);

// Public demonstration broker. Do not send private data through this broker.
const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";
const char* MQTT_HOST = "broker.hivemq.com";
constexpr uint16_t MQTT_PORT = 1883;
const char* MQTT_TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry";

WiFiClient networkClient;
PubSubClient mqtt(networkClient);
unsigned long lastPublish = 0;
unsigned long sequenceNumber = 0;

const char* stateFor(int gasSignal, bool highGasAlarm) {
  if (highGasAlarm) return "ALERTA";
  if (gasSignal <= GOOD_MAX) return "VENTILE";
  return "ESPERE";
}

int readGasSignalAverage() {
  uint32_t total = 0;
  for (int i = 0; i < ADC_SAMPLES; ++i) {
    total += analogRead(GAS_INPUT);
    delay(5);
  }
  return total / ADC_SAMPLES;
}

void showState(int gasSignal, const char* state) {
  const bool good = strcmp(state, "VENTILE") == 0;
  const bool caution = strcmp(state, "ESPERE") == 0;

  digitalWrite(LED_GREEN, good ? HIGH : LOW);
  digitalWrite(LED_AMBER, caution ? HIGH : LOW);
  digitalWrite(LED_RED, (!good && !caution) ? HIGH : LOW);

  char firstLine[17];
  char secondLine[17];
  snprintf(firstLine, sizeof(firstLine), "MQ-2: %4d ADC  ", gasSignal);
  snprintf(secondLine, sizeof(secondLine), "ESTADO: %-7s ", state);
  lcd.setCursor(0, 0);
  lcd.print(firstLine);
  lcd.setCursor(0, 1);
  lcd.print(secondLine);
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
  String clientId = "VigiAire-Wokwi-" + String((uint32_t)ESP.getEfuseMac(), HEX);
  Serial.print("Conectando al broker MQTT... ");
  if (mqtt.connect(clientId.c_str())) {
    Serial.println("conectado");
  } else {
    Serial.print("fallo, estado MQTT ");
    Serial.println(mqtt.state());
  }
}

void publishReading(int gasSignal, const char* state) {
  char payload[180];
  snprintf(payload, sizeof(payload),
           "{\"device\":\"VigiAire-ESP32\",\"sensor\":\"MQ-2\",\"gasAdc\":%d,\"status\":\"%s\",\"seq\":%lu,\"uptimeMs\":%lu}",
           gasSignal, state, ++sequenceNumber, millis());

  const bool sent = mqtt.publish(MQTT_TOPIC, payload, false);
  Serial.println(sent ? "MQTT publicado correctamente." : "No se pudo publicar en MQTT.");
}

void setup() {
  Serial.begin(115200);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_AMBER, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  pinMode(GAS_DIGITAL, INPUT_PULLUP);
  analogReadResolution(12);

  lcd.begin(16, 2);
  lcd.clear();
  showState(0, "INICIANDO");

  WiFi.mode(WIFI_STA);
  connectWiFi();
  mqtt.setServer(MQTT_HOST, MQTT_PORT);
  mqtt.setBufferSize(256);
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) connectWiFi();
  if (!mqtt.connected()) reconnectMqtt();
  mqtt.loop();

  const int gasSignal = readGasSignalAverage();
  const bool highGasAlarm = digitalRead(GAS_DIGITAL) == LOW;
  const char* state = stateFor(gasSignal, highGasAlarm);
  showState(gasSignal, state);

  if (millis() - lastPublish >= PUBLISH_INTERVAL_MS) {
    lastPublish = millis();
    Serial.printf("Lectura MQ-2: %d ADC | Estado: %s\n", gasSignal, state);
    if (mqtt.connected()) {
      publishReading(gasSignal, state);
    } else {
      Serial.println("Lectura local disponible; esperando conexión MQTT.");
    }
  }

  delay(100);
}
