package com.tecsup.vigiaire.ui.screens.noticias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.data.network.MqttConnectionState
import com.tecsup.vigiaire.data.network.MqttGasReading
import com.tecsup.vigiaire.data.network.MqttVigiAireClient
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulOscuro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class Novedad(
    val timestamp: Long,
    val titulo: String,
    val descripcion: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticiasScreen(currentRoute: String, onNavigate: (String) -> Unit) {
    val history by MqttVigiAireClient.sessionReadings.collectAsState()
    val latest by MqttVigiAireClient.latestReading.collectAsState()
    val connection by MqttVigiAireClient.state.collectAsState()
    val sessionNumber by MqttVigiAireClient.sessionNumber.collectAsState()
    val startedAt by MqttVigiAireClient.sessionStartedAt.collectAsState()
    val readingCount by MqttVigiAireClient.sessionReadingCount.collectAsState()
    val min by MqttVigiAireClient.sessionMinimum.collectAsState()
    val max by MqttVigiAireClient.sessionMaximum.collectAsState()
    val transitions = history.zipWithNext().filter { (a, b) -> a.status != b.status }
    val news = remember(history, min, max, readingCount) { buildNews(history, min, max, readingCount) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Noticias", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Novedades de la sesión en vivo", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco),
                modifier = Modifier.shadow(3.dp)
            )
        },
        bottomBar = { VigiAireBottomBar(currentRoute, onNavigate) },
        containerColor = FondoGris
    ) { insets ->
        Column(
            Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = AzulClaro)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Air, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(28.dp))
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("Día de prueba $sessionNumber", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        Text(
                            text = if (startedAt == null) "Esperando el primer dato de Wokwi" else "Sesión iniciada ${formatDateTime(startedAt!!)}",
                            fontSize = VigiAireTypeScale.Label,
                            color = AzulOscuro
                        )
                        Text("Si el ESP32 se reinicia, comienza un nuevo día de prueba.", fontSize = VigiAireTypeScale.Label, color = AzulOscuro)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ResumenDato("Recibidas", readingCount.toString(), "mensajes", Modifier.weight(1f))
                ResumenDato("Mínima", min?.gasAdc?.toString() ?: "—", "ADC", Modifier.weight(1f))
                ResumenDato("Máxima", max?.gasAdc?.toString() ?: "—", "ADC", Modifier.weight(1f))
            }

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Router, contentDescription = null, tint = if (connection == MqttConnectionState.CONNECTED) Color(0xFF138A55) else TextoGris)
                        Text(
                            text = if (connection == MqttConnectionState.CONNECTED) "  Circuito conectado" else "  Sin conexión en vivo",
                            fontSize = VigiAireTypeScale.Body,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }
                    Text("Último dato: ${latest?.let { "${it.gasAdc} ADC · ${it.status} · ${formatTime(it.receivedAt)}" } ?: "todavía no recibido"}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    Text("MQ-2 entrega una señal relativa de gases/humo; no mide PM2.5.", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                }
            }

            Text("Actividad de esta sesión", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
            if (news.isEmpty()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulPrimario)
                        Text("Aún no hay novedades", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Inicia Wokwi. La app agregará lecturas, cambios de estado y los valores mínimo y máximo recibidos.", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    }
                }
            } else {
                news.forEach { item ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
                            Box(Modifier.padding(top = 4.dp).size(10.dp).clip(CircleShape).background(item.color))
                            Column(Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.titulo, fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(item.descripcion, fontSize = VigiAireTypeScale.Label, color = TextoGris)
                                Text(formatDateTime(item.timestamp), fontSize = VigiAireTypeScale.Caption, color = AzulPrimario)
                            }
                        }
                    }
                }
            }

            if (transitions.isNotEmpty()) {
                Text("Cambios de estado registrados: ${transitions.size}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
            }
        }
    }
}

@Composable
private fun ResumenDato(titulo: String, valor: String, unidad: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, fontSize = VigiAireTypeScale.Caption, color = TextoGris)
            Text(valor, fontSize = VigiAireTypeScale.Heading, fontWeight = FontWeight.Bold, color = TextoOscuro)
            Text(unidad, fontSize = VigiAireTypeScale.Caption, color = TextoGris)
        }
    }
}

private fun buildNews(
    readings: List<MqttGasReading>,
    sessionMinimum: MqttGasReading?,
    sessionMaximum: MqttGasReading?,
    readingCount: Long
): List<Novedad> {
    if (readings.isEmpty()) return emptyList()
    val events = mutableListOf<Novedad>()
    val first = readings.first()
    val last = readings.last()
    events += Novedad(first.receivedAt, "Comenzó el monitoreo", "Primera señal recibida: ${first.gasAdc} ADC (${first.status}).", AzulPrimario)
    readings.zipWithNext().filter { (a, b) -> a.status != b.status }.takeLast(12).forEach { (_, current) ->
        events += Novedad(current.receivedAt, "Cambió el estado a ${current.status}", "La señal del MQ-2 llegó a ${current.gasAdc} ADC.", current.estado.color)
    }
    sessionMinimum?.let { events += Novedad(it.receivedAt, "Señal mínima de la sesión", "El menor valor recibido fue ${it.gasAdc} ADC.", Color(0xFF138A55)) }
    sessionMaximum?.let { events += Novedad(it.receivedAt, "Señal máxima de la sesión", "El mayor valor recibido fue ${it.gasAdc} ADC.", Color(0xFFCE3434)) }
    events += Novedad(last.receivedAt, "Mensajes recibidos", "La sesión acumula $readingCount lecturas MQTT.", AzulPrimario)
    if (last.receivedAt != first.receivedAt) {
        events += Novedad(last.receivedAt, "Lectura más reciente", "${last.gasAdc} ADC · estado ${last.status}.", last.estado.color)
    }
    return events.distinctBy { Triple(it.titulo, it.timestamp, it.descripcion) }.sortedByDescending { it.timestamp }
}

private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
private fun formatDateTime(timestamp: Long): String = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
