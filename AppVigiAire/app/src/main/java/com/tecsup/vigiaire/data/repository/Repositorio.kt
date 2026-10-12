package com.tecsup.vigiaire.data.repository

import com.tecsup.vigiaire.data.model.DatoHistoricoBarra
import com.tecsup.vigiaire.data.model.LecturaAire
import com.tecsup.vigiaire.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round
import kotlin.random.Random

object Repositorio {

    // Usuario activo (Demo: Jeremy)
    private val _usuarioActual = MutableStateFlow<Usuario?>(
        Usuario(email = "demo@vigiaire.com", nombre = "Jeremy")
    )
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Estado de conexión del dispositivo físico simulado
    private val _dispositivoConectado = MutableStateFlow(true)
    val dispositivoConectado: StateFlow<Boolean> = _dispositivoConectado.asStateFlow()

    // Prueba de LED indicador
    private val _ledPruebaActivo = MutableStateFlow(false)
    val ledPruebaActivo: StateFlow<Boolean> = _ledPruebaActivo.asStateFlow()

    // Lista de lecturas en tiempo real
    private val _lecturas = MutableStateFlow<List<LecturaAire>>(emptyList())
    val lecturas: StateFlow<List<LecturaAire>> = _lecturas.asStateFlow()

    // Histórico de las últimas 12 horas para el gráfico
    private val _historico12Horas = MutableStateFlow<List<DatoHistoricoBarra>>(emptyList())
    val historico12Horas: StateFlow<List<DatoHistoricoBarra>> = _historico12Horas.asStateFlow()

    init {
        // Inicializar histórico de 12 horas
        generarHistoricoInicial()
        // Generar lectura de arranque
        generarNuevaLectura()
    }

    fun autenticar(correo: String, clave: String): Boolean {
        return if (correo.trim().equals("demo@vigiaire.com", ignoreCase = true) && clave == "123456") {
            _usuarioActual.value = Usuario(
                email = "demo@vigiaire.com",
                nombre = "Jeremy"
            )
            true
        } else {
            false
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun alternarConexionDispositivo() {
        _dispositivoConectado.value = !_dispositivoConectado.value
    }

    fun activarPruebaLed() {
        _ledPruebaActivo.value = true
    }

    fun desactivarPruebaLed() {
        _ledPruebaActivo.value = false
    }

    fun generarNuevaLectura(): LecturaAire {
        val pm25Raw = Random.nextDouble(10.0, 120.0)
        val pm25 = round(pm25Raw * 10) / 10.0

        val pm10Raw = (pm25 * 1.8).coerceIn(15.0, 220.0)
        val pm10 = round(pm10Raw * 10) / 10.0

        val tempRaw = Random.nextDouble(15.0, 28.0)
        val temperatura = round(tempRaw * 10) / 10.0

        val humRaw = Random.nextDouble(40.0, 90.0)
        val humedad = round(humRaw * 10) / 10.0

        val nuevaLectura = LecturaAire(
            id = "MOD-VIGI-${System.currentTimeMillis() % 10000}",
            pm25 = pm25,
            pm10 = pm10,
            temperatura = temperatura,
            humedad = humedad,
            timestamp = System.currentTimeMillis()
        )

        // Mantiene máximo 20 registros en memoria, el más reciente primero
        val listaActualizada = listOf(nuevaLectura) + _lecturas.value
        _lecturas.value = listaActualizada.take(20)

        // Actualizar último punto del gráfico de tendencia
        actualizarUltimoPuntoHistorico(pm25)

        return nuevaLectura
    }

    fun obtenerUltimaLectura(): LecturaAire {
        return _lecturas.value.firstOrNull() ?: generarNuevaLectura()
    }

    private fun generarHistoricoInicial() {
        // Simulación de 12 horas (Hora punta de tráfico a las 8-10 AM)
        val horas = listOf("01h", "02h", "03h", "04h", "05h", "06h", "07h", "08h (Punta)", "09h", "10h", "11h", "12h (Ahora)")
        val valores = listOf(18.0, 15.0, 22.0, 28.0, 32.0, 45.0, 68.0, 92.0, 85.0, 54.0, 38.0, 28.0)

        val lista = horas.zip(valores).map { (hora, valPm) ->
            DatoHistoricoBarra(
                horaLabel = hora,
                pm25 = valPm
            )
        }
        _historico12Horas.value = lista
    }

    private fun actualizarUltimoPuntoHistorico(nuevoPm25: Double) {
        val listaActual = _historico12Horas.value.toMutableList()
        if (listaActual.isNotEmpty()) {
            val ultimoIndice = listaActual.lastIndex
            val ultimoElemento = listaActual[ultimoIndice]
            listaActual[ultimoIndice] = ultimoElemento.copy(pm25 = nuevoPm25)
            _historico12Horas.value = listaActual
        }
    }
}
