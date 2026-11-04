package com.tecsup.vigiaire.data.model

import androidx.compose.ui.graphics.Color

/** Estados que publica el circuito simulado. No representan categorías de PM2.5. */
enum class EstadoSensor(
    val titulo: String,
    val color: Color,
    val colorFondo: Color,
    val explicacion: String
) {
    VENTILE("VENTILE", Color(0xFF138A55), Color(0xFFE4F5EC), "La lectura ADC simulada está dentro del umbral bajo configurado."),
    ESPERE("ESPERE", Color(0xFFB77900), Color(0xFFFFF3D2), "La lectura ADC simulada superó el umbral bajo configurado."),
    ALERTA("ALERTA", Color(0xFFCE3434), Color(0xFFFFE8E8), "La lectura ADC simulada superó el umbral alto configurado en Wokwi."),
    DESCONOCIDO("SIN ESTADO", Color(0xFF64748B), Color(0xFFF1F5F9), "El circuito todavía no ha enviado un estado válido.");

    val recomendacion: String
        get() = when (this) {
            VENTILE -> "El simulador indica VENTILE. Esta es una respuesta del MQ-2 de prueba, no una medición de PM2.5."
            ESPERE -> "El simulador indica ESPERE. La lectura ADC de prueba está sobre el umbral bajo."
            ALERTA -> "El simulador indica ALERTA. La lectura ADC de prueba alcanzó el umbral alto."
            DESCONOCIDO -> "Inicia Wokwi y espera a que llegue una lectura para mostrar el consejo del circuito."
        }

    companion object {
        fun from(raw: String): EstadoSensor = entries.firstOrNull { it.name == raw.uppercase() } ?: DESCONOCIDO
    }
}
