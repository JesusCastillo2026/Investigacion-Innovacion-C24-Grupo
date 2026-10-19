# VigiAire: monitoreo ambiental simulado

**Reto:** Visualización de datos ambientales<br>
**Curso:** Investigación e Innovación Tecnológica (sección C24)<br>
**Carrera:** Diseño y Desarrollo de Software

## Acerca del proyecto

VigiAire explora cómo ayudar a una persona a decidir cuándo ventilar una habitación cercana a una avenida con tráfico. El concepto inicial contemplaba un dispositivo físico para medir partículas y orientar esa decisión. Desde la semana 8, el alcance del equipo cambió: el prototipo electrónico y su comunicación con la aplicación se desarrollan y prueban de manera virtual.

El circuito funcional está simulado en Wokwi con un ESP32, una señal de gas/humo, una pantalla LCD y tres LEDs. La aplicación Android recibe por MQTT la lectura analógica simulada y el estado del circuito. Si se muestra una maqueta de cartón en el material del proyecto, esta representa la apariencia de la carcasa; no es un dispositivo electrónico funcional.

## Alcance y límites de las lecturas

En Wokwi usamos el componente **Gas Sensor**, tratado en el programa como MQ-2. Su lectura ADC representa una señal relativa de gas/humo dentro de la simulación. **No es un sensor de aire ni de PM2.5**, no mide partículas finas y sus valores no se pueden interpretar como una concentración ambiental real. Por eso, el prototipo sirve para probar el flujo de datos, los estados, las alertas, la pantalla y la comunicación con la app; no sirve para evaluar la calidad del aire ni dar recomendaciones sanitarias.

Para medir PM2.5 en un proyecto real se necesitaría un sensor óptico de partículas dedicado, además de calibración y validación. El MQ-2 no es equivalente ni reemplaza ese sensor.

## Características del prototipo simulado

- Lee la salida analógica del sensor de gas de Wokwi usando el ADC del ESP32.
- Clasifica la señal con umbrales de demostración y presenta `VENTILE`, `ESPERE` o `ALERTA`.
- Muestra la lectura y el estado en la LCD 16×2 y comunica el estado con LEDs verde, amarillo y rojo.
- Publica los datos por MQTT desde Wokwi; la app Android se suscribe al mismo tema para reflejar la lectura recibida.
- Presenta en la app inicio, detalle de lectura, consejo, equipo/dispositivo, tendencias de la sesión y un dato informativo del día. El dato del día es contenido informativo, no una medición adicional.
- Actualiza la tendencia con los mensajes recibidos durante la sesión de simulación. Los resúmenes describen la sesión de prueba y no un historial ambiental real.

Los umbrales son parámetros demostrativos del programa; los estados no representan límites regulatorios de PM2.5.

## Componentes del circuito en Wokwi

| Componente | Función en la simulación |
| --- | --- |
| ESP32 DevKit v1 | Lee la señal analógica, determina el estado y gestiona la conexión Wi-Fi/MQTT. |
| Wokwi Gas Sensor (señal tipo MQ-2) | Permite variar una señal simulada de gas/humo; no mide PM2.5. |
| LCD 16×2 con interfaz I²C | Muestra el valor ADC y el estado actual. |
| LED verde, amarillo y rojo | Representan visualmente los estados del prototipo. |
| Resistencias de 220 Ω | Limitan la corriente de los LEDs en el esquema simulado. |
| Cables virtuales | Representan alimentación, tierra y conexiones de señal entre componentes. |

El detalle de pines, tema MQTT, formato de mensaje y pasos de ejecución está en [la guía del simulador](software/VigiAireSimulator_Wokwi/README.md).

## Aplicación Android y simulador

La app se encuentra en [`software/AppVigiAire`](software/AppVigiAire/) y está desarrollada en Kotlin con Jetpack Compose. El circuito de demostración está en [`software/VigiAireSimulator_Wokwi`](software/VigiAireSimulator_Wokwi/), con su código y diagrama de Wokwi.

- [Abrir simulador VigiAire en Wokwi](https://wokwi.com/projects/478264195609475073)
- [Guía para ejecutar y conectar la app](software/VigiAireSimulator_Wokwi/README.md)

Para ver lecturas en la app, inicia la simulación y luego conecta la app al broker MQTT y al tema configurado en la guía. El broker de demostración es público y usa una conexión sin cifrado; los mensajes pueden no llegar si la red bloquea MQTT por TCP en el puerto 1883.

## Estructura del repositorio

- [`documentos/`](documentos/): avances, planificación y documentación del proyecto.
- [`hardware/`](hardware/): material de diseño y representación del prototipo.
- [`software/AppVigiAire/`](software/AppVigiAire/): aplicación Android.
- [`software/VigiAireSimulator_Wokwi/`](software/VigiAireSimulator_Wokwi/): circuito y firmware simulados en Wokwi.

## Uso de herramientas de IA

Durante el trabajo se usaron ChatGPT/Codex y Gemini Pro Avanzado como apoyo para organizar y revisar documentación, contrastar los requisitos y avanzar en materiales del proyecto. El equipo revisó las propuestas y definió el contenido final. Las herramientas de IA no sustituyen mediciones reales ni validan el circuito físico.

## Nota sobre la versión actual

La versión del repositorio documenta un prototipo electrónico simulado. Las lecturas de gas ADC son datos de prueba generados por Wokwi y no deben presentarse como datos de PM2.5, PM10 o calidad real del aire.
