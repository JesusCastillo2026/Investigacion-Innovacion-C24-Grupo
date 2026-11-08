# Circuito simulado VigiAire en Wokwi

**Abrir el circuito:** [VigiAire ESP32 + MQTT en Wokwi](https://wokwi.com/projects/478264195609475073)

Esta carpeta reúne el firmware y el diagrama del circuito electrónico simulado. Wokwi representa el ESP32, el sensor de gas, la pantalla LCD, los LEDs y su cableado. La app Android permanece en [software/AppVigiAire](../../software/AppVigiAire/) y recibe las lecturas publicadas mediante MQTT.

## Componentes simulados

- ESP32 DevKit v1: genera la secuencia ADC de demostración, determina el estado y gestiona Wi-Fi y MQTT.
- Gas Sensor de Wokwi, usado como señal tipo MQ-2: representa una lectura relativa de gas/humo; no mide PM2.5.
- LCD 16×2 I²C: muestra la lectura ADC y el estado.
- LEDs verde, amarillo y rojo con resistencias de 220 Ω conectadas en serie entre el GPIO de control y el ánodo (A) de cada LED; el cátodo (C) va a GND. Limitan la corriente para proteger los LEDs y los GPIO.
- Motor paso a paso bipolar NEMA 17 con controlador A4988: al recibir la orden de la app, gira continuamente en un solo sentido; al detener la orden, deja de recibir pasos y se deshabilita. Es una representación visual en Wokwi, no genera succión ni mueve aire.
- Controlador A4988: recibe STEP, DIR y ENABLE del ESP32; RESET y SLEEP se mantienen habilitados juntos. El modo de paso completo usa los pines MS1/MS2/MS3 en bajo.
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
| STEP del controlador A4988 | GPIO18 | Pulsos que hacen girar el motor paso a paso mientras la app activa la función |
| DIR del controlador A4988 | GPIO19 | Sentido fijo de giro |
| ENABLE del controlador A4988 | GPIO23 | Activo en bajo; habilita el motor cuando la app activa la función |
| VDD del A4988 | 3V3 | Alimentación lógica |
| GND del A4988 | GND | Tierra común con ESP32 |

El circuito publica aproximadamente cada segundo en el broker público `broker.hivemq.com:1883`, tema `vigiaire/c24d/grupo4/9f3c7d8b/telemetry`. Para controlar la animación, el ESP32 también escucha el tema `vigiaire/c24d/grupo4/9f3c7d8b/command`; la app publica allí las órdenes de iniciar o detener. La app y Wokwi deben permanecer conectados al broker.

La demostración automática selecciona objetivos bajos (78 %), intermedios (19 %) y altos (3 %) y aproxima la lectura gradualmente cada segundo. `VENTILE` corresponde a ADC ≤ 2100; `ESPERE`, a 2101–3099; y `ALERTA`, a ≥ 3100. Estos porcentajes describen la selección de escenarios, no la proporción exacta de segundos en cada estado durante las transiciones. El valor ADC es sintético, no una concentración ambiental ni una medición de PM2.5. LCD, LEDs y mensaje MQTT usan la misma muestra y el mismo estado en cada ciclo; el motor paso a paso se controla por separado desde la app. El broker es público y no usa cifrado; úsalo solo para la demostración académica, nunca para datos privados.

## Archivos

- `sketch.ino`: lectura, actualización de LCD y LEDs, monitor serial y envío MQTT.
- `diagram.json`: componentes y conexiones del circuito.
- `libraries.txt`: bibliotecas necesarias en Wokwi.

## Ejecución

1. Abre el enlace de Wokwi y pulsa **Start the simulation**.
2. Observa cómo varían automáticamente el ADC y el estado en LCD, LEDs y monitor serial. La línea superior del LCD identifica la lectura como `SIM`.
3. Abre la app Android y conecta MQTT desde la sección **Equipo**.
4. En **Equipo**, pulsa **Iniciar extracción de aire** para activar la rotación simulada del motor paso a paso; pulsa **Detener extracción de aire** para detenerlo. El estado recibido por MQTT confirma la función.

El motor paso a paso y el controlador A4988 son elementos virtuales del simulador. La animación representa un mecanismo giratorio, pero no es una extracción efectiva ni evidencia de caudal o alcance de ventilación.




