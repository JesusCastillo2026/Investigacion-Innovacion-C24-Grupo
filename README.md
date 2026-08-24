# VigiAire: Estación de Monitoreo y Alerta por Horarios de Tráfico 🍃

**Reto 1:** Visualización de Datos Ambientales
**Curso:** Investigación e Innovación Tecnológica (Sección C24)
**Carrera:** Diseño y Desarrollo de Software

## 📝 Descripción del Proyecto
VigiAire es un dispositivo diseñado para instalarse en la ventana o balcón de una vivienda cercana a avenidas de alto tráfico. El sistema mide continuamente los niveles de PM2.5 y PM10, cruza esa lectura con los horarios pico de tráfico registrados (mediante un reloj RTC) y muestra en una pantalla LCD una recomendación clara: "ventilar ahora" o "esperar".

Este proyecto nace para resolver el problema de los vecinos que carecen de datos en tiempo real y suelen abrir sus ventanas en horas de alta toxicidad sin darse cuenta.

## ⚙️ Características Principales
* **Lectura por horarios pico:** Registra el material particulado y lo cruza con las horas de mayor tráfico de microbuses.
* **Alerta Inteligente:** Indica de forma directa si es buen momento para ventilar.
* **Señalización Visual Fácil:** Uso de un LED indicador para reforzar visualmente el nivel de contaminación, evitando que el usuario tenga que interpretar cifras técnicas.

## 🛠️ Hardware y Componentes
El prototipo está diseñado para ser accesible, replicable y estar contenido en una caja de madera MDF:
* **Microcontrolador:** Arduino Uno.
* **Sensores:** Sensor de partículas (PM2.5/PM10) y Sensor DHT11 (Temperatura/Humedad).
* **Módulos:** Módulo RTC (Reloj en tiempo real) y Pantalla LCD 16x2.
* **Indicadores:** LED de estado (verde/amarillo/rojo).

## 📂 Estructura del Repositorio
* `/documentos`: Contiene los avances semanales, diagramas, esquemas e infografías del proyecto.
* `/hardware`: Esquemas de conexiones y distribución de los componentes electrónicos.
* `/software`: Código fuente en C++ (sketch) para procesar las lecturas en el Arduino Uno.

## 🤖 Uso de IA en el proceso
En la fase de ideación y diseño se utilizaron las siguientes herramientas:
* **Claude (Anthropic):** Apoyo para organizar la lluvia de ideas, redactar insights y seleccionar la propuesta.
* **Google Gemini:** Generación de la representación visual técnica del prototipo (boceto, esquema de conexiones y vistas interna/externa) manteniendo las características definidas por el equipo.
