package com.tecsup.vigiaire.ui.screens.dispositivo

import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.data.network.MqttConnectionState
import com.tecsup.vigiaire.data.network.MqttVigiAireClient
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulOscuro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.RojoAlerta
import com.tecsup.vigiaire.ui.theme.RojoSuave
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VerdeExito
import com.tecsup.vigiaire.ui.theme.VerdeSuave
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispositivoScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val connectionState by MqttVigiAireClient.state.collectAsState()
    val reading by MqttVigiAireClient.latestReading.collectAsState()
    val error by MqttVigiAireClient.error.collectAsState()
    val connected = connectionState == MqttConnectionState.CONNECTED

    val stateColor = when (connectionState) {
        MqttConnectionState.CONNECTED -> VerdeExito
        MqttConnectionState.CONNECTING -> AzulPrimario
        MqttConnectionState.ERROR -> RojoAlerta
        MqttConnectionState.DISCONNECTED -> TextoGris
    }
    val stateBackground = if (connected) VerdeSuave else RojoSuave
    val stateText = when (connectionState) {
        MqttConnectionState.CONNECTED -> "Conectado al simulador"
        MqttConnectionState.CONNECTING -> "Conectando al broker..."
        MqttConnectionState.ERROR -> "No se pudo conectar"
        MqttConnectionState.DISCONNECTED -> "Sin conexión"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VigiAire simulado", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Datos en vivo desde Wokwi", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            VigiAireBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = FondoGris
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Estado de MQTT",
                            tint = stateColor,
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(stateBackground).padding(5.dp)
                        )
                        Column {
                            Text(stateText, fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = stateColor)
                            Text("ESP32 Wokwi · MQTT", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                        }
                    }

                    Button(
                        onClick = {
                            if (connected || connectionState == MqttConnectionState.CONNECTING) {
                                MqttVigiAireClient.disconnect()
                            } else {
                                MqttVigiAireClient.connect()
                            }
                        },
                        enabled = connectionState != MqttConnectionState.CONNECTING,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario, contentColor = Blanco)
                    ) {
                        if (connectionState == MqttConnectionState.CONNECTING) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Blanco, strokeWidth = 2.dp)
                            Text("  Conectando...", fontWeight = FontWeight.Bold)
                        } else {
                            Text(if (connected) "Desconectar" else "Conectar MQTT", fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("Broker: ${MqttVigiAireClient.BROKER}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    Text("Tema: ${MqttVigiAireClient.TOPIC}", fontSize = VigiAireTypeScale.Caption, color = TextoGris)
                    error?.let { Text("Detalle: $it", fontSize = VigiAireTypeScale.Label, color = RojoAlerta) }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Lectura recibida", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    if (reading == null) {
                        Text(
                            text = "Aún no llega una lectura. Inicia la simulación en Wokwi y deja esta pantalla conectada.",
                            fontSize = VigiAireTypeScale.Body,
                            color = TextoGris,
                            lineHeight = 20.sp
                        )
                    } else {
                        val value = reading!!
                        Text("${value.gasAdc}", fontSize = VigiAireTypeScale.Metric, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("señal relativa MQ-2 · ADC 0–4095", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                        Text("Estado del circuito: ${value.status}", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.SemiBold, color = AzulOscuro)
                        Text("Mensaje #${value.sequence} · ${formatTime(value.receivedAt)}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaro)
            ) {
                Text(
                    text = "Esta conexión muestra la señal simulada del MQ-2 para gases/humo. No es una lectura de PM2.5 ni una medición real de calidad del aire.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = VigiAireTypeScale.Label,
                    color = AzulOscuro,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
