# Software de VigiAire

La aplicación Android del proyecto se desarrolla y ejecuta desde esta carpeta:

- **[AppVigiAire](./AppVigiAire/):** aplicación Android en Kotlin y Jetpack Compose. Presenta las lecturas recibidas, estado, tendencias de sesión y consejos.

El circuito electrónico simulado y su firmware se encuentran en [hardware/Circuito_Simulado_Wokwi](../hardware/Circuito_Simulado_Wokwi/). La app recibe desde Wokwi por MQTT el valor ADC y el estado.

**Nota sobre el sensor:** la señal del Gas Sensor de Wokwi, tratado en el firmware como MQ-2, representa gas/humo simulado. No mide PM2.5 ni calidad real del aire. Para medir partículas finas se necesitaría un sensor óptico PM2.5 dedicado.

Consulta la [guía del circuito](../hardware/Circuito_Simulado_Wokwi/README.md) para revisar pines, tema MQTT y pasos para conectar la app. [Abrir simulador Wokwi](https://wokwi.com/projects/478264195609475073).
