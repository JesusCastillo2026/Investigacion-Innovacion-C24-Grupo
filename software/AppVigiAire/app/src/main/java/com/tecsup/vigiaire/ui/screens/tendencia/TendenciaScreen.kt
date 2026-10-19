package com.tecsup.vigiaire.ui.screens.tendencia

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.tecsup.vigiaire.ui.components.BarChartWidget
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TendenciaScreen(currentRoute: String, onNavigate: (String) -> Unit) {
    val history by MqttVigiAireClient.sessionReadings.collectAsState()
    val latest by MqttVigiAireClient.latestReading.collectAsState()
    val connection by MqttVigiAireClient.state.collectAsState()
    val sessionNumber by MqttVigiAireClient.sessionNumber.collectAsState()
    val readingCount by MqttVigiAireClient.sessionReadingCount.collectAsState()
    val minimumReading by MqttVigiAireClient.sessionMinimum.collectAsState()
    val maximumReading by MqttVigiAireClient.sessionMaximum.collectAsState()
    val sessionAverage by MqttVigiAireClient.sessionAverage.collectAsState()
    val points = history.takeLast(24)
    val live = latest?.takeIf { connection == MqttConnectionState.CONNECTED && System.currentTimeMillis() - it.receivedAt <= MqttVigiAireClient.LIVE_TIMEOUT_MS }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tendencia en vivo", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Sesión de prueba $sessionNumber · MQ-2", fontSize = VigiAireTypeScale.Label, color = TextoGris)
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Señal · últimas ${points.size} de $readingCount", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                EstadoPill(if (live != null) "EN VIVO" else "SIN SEÑAL")
            }
            BarChartWidget(datos = points)

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AzulPrimario)
                    Column(Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = live?.let { "Ahora: ${it.gasAdc} ADC · ${it.status}" } ?: "Esperando lectura MQTT",
                            fontSize = VigiAireTypeScale.Body,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text("Cada barra corresponde a un mensaje recibido desde Wokwi.", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    }
                }
            }

            Text("Resumen de esta sesión", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Mínimo", minimumReading?.gasAdc?.toString() ?: "—", Modifier.weight(1f))
                StatCard("Promedio", sessionAverage?.toString() ?: "—", Modifier.weight(1f))
                StatCard("Máximo", maximumReading?.gasAdc?.toString() ?: "—", Modifier.weight(1f))
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = AzulClaro)) {
                Text(
                    "El gráfico se actualiza al recibir mensajes. Al detectar que el ESP32 reinició (su tiempo de actividad vuelve a cero), se limpia la serie y empieza un nuevo día de prueba. Los datos se guardan en memoria mientras la app siga abierta.",
                    Modifier.padding(16.dp), fontSize = VigiAireTypeScale.Label, color = TextoOscuro
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EstadoPill(texto: String) {
    androidx.compose.foundation.layout.Box(Modifier.background(AzulClaro, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp)) {
        Text(texto, fontSize = VigiAireTypeScale.Caption, fontWeight = FontWeight.Bold, color = AzulPrimario)
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = VigiAireTypeScale.Caption, color = TextoGris)
            Text(value, fontSize = VigiAireTypeScale.Heading, fontWeight = FontWeight.Bold, color = TextoOscuro)
            Text("ADC", fontSize = VigiAireTypeScale.Caption, color = TextoGris)
        }
    }
}
