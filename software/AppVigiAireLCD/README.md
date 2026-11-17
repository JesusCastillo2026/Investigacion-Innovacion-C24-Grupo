# AppVigiAireLCD

Proyecto Android independiente que transforma el teléfono en una pantalla inspirada en el LCD del prototipo. Presenta la lectura ADC en un medidor circular, el estado VENTILE/ESPERE/ALERTA, el nivel relativo de la señal y el estado de conexión con el circuito.

## Conexión con Wokwi

La aplicación se suscribe a la telemetría que publica el ESP32:

- Broker: `broker.hivemq.com:1883`
- Tema: `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`

Inicia la simulación de Wokwi antes de abrir la app. La pantalla se actualiza con cada mensaje recibido.

## Ejecutar en Android Studio

Abre esta carpeta `software/AppVigiAireLCD` como proyecto independiente, espera a que termine la sincronización de Gradle y ejecuta la configuración `app` en un emulador o teléfono Android. El proyecto incluye su propio Gradle Wrapper.

> La lectura es una señal analógica simulada del MQ-2 de Wokwi. No representa una medición real de PM2.5 ni de calidad del aire.
