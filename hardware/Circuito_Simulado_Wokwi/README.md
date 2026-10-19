# Circuito simulado VigiAire en Wokwi

**Abrir el circuito:** [VigiAire ESP32 + MQTT en Wokwi](https://wokwi.com/projects/478264195609475073)

Esta carpeta reúne el firmware y el diagrama del circuito electrónico simulado. Wokwi representa el ESP32, el sensor de gas, la pantalla LCD, los LEDs y su cableado. La app Android permanece en [software/AppVigiAire](../../software/AppVigiAire/) y recibe las lecturas publicadas mediante MQTT.

## Componentes simulados

- ESP32 DevKit v1: lee la señal analógica, determina el estado y gestiona Wi-Fi y MQTT.
- Gas Sensor de Wokwi, usado como señal tipo MQ-2: representa una lectura relativa de gas/humo; no mide PM2.5.
- LCD 16×2 I²C: muestra la lectura ADC y el estado.
- LEDs verde, amarillo y rojo con resistencias de 220 Ω: indican los estados del programa.
- Cables virtuales: representan alimentación, tierra y conexiones de señal.

## Mapeo

| Componente | Pin del ESP32 | Función |
| --- | --- | --- |
| Salida analógica AO del Gas Sensor | GPIO34 | Lectura ADC (promedio de 20 muestras) |
| Salida digital DO del Gas Sensor | GPIO32 | Entrada de alarma activa en LOW |
| LCD SDA | GPIO21 | Datos I²C |
| LCD SCL | GPIO22 | Reloj I²C |
| LED verde | GPIO25 | Estado `VENTILE` |
| LED amarillo | GPIO26 | Estado `ESPERE` |
| LED rojo | GPIO27 | Estado `ALERTA` |

El circuito publica aproximadamente cada segundo en el broker público `broker.hivemq.com:1883`, tema `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`. La app debe usar ese mismo broker y tema, y la simulación debe permanecer encendida para recibir los datos.

El umbral `GOOD_MAX = 2100` y los estados son parámetros de demostración. El valor ADC no es una concentración ambiental ni una medición de PM2.5. Para medir partículas finas se necesitaría un sensor óptico dedicado. El broker es público y no usa cifrado; úsalo solo para la demostración académica, nunca para datos privados.

## Archivos

- `sketch.ino`: lectura, actualización de LCD y LEDs, monitor serial y envío MQTT.
- `diagram.json`: componentes y conexiones del circuito.
- `libraries.txt`: bibliotecas necesarias en Wokwi.

## Ejecución

1. Abre el enlace de Wokwi y pulsa **Start the simulation**.
2. Cambia el control del sensor de gas y observa cómo varían el ADC y el estado en LCD, LEDs y monitor serial.
3. Abre la app Android y conecta MQTT desde la sección **Dispositivo**.
