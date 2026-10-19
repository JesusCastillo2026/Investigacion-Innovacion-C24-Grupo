# VigiAire: simulador ESP32 + MQTT

**Proyecto Wokwi:** [Abrir VigiAire ESP32 + MQTT](https://wokwi.com/projects/478264195609475073)

Este prototipo publica por MQTT una lectura simulada del sensor MQ-2 cada dos segundos. La app Android se suscribe al mismo tema y muestra el valor ADC y el estado recibido.

## Componentes simulados

- ESP32 DevKit v1: microcontrolador con Wi-Fi.
- MQ-2 de Wokwi: señal relativa de gas/humo, no mide PM2.5.
- Pantalla LCD 16x2, tres LEDs de estado y resistencias de 220 Ω.

## Mapeo del circuito

| MQ-2 de Wokwi | ESP32 | Uso |
| --- | --- | --- |
| VCC | VIN | Alimentación del sensor en la simulación |
| GND | GND | Tierra común |
| AO | GPIO34 | Lectura analógica; se promedian 20 muestras para reducir variaciones |
| DO | GPIO32 | Alarma digital activa en LOW; GPIO32 permite `INPUT_PULLUP` |

La lectura se compara con el umbral demostrativo `GOOD_MAX = 2100`: hasta ese valor se muestra `VENTILE`; por encima, `ESPERE`; cuando DO se activa, se muestra `ALERTA`. El umbral es solo para probar la lógica y no equivale a una concentración de PM2.5.

En Wokwi AO está conectado directamente para facilitar la simulación. Para un ESP32 físico, no conectar directamente la salida de 5 V del módulo MQ-2 a GPIO34: usar un divisor de tensión o un adaptador de nivel compatible con 3.3 V.

## MQTT para la demostración

- Broker público de prueba: `broker.hivemq.com:1883`.
- Tema: `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`.
- QoS 0: se muestran los mensajes que llegan; no se garantiza su almacenamiento.
- No requiere usuario ni contraseña. El broker y el tema son públicos: no enviar información privada ni usar esta configuración en producción.

Cada mensaje tiene este formato:

```json
{"device":"VigiAire-ESP32","sensor":"MQ-2","gasAdc":1250,"status":"VENTILE","seq":1,"uptimeMs":2000}
```

`gasAdc` está en el rango ADC del ESP32 (0–4095). Los estados y umbrales son demostrativos, no son mediciones ambientales ni recomendaciones sanitarias.

## Archivos

- `sketch.ino`: conexión Wi-Fi, lectura, semáforo y publicación MQTT.
- `diagram.json`: ESP32, MQ-2, LCD y LEDs con su mapeo de pines.
- `libraries.txt`: bibliotecas usadas por Wokwi.

## Iniciar el simulador

1. Abre el proyecto de Wokwi y pulsa **Start the simulation**.
2. En el Monitor Serial, espera los mensajes `conectado` y `MQTT publicado`.
3. Cambia el control de gas del MQ-2 para variar la señal y el estado del LCD/LED.
4. Deja la simulación activa mientras ejecutas la app Android.

## Ejecutar la app Android

1. Abre `software/AppVigiAire` en Android Studio y espera la sincronización de Gradle.
2. Ejecuta la app en un teléfono o emulador con Internet.
3. Inicia sesión de demostración: `demo@vigiaire.com` / `123456`.
4. En la barra inferior, abre **Dispositivo** y pulsa **Conectar MQTT**.
5. La pantalla debe mostrar el indicador de conexión, la lectura `gasAdc` y el estado que publica Wokwi.

Ambos extremos deben usar el mismo broker y tema, y Wokwi debe seguir ejecutándose. La red del aula debe permitir MQTT por TCP en el puerto 1883; si lo bloquea, probar con otra red.

## Límites del prototipo

El MQ-2 simulado representa gases combustibles/humo y no mide PM2.5. La pantalla de conexión de la app rotula el dato como señal relativa de MQ-2 para no confundirlo con una concentración real. Este broker público y la conexión sin cifrado son solo para la demostración académica; una implementación real requiere broker privado, autenticación, TLS y un sensor óptico de partículas calibrado.
