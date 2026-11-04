# Circuito simulado VigiAire en Wokwi

**Abrir el circuito:** [VigiAire ESP32 + MQTT en Wokwi](https://wokwi.com/projects/478264195609475073)

Esta carpeta reúne el firmware y el diagrama del circuito electrónico simulado. Wokwi representa el ESP32, el sensor de gas, la pantalla LCD, los LEDs y su cableado. La app Android permanece en [software/AppVigiAire](../../software/AppVigiAire/) y recibe las lecturas publicadas mediante MQTT.

## Componentes simulados

- ESP32 DevKit v1: genera la secuencia ADC de demostración, determina el estado y gestiona Wi-Fi y MQTT.
- Gas Sensor de Wokwi, usado como señal tipo MQ-2: representa una lectura relativa de gas/humo; no mide PM2.5.
- LCD 16×2 I²C: muestra la lectura ADC y el estado.
- LEDs verde, amarillo y rojo con resistencias de 220 Ω conectadas en serie entre el GPIO de control y el ánodo (A) de cada LED; el cátodo (C) va a GND. Limitan la corriente para proteger los LEDs y los GPIO.
- Servo en GPIO18: abre una compuerta visual cuando el estado es `ALERTA`, como representación de la orden de activar un extractor. Wokwi no simula succión ni movimiento real del aire.
- Cables virtuales: representan alimentación, tierra y conexiones de señal.

## Mapeo

| Componente | Pin del ESP32 | Función |
| --- | --- | --- |
| Salida analógica AO del Gas Sensor | GPIO34 | Semilla inicial de la simulación automática; no es la lectura publicada |
| Salida digital DO del Gas Sensor | GPIO32 | Conexión de referencia, sin intervención en el modo automático |
| LCD SDA | GPIO21 | Datos I²C |
| LCD SCL | GPIO22 | Reloj I²C |
| LED verde | GPIO25 | Estado `VENTILE` |
| LED amarillo | GPIO26 | Estado `ESPERE` |
| LED rojo | GPIO27 | Estado `ALERTA` |
| Servo de compuerta/extractor representativo | GPIO18 | Abre a 90° en `ALERTA`; vuelve a 0° en los otros estados |

El circuito publica aproximadamente cada segundo en el broker público `broker.hivemq.com:1883`, tema `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`. La app debe usar ese mismo broker y tema, y la simulación debe permanecer encendida para recibir los datos.

La demostración automática selecciona objetivos bajos (78 %), intermedios (19 %) y altos (3 %) y aproxima la lectura gradualmente cada segundo. `VENTILE` corresponde a ADC ≤ 2100; `ESPERE`, a 2101–3099; y `ALERTA`, a ≥ 3100. Estos porcentajes describen la selección de escenarios, no la proporción exacta de segundos en cada estado durante las transiciones. El valor ADC es sintético, no una concentración ambiental ni una medición de PM2.5. LCD, LEDs, servo y mensaje MQTT usan la misma muestra y el mismo estado en cada ciclo. El broker es público y no usa cifrado; úsalo solo para la demostración académica, nunca para datos privados.

## Archivos

- `sketch.ino`: lectura, actualización de LCD y LEDs, monitor serial y envío MQTT.
- `diagram.json`: componentes y conexiones del circuito.
- `libraries.txt`: bibliotecas necesarias en Wokwi.

## Ejecución

1. Abre el enlace de Wokwi y pulsa **Start the simulation**.
2. Observa cómo varían automáticamente el ADC y el estado en LCD, LEDs y monitor serial. La línea superior del LCD identifica la lectura como `SIM`.
3. Abre la app Android y conecta MQTT desde la sección **Dispositivo**.
