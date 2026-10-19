# Software de VigiAire

El prototipo electrónico de VigiAire se prueba de forma virtual. Wokwi simula el circuito con un ESP32 y una señal de gas/humo; la app Android recibe por MQTT los datos publicados por el circuito.

- **[AppVigiAire](./AppVigiAire/):** aplicación Android desarrollada con Kotlin y Jetpack Compose. Presenta lecturas recibidas, estado, tendencias de la sesión y consejos.
- **[VigiAireSimulator_Wokwi](./VigiAireSimulator_Wokwi/):** firmware y diagrama del ESP32 simulado, LCD 16×2, tres LEDs y sensor de gas de Wokwi. [Abrir simulador](https://wokwi.com/projects/478264195609475073).

**Nota sobre el sensor:** la señal del componente Gas Sensor/MQ-2 representa gas/humo de manera simulada. No es una medición de PM2.5 ni de calidad real del aire. El sistema permite probar el circuito, los estados, la interfaz y la comunicación MQTT; para medir partículas finas se requeriría un sensor óptico PM2.5 dedicado.

Consulta la [guía del simulador](./VigiAireSimulator_Wokwi/README.md) para ver el mapeo del circuito, el tema MQTT y los pasos de ejecución y conexión con Android.
