#include <LiquidCrystal.h>

// Wokwi's MQ-2 simulates combustible gases and smoke, not PM2.5 particles.
// ADC thresholds are illustrative and only demonstrate the alert behavior.
constexpr int GAS_INPUT = A0;
constexpr int LED_GREEN = 8;
constexpr int LED_AMBER = 9;
constexpr int LED_RED = 10;
constexpr int GOOD_MAX = 250;
constexpr int CAUTION_MAX = 600;

// LCD pins: RS, E, D4, D5, D6, D7.
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

void showState(int gasSignal) {
  const bool good = gasSignal <= GOOD_MAX;
  const bool caution = gasSignal > GOOD_MAX && gasSignal <= CAUTION_MAX;
  const char* state = good ? "VENTILE" : (caution ? "ESPERE" : "ALERTA");

  digitalWrite(LED_GREEN, good ? HIGH : LOW);
  digitalWrite(LED_AMBER, caution ? HIGH : LOW);
  digitalWrite(LED_RED, (!good && !caution) ? HIGH : LOW);

  lcd.setCursor(0, 0);
  lcd.print("MQ2 senal:");
  lcd.print(gasSignal);
  lcd.print("   ");
  lcd.setCursor(0, 1);
  lcd.print("GAS: ");
  lcd.print(state);
  lcd.print("   ");

  Serial.print("Senal relativa MQ-2 (ADC 0-1023): ");
  Serial.print(gasSignal);
  Serial.print(" | Recomendacion demostrativa: ");
  Serial.println(state);
}

void setup() {
  Serial.begin(9600);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_AMBER, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("VigiAire listo");
  lcd.setCursor(0, 1);
  lcd.print("MQ-2 gas/humo");
  delay(1200);
}

void loop() {
  const int gasSignal = analogRead(GAS_INPUT);
  showState(gasSignal);
  delay(700);
}

