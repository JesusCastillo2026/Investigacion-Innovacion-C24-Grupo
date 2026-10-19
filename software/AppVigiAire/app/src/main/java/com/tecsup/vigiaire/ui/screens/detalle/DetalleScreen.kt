package com.tecsup.vigiaire.ui.screens.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.data.network.MqttConnectionState
import com.tecsup.vigiaire.data.network.MqttVigiAireClient
import com.tecsup.vigiaire.ui.components.MetricCard
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(currentRoute: String, onNavigate: (String) -> Unit) {
    val latest by MqttVigiAireClient.latestReading.collectAsState()
    val history by MqttVigiAireClient.sessionReadings.collectAsState()
    val connection by MqttVigiAireClient.state.collectAsState()
    val live = latest?.takeIf { connection == MqttConnectionState.CONNECTED && System.currentTimeMillis() - it.receivedAt <= MqttVigiAireClient.LIVE_TIMEOUT_MS }
    val min by MqttVigiAireClient.sessionMinimum.collectAsState()
    val max by MqttVigiAireClient.sessionMaximum.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Detalle en vivo", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Señal que publica el ESP32 simulado", fontSize = VigiAireTypeScale.Label, color = TextoGris)
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
            Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Air, contentDescription = null, tint = live?.estado?.color ?: TextoGris)
                    Text(live?.gasAdc?.toString() ?: "—", fontSize = VigiAireTypeScale.Display, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    Text("ADC · escala de 0 a 4095", fontSize = VigiAireTypeScale.Body, color = TextoGris)
                    Text(live?.status ?: "Sin lectura vigente", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = live?.estado?.color ?: TextoGris)
                    Text(live?.let { "Recibido ${formatTime(it.receivedAt)} · mensaje #${it.sequence}" } ?: "Estado de conexión: ${connection.label()}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Mínimo de la sesión", min?.gasAdc?.toString() ?: "—", "ADC", Icons.Default.Air, AzulPrimario, AzulClaro, Modifier.weight(1f))
                MetricCard("Máximo de la sesión", max?.gasAdc?.toString() ?: "—", "ADC", Icons.Default.Router, AzulPrimario, AzulClaro, Modifier.weight(1f))
            }

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = AzulClaro)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AzulPrimario)
                        Text("  Alcance de esta lectura", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    }
                    Text("El MQ-2 del circuito virtual entrega una señal relativa de gases/humo. El valor ADC y el estado provienen del último mensaje MQTT recibido; no son PM2.5, temperatura ni humedad.", fontSize = VigiAireTypeScale.Label, color = TextoOscuro)
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

private fun MqttConnectionState.label(): String = when (this) {
    MqttConnectionState.CONNECTED -> "conectado"
    MqttConnectionState.CONNECTING -> "conectando"
    MqttConnectionState.DISCONNECTED -> "desconectado"
    MqttConnectionState.ERROR -> "error"
}

private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
