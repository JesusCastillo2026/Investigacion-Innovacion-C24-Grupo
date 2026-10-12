package com.tecsup.vigiaire.data.model

import androidx.compose.ui.graphics.Color
import com.tecsup.vigiaire.ui.theme.AmarilloAlerta
import com.tecsup.vigiaire.ui.theme.AmarilloSuave
import com.tecsup.vigiaire.ui.theme.RojoAlerta
import com.tecsup.vigiaire.ui.theme.RojoSuave
import com.tecsup.vigiaire.ui.theme.VerdeExito
import com.tecsup.vigiaire.ui.theme.VerdeSuave
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class EstadoCalidad(
    val titulo: String,
    val color: Color,
    val colorFondo: Color,
    val recomendacionHome: String,
    val recomendacionVentilacion: String
) {
    BUENA(
        titulo = "BUENA",
        color = VerdeExito,
        colorFondo = VerdeSuave,
        recomendacionHome = "Puedes ventilar tu hogar durante 15 min.",
        recomendacionVentilacion = "Ventila ahora. El aire está limpio."
    ),
    MODERADA(
        titulo = "MODERADA",
        color = AmarilloAlerta,
        colorFondo = AmarilloSuave,
        recomendacionHome = "Reduce el tiempo al aire libre.",
        recomendacionVentilacion = "Ventila con precaución."
    ),
    MALA(
        titulo = "MALA",
        color = RojoAlerta,
        colorFondo = RojoSuave,
        recomendacionHome = "Espera para ventilar. Mantén la ventana cerrada.",
        recomendacionVentilacion = "Espera para ventilar. Reduce el tiempo al aire libre."
    )
}

data class LecturaAire(
    val id: String,
    val pm25: Double,
    val pm10: Double,
    val temperatura: Double,
    val humedad: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val estado: EstadoCalidad = calcularEstado(pm25)
) {
    val aqiCalculado: Int
        get() = (pm25 * 1.5).toInt().coerceIn(10, 300)

    val horaFormateada: String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    val horaCorta: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    companion object {
        fun calcularEstado(pm25: Double): EstadoCalidad {
            return when {
                pm25 < 35.0 -> EstadoCalidad.BUENA
                pm25 < 75.0 -> EstadoCalidad.MODERADA
                else -> EstadoCalidad.MALA
            }
        }
    }
}

data class DatoHistoricoBarra(
    val horaLabel: String,
    val pm25: Double,
    val estado: EstadoCalidad = LecturaAire.calcularEstado(pm25)
)
