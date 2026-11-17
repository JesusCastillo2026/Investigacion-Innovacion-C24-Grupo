# App VigiAire LCD

Módulo Android independiente que convierte el móvil en una pantalla inspirada en el LCD del prototipo. Presenta el valor ADC simulado en un medidor circular, el estado VENTILE/ESPERE/ALERTA, el nivel relativo de la señal y la recepción de datos.

## Conexión con Wokwi

Se suscribe al tema de telemetría existente del ESP32:

- Broker: `broker.hivemq.com:1883`
- Tema: `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`

Inicia la simulación en Wokwi y ejecuta **VigiAire LCD**. La vista se actualiza con cada mensaje recibido; requiere Internet y conexión con el broker MQTT.

## Abrir en Android Studio

Abre el proyecto `software/AppVigiAire` y selecciona el módulo `app-lcd` como configuración de ejecución.

> Los valores son una señal de gas/humo simulada en Wokwi, no una medición real de PM2.5 ni de calidad del aire.
