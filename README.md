Reto 1: Visualización de Datos Ambientales 
Curso: Investigación e Innovación Tecnológica (Sección C24)
Carrera: Diseño y Desarrollo de Software


Descripción del Proyecto:
-  Este repositorio contiene el código fuente y la documentación para la arquitectura de un sistema de monitoreo de calidad del aire. El proyecto nace como respuesta a los niveles críticos      de partículas tóxicas (PM2.5) generados por el alto tráfico de microbuses antiguos en Lima Metropolitana.

-  El objetivo principal es capturar, procesar y visualizar los niveles de toxicidad ambiental en tiempo real, brindando a los vecinos de zonas altamente transitadas una herramienta             intuitiva para gestionar la ventilación de sus hogares de manera segura.


Arquitectura del Sistema:
-  El sistema emplea una arquitectura dual (local y remota):

-  Monitoreo Local (Hardware): Un módulo físico instalado cerca de la ventana que utiliza sensores para medir el nivel de partículas. Los datos procesados activan una alerta visual tipo         "semáforo" (LEDs: Verde, Amarillo, Rojo) para una rápida interpretación del usuario.

-  Monitoreo Remoto (Software): Transmisión de los datos capturados hacia un dashboard web/app, permitiendo a los usuarios visualizar las métricas y el estado del aire desde sus dispositivos    móviles.

Tecnologías a utilizar
-  Hardware / Simulación: Arduino / ESP32, Sensores de calidad de aire (ej. MQ-135), LEDs. Modelado inicial en Tinkercad.

-  Software: C++ (para la lógica del microcontrolador), Tecnologías Web (HTML, CSS, JS) para el dashboard remoto.
