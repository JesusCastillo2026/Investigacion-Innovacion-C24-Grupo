package com.tecsup.vigiairelcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Fondo = Color(0xFF071922)
private val Panel = Color(0xFF102B35)
private val Texto = Color(0xFFE7F7F4)
private val Muted = Color(0xFF9AB8BE)
private val Menta = Color(0xFF60E0B2)
private val Verde = Color(0xFF45D49A)
private val Amarillo = Color(0xFFFFC45C)
private val Rojo = Color(0xFFFF7180)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LcdMqtt.connect()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Menta, background = Fondo, surface = Panel, onSurface = Texto)) {
                PantallaLcd()
            }
        }
    }

    override fun onDestroy() {
        LcdMqtt.disconnect()
        super.onDestroy()
    }
}

@Composable
private fun PantallaLcd() {
    val link by LcdMqtt.link.collectAsState()
    val lectura by LcdMqtt.lectura.collectAsState()
    val error by LcdMqtt.error.collectAsState()
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (isActive) { ahora = System.currentTimeMillis(); delay(1_000) }
    }
    val enVivo = link == EstadoLink.OK && lectura != null && ahora - lectura!!.hora < 10_000L
    val colorEstado = lectura?.estado?.let(::colorEstado) ?: Muted
    val valor = lectura?.adc

    Surface(Modifier.fillMaxSize(), color = Fondo) {
        Column(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B2630), Fondo, Color(0xFF09151E))))
                .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("VIGIAIRE", color = Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Panel LCD móvil · señal del circuito", color = Muted, fontSize = 13.sp)
                }
                EstadoConexion(enVivo, link == EstadoLink.CONECTANDO)
            }
            Spacer(Modifier.height(22.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF17434A), Color(0xFF0D2832), Color(0xFF10232D))))
                    .border(1.dp, Color(0xFF3A7777), RoundedCornerShape(30.dp)).padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("LECTURA DEL CIRCUITO", color = Menta, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("MQ-2 · ADC", color = Muted, fontSize = 11.sp)
                }
                Spacer(Modifier.height(12.dp))
                Medidor(valor, lectura?.estado, colorEstado, 246.dp)
                Spacer(Modifier.height(10.dp))
                Text(lectura?.estado ?: "ESPERANDO DATOS", color = colorEstado, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Text(descripcionEstado(lectura?.estado), color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Indicador("SEÑAL ADC", valor?.toString() ?: "—", "escala 0–4095", Modifier.weight(1f))
                Indicador("NIVEL", valor?.let { "${(it * 100 / 4095).coerceIn(0, 100)}%" } ?: "—", "relativo y simulado", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel)
                    .border(1.dp, Color(0xFF244752), RoundedCornerShape(20.dp)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Fila("Conexión", if (enVivo) "Dispositivo conectado" else when(link) {
                    EstadoLink.CONECTANDO -> "Conectando…"
                    EstadoLink.ERROR -> "Error de conexión"
                    else -> "Sin datos del circuito"
                }, if (enVivo) Verde else Amarillo)
                Fila("Canal", "MQTT · VigiAire", Texto)
                Fila("Última actualización", lectura?.let { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it.hora)) } ?: "—", Texto)
            }
            if (!enVivo) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { LcdMqtt.connect() }, enabled = link != EstadoLink.CONECTANDO,
                    colors = ButtonDefaults.buttonColors(containerColor = Menta, contentColor = Fondo),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(if (link == EstadoLink.CONECTANDO) "Conectando…" else "Conectar con Wokwi", fontWeight = FontWeight.Bold) }
            }
            error?.let { Text(it, color = Rojo, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(Modifier.height(14.dp))
            Text(
                "La lectura es una señal analógica simulada del MQ-2 en Wokwi. No representa una medición real de PM2.5 ni de calidad del aire.",
                color = Muted, fontSize = 11.sp, lineHeight = 16.sp, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Medidor(valor: Int?, estado: String?, color: Color, size: Dp) {
    val avance by animateFloatAsState(((valor ?: 0).coerceIn(0, 4095) / 4095f), animationSpec = tween(700), label = "avanceLcd")
    Box(Modifier.size(size).shadow(18.dp, CircleShape, spotColor = color.copy(alpha = .25f)).clip(CircleShape)
        .background(Color(0xFF0A2028)).border(1.dp, Color(0xFF2C5C64), CircleShape), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(13.dp)) {
            val grosor = 13.dp.toPx()
            drawArc(Color(0xFF24434B), 135f, 270f, false, style = Stroke(grosor, cap = StrokeCap.Round))
            drawArc(Brush.sweepGradient(listOf(Menta, color, Menta)), 135f, 270f * avance, false, style = Stroke(grosor, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LECTURA", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Text(valor?.toString() ?: "----", color = Texto, fontSize = 46.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("ADC", color = Menta, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(7.dp))
            Text(if (valor == null) "SIN SEÑAL" else "${valor * 100 / 4095}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Indicador(titulo: String, valor: String, detalle: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Panel).border(1.dp, Color(0xFF244752), RoundedCornerShape(18.dp)).padding(15.dp)) {
        Text(titulo, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(valor, color = Texto, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(detalle, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun EstadoConexion(enVivo: Boolean, conectando: Boolean) {
    val c = if (enVivo) Verde else if (conectando) Amarillo else Rojo
    Row(Modifier.clip(CircleShape).background(c.copy(alpha = .12f)).border(1.dp, c.copy(alpha = .35f), CircleShape).padding(horizontal = 11.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(c))
        Spacer(Modifier.width(7.dp))
        Text(if (enVivo) "EN VIVO" else if (conectando) "CONECTANDO" else "SIN SEÑAL", color = c, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = Muted, fontSize = 12.sp)
        Text(valor, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun colorEstado(estado: String): Color = when (estado.uppercase()) {
    "VENTILE" -> Verde
    "ESPERE" -> Amarillo
    "ALERTA" -> Rojo
    else -> Muted
}

private fun descripcionEstado(estado: String?): String = when (estado?.uppercase()) {
    "VENTILE" -> "Señal baja dentro del umbral configurado."
    "ESPERE" -> "Señal intermedia según los umbrales del simulador."
    "ALERTA" -> "Señal alta según los umbrales del simulador."
    else -> "Al iniciar Wokwi aparecerán aquí la lectura y el estado."
}

private enum class EstadoLink { DESCONECTADO, CONECTANDO, OK, ERROR }
private data class Lectura(val adc: Int, val estado: String, val hora: Long)

private object LcdMqtt {
    private const val BROKER = "tcp://broker.hivemq.com:1883"
    private const val TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry"
    private val _link = MutableStateFlow(EstadoLink.DESCONECTADO)
    val link = _link.asStateFlow()
    private val _lectura = MutableStateFlow<Lectura?>(null)
    val lectura = _lectura.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    @Volatile private var cliente: MqttAsyncClient? = null

    @Synchronized fun connect() {
        if (_link.value == EstadoLink.CONECTANDO || cliente?.isConnected == true) return
        try {
            _error.value = null
            _link.value = EstadoLink.CONECTANDO
            val mqtt = MqttAsyncClient(BROKER, "VigiAireLCD-${UUID.randomUUID()}", MemoryPersistence())
            cliente = mqtt
            mqtt.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    _link.value = EstadoLink.OK
                    mqtt.subscribe(TOPIC, 0, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) { _error.value = null }
                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { _error.value = exception?.message ?: "No se pudo recibir la telemetría." }
                    })
                }
                override fun connectionLost(cause: Throwable?) { _link.value = EstadoLink.DESCONECTADO; _error.value = cause?.message ?: "Se perdió la conexión MQTT." }
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != TOPIC || message == null) return
                    runCatching {
                        val json = JSONObject(String(message.payload, Charsets.UTF_8))
                        require(json.optString("device") == "VigiAire-ESP32" && json.optString("sensor") == "MQ-2")
                        val adc = json.getInt("gasAdc")
                        val status = json.getString("status").uppercase()
                        require(adc in 0..4095 && status in setOf("VENTILE", "ESPERE", "ALERTA"))
                        Lectura(adc, status, System.currentTimeMillis())
                    }.onSuccess { _lectura.value = it }.onFailure { _error.value = "Se recibió un mensaje con formato inesperado." }
                }
                override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
            })
            val options = MqttConnectOptions().apply { isCleanSession = true; isAutomaticReconnect = true; connectionTimeout = 10; keepAliveInterval = 30 }
            mqtt.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) { _link.value = EstadoLink.OK }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { _link.value = EstadoLink.ERROR; _error.value = exception?.message ?: "No se pudo conectar al broker MQTT." }
            })
        } catch (e: Exception) {
            _link.value = EstadoLink.ERROR
            _error.value = e.message ?: "No se pudo iniciar MQTT."
        }
    }

    @Synchronized fun disconnect() {
        val active = cliente
        cliente = null
        _link.value = EstadoLink.DESCONECTADO
        if (active != null) runCatching { if (active.isConnected) active.disconnect(); active.close() }
    }
}
