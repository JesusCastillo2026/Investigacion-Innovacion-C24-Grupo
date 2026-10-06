#include <LiquidCrystal.h>

// VigiAire virtual: the potentiometer stands in for a PM2.5 sensor signal.
// It does not measure particles; thresholds below are demonstration values.
constexpr int PM_INPUT = A0;
constexpr int LED_GREEN = 8;
constexpr int LED_AMBER = 9;
constexpr int LED_RED = 10;
constexpr int GOOD_MAX = 15;
constexpr int CAUTION_MAX = 35;

// LCD pins: RS, E, D4, D5, D6, D7.
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

void showState(int pm25) {
  const bool good = pm25 <= GOOD_MAX;
  const bool caution = pm25 > GOOD_MAX && pm25 <= CAUTION_MAX;
  const char* state = good ? "VENTILE" : (caution ? "ESPERE" : "ALERTA");

  digitalWrite(LED_GREEN, good ? HIGH : LOW);
  digitalWrite(LED_AMBER, caution ? HIGH : LOW);
  digitalWrite(LED_RED, (!good && !caution) ? HIGH : LOW);

  char line1[17];
  snprintf(line1, sizeof(line1), "PM2.5:%3d ug/m3", pm25);
  lcd.setCursor(0, 0);
  lcd.print(line1);
  lcd.setCursor(0, 1);
  lcd.print("ESTADO: ");
  lcd.print(state);
  lcd.print("   ");

  Serial.print("Lectura PM2.5 simulada: ");
  Serial.print(pm25);
  Serial.print(" ug/m3 | Recomendacion: ");
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
  lcd.print("Lectura virtual");
  delay(1200);
}

void loop() {
  const int raw = analogRead(PM_INPUT);
  const int pm25 = map(raw, 0, 1023, 5, 80);
  showState(pm25);
  delay(700);
}

