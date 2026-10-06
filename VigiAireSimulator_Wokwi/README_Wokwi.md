# VigiAire en Wokwi

Prototipo virtual inicial del módulo VigiAire. Simula una entrada variable, presenta el valor en una LCD 16×2 y activa un indicador LED según el rango.

## Archivos

- `sketch.ino`: programa C++ para Arduino Uno.
- `diagram.json`: conexiones del Arduino Uno, potenciómetro, LCD 16×2 y LEDs con resistencias.
- `libraries.txt`: biblioteca requerida (`LiquidCrystal`).

## Abrir y ejecutar en Wokwi

1. Abre [un proyecto nuevo de Arduino Uno](https://wokwi.com/projects/new/arduino-uno).
2. Copia el contenido de `sketch.ino` y reemplaza el del editor.
3. Reemplaza el contenido de `diagram.json` con el diagrama de esta carpeta.
4. En Library Manager instala `LiquidCrystal`.
5. Inicia la simulación y abre el Monitor Serial. Mueve el potenciómetro para variar la lectura.

## Cómo funciona

El potenciómetro conectado a A0 representa una señal de lectura, que el programa convierte a un rango ilustrativo de 5 a 80 µg/m³. La LCD y el Monitor Serial muestran el valor y la recomendación; se enciende el LED verde, amarillo o rojo según los umbrales de demostración de 15 y 35 µg/m³.

## Alcance

Esta simulación no mide partículas PM2.5 reales: Wokwi usa el potenciómetro como entrada de prueba. Los umbrales son únicamente demostrativos y no representan una recomendación sanitaria. Para medir PM2.5 se requiere un sensor óptico físico y calibración. No incluye conexión con una app móvil.

El proyecto fuente está preparado para importarlo en Wokwi. Aún no tiene un enlace permanente de simulación guardado en una cuenta de Wokwi.

## Referencias

- [Arduino Uno en Wokwi](https://docs.wokwi.com/parts/wokwi-arduino-uno)
- [Potenciómetro en Wokwi](https://docs.wokwi.com/parts/wokwi-potentiometer)
- [LCD1602 en Wokwi](https://docs.wokwi.com/parts/wokwi-lcd1602)
- [Formato de diagramas Wokwi](https://docs.wokwi.com/diagram-format)
