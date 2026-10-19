# Pruebas electrónicas de VigiAire

## Objetivo

Como equipo, verificamos en Wokwi que el circuito simulado cambie de estado al variar la señal del sensor, que la LCD y los LEDs representen ese estado y que la app Android reciba por MQTT la misma lectura publicada por el ESP32.

## Alcance de las pruebas

Estas son pruebas virtuales del firmware y del flujo de datos. Usamos el ESP32 DevKit v1, el componente Gas Sensor de Wokwi (señal de gas/humo tratada como MQ-2), la pantalla LCD 16×2, tres LEDs y resistencias de 220 Ω. No son mediciones del aire real ni pruebas de componentes físicos.

La señal ADC es relativa y sirve para comprobar el funcionamiento del prototipo. El MQ-2 simulado no mide PM2.5 ni partículas finas; por lo tanto, los resultados no deben presentarse como niveles reales de contaminación ni como recomendaciones sanitarias.

## Procedimiento

1. Iniciamos el circuito desde el [proyecto de Wokwi](https://wokwi.com/projects/478264195609475073) y esperamos la conexión Wi-Fi y MQTT en el Monitor Serial.
2. Modificamos el control del Gas Sensor para obtener lecturas ADC a ambos lados del umbral demostrativo `GOOD_MAX = 2100`.
3. Para la prueba de alerta, activamos la salida digital del sensor, que el programa interpreta como activa en LOW.
4. En cada caso observamos el valor ADC y el estado en la LCD, el LED correspondiente y el Monitor Serial.
5. Conectamos la app Android al mismo broker y tema MQTT. Comparamos el valor y el estado recibidos en la app con los publicados por Wokwi.
6. Registramos capturas o un video como evidencia. Anotamos el resultado observado; no marcamos una prueba como exitosa sin evidencia.

## Casos de prueba

Los valores indicados son rangos de entrada para orientar la prueba. Registramos el ADC que realmente aparece durante la ejecución. La salida digital activa puede producir `ALERTA` independientemente del valor analógico.

| Caso | Condición de entrada | Resultado esperado | Lectura y estado observados | LCD / LED / app | Evidencia | Resultado |
|---|---|---|---|---|---|---|
| 1. Estado para ventilar | Salida digital inactiva; ADC hasta 2100 (por ejemplo, cerca de 1800) | `VENTILE`; LED verde encendido | Completar durante la prueba | Confirmar coincidencia en LCD, LED, Monitor Serial y app | Añadir captura o indicar minuto del video | Pendiente de registrar |
| 2. Estado de espera | Salida digital inactiva; ADC mayor que 2100 (por ejemplo, cerca de 2500) | `ESPERE`; LED amarillo encendido | Completar durante la prueba | Confirmar coincidencia en LCD, LED, Monitor Serial y app | Añadir captura o indicar minuto del video | Pendiente de registrar |
| 3. Alerta digital | Activar la salida digital DO (LOW) | `ALERTA`; LED rojo encendido | Completar durante la prueba | Confirmar coincidencia en LCD, LED, Monitor Serial y app | Añadir captura o indicar minuto del video | Pendiente de registrar |
| 4. Actualización por MQTT | Mantener Wokwi activo y conectar la app al broker y tema configurados | La app muestra el mismo `gasAdc` y estado que publica el circuito | Completar durante la prueba | Registrar valor y estado simultáneos de Wokwi y Android | Añadir captura o indicar minuto del video | Pendiente de registrar |

## Evidencias que debemos adjuntar

- Captura del estado `VENTILE` con lectura, LCD, LED verde y app.
- Captura del estado `ESPERE` con lectura, LCD, LED amarillo y app.
- Captura del estado `ALERTA` con lectura, LCD, LED rojo y app.
- Captura del Monitor Serial donde se vea la publicación MQTT y su lectura.
- Video breve que muestre el cambio de entrada y la respuesta de la simulación y la app.

## Observaciones y mejoras

**Problemas encontrados:** completar con los errores que observamos durante las pruebas.  
**Ajustes realizados:** describir los cambios aplicados al cableado virtual, al firmware o a la app.  
**Oportunidades de mejora:** indicar qué revisaríamos en una siguiente versión, como validación con un sensor óptico PM2.5 real y calibración del sistema.

## Link del video

**Video de las pruebas electrónicas:** [Pegar aquí el enlace compartido]

## Conclusión

Completar después de ejecutar los casos de prueba. Resumir cuáles funcionaron, cuáles requieren ajustes y qué evidencias respaldan los resultados. Aclarar que el circuito probado es una simulación y que el Gas Sensor no mide PM2.5.
