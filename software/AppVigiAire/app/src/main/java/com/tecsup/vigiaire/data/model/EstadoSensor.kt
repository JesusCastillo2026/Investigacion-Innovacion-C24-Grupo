package com.tecsup.vigiaire.data.model

import androidx.compose.ui.graphics.Color

/** Estados que publica el circuito simulado. No representan categorías de PM2.5. */
enum class EstadoSensor(
    val titulo: String,
    val color: Color,
    val colorFondo: Color,
    val explicacion: String
) {
    VENTILE("VENTILE", Color(0xFF138A55), Color(0xFFE4F5EC), "La señal está dentro del umbral bajo configurado en el simulador."),
    ESPERE("ESPERE", Color(0xFFB77900), Color(0xFFFFF3D2), "La señal superó el umbral bajo; el circuito recomienda esperar."),
    ALERTA("ALERTA", Color(0xFFCE3434), Color(0xFFFFE8E8), "El sensor activó la alerta digital configurada en el circuito."),
    DESCONOCIDO("SIN ESTADO", Color(0xFF64748B), Color(0xFFF1F5F9), "El circuito todavía no ha enviado un estado válido.");

    val recomendacion: String
        get() = when (this) {
            VENTILE -> "El simulador indica VENTILE. Esta es una respuesta del MQ-2 de prueba, no una medición de PM2.5."
            ESPERE -> "El simulador indica ESPERE. La señal relativa del MQ-2 está sobre el umbral configurado."
            ALERTA -> "El simulador indica ALERTA. Revisa la señal y la salida digital del circuito."
            DESCONOCIDO -> "Inicia Wokwi y espera a que llegue una lectura para mostrar el consejo del circuito."
        }

    companion object {
        fun from(raw: String): EstadoSensor = entries.firstOrNull { it.name == raw.uppercase() } ?: DESCONOCIDO
    }
}
