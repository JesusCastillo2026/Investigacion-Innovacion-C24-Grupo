# VigiAire en Wokwi

**[Abrir la simulación interactiva de VigiAire](https://wokwi.com/projects/477170557858030593)**

Prototipo virtual inicial del módulo VigiAire. Usa un sensor MQ-2 de Wokwi para variar una señal de gas/humo, muestra su lectura analógica relativa en una LCD 16×2 y activa un indicador LED según el nivel configurado.

## Archivos

- `sketch.ino`: programa C++ para Arduino Uno.
- `diagram.json`: conexiones del Arduino Uno, sensor MQ-2, LCD 16×2 y LEDs con resistencias.
- `libraries.txt`: biblioteca requerida (`LiquidCrystal`).

## Probar la simulación

1. Abre la [simulación en Wokwi](https://wokwi.com/projects/477170557858030593).
2. Inicia la simulación.
3. Selecciona el sensor MQ-2 y ajusta el control **GAS (PPM)** para cambiar el escenario.
4. Observa la lectura analógica relativa, la recomendación en la LCD y el estado de los LEDs. También puedes consultar el Monitor Serial.

## Alcance de la lectura

El MQ-2 permite simular la detección de gases combustibles y humo. La lectura mostrada por este prototipo es una señal relativa del sensor; **no mide PM2.5 ni determina la calidad ambiental real**. Los umbrales son ilustrativos y no constituyen una recomendación sanitaria. Para medir PM2.5 se necesita un sensor óptico de partículas y calibración. Esta versión no incluye conexión con una app móvil.

## Referencias

- [Sensor de gas MQ-2 en Wokwi](https://docs.wokwi.com/parts/wokwi-gas-sensor)
- [Arduino Uno en Wokwi](https://docs.wokwi.com/parts/wokwi-arduino-uno)
- [LCD1602 en Wokwi](https://docs.wokwi.com/parts/wokwi-lcd1602)
- [Formato de diagramas Wokwi](https://docs.wokwi.com/diagram-format)
