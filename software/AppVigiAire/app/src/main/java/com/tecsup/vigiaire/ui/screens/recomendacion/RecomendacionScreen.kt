package com.tecsup.vigiaire.ui.screens.recomendacion

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.tecsup.vigiaire.navigation.Pantalla
import com.tecsup.vigiaire.ui.components.RecommendationCard
import com.tecsup.vigiaire.ui.components.TrafficLightWidget
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
fun RecomendacionScreen(currentRoute: String, onNavigate: (String) -> Unit) {
    val latest by MqttVigiAireClient.latestReading.collectAsState()
    val connection by MqttVigiAireClient.state.collectAsState()
    val live = latest?.takeIf { connection == MqttConnectionState.CONNECTED && System.currentTimeMillis() - it.receivedAt <= MqttVigiAireClient.LIVE_TIMEOUT_MS }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Consejo del circuito", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Basado en el último estado MQTT", fontSize = VigiAireTypeScale.Label, color = TextoGris)
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
            Text("Indicador que envió Wokwi", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
            TrafficLightWidget(estadoActivo = live?.estado)
            RecommendationCard(live?.estado)

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AzulPrimario)
                        Text("  ¿Cómo interpretar el estado?", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    }
                    val desc = when (live?.estado) {
                        com.tecsup.vigiaire.data.model.EstadoSensor.VENTILE -> "El firmware ubicó la señal analógica por debajo del umbral bajo configurado y envió VENTILE."
                        com.tecsup.vigiaire.data.model.EstadoSensor.ESPERE -> "El firmware ubicó la señal analógica por encima del umbral bajo y envió ESPERE."
                        com.tecsup.vigiaire.data.model.EstadoSensor.ALERTA -> "La entrada digital del sensor activó la condición de alerta y el firmware envió ALERTA."
                        else -> if (connection == MqttConnectionState.CONNECTED) "Esperando el siguiente mensaje del sensor…" else "Sin una lectura vigente. Revisa la conexión MQTT y deja Wokwi en ejecución."
                    }
                    Text(desc, fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    Text("Esta recomendación explica la lógica de prueba del circuito. No es una recomendación sanitaria ni una decisión basada en PM2.5.", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                }
            }

            Button(
                onClick = { onNavigate(Pantalla.Tendencia.ruta) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario, contentColor = Blanco)
            ) { Text("Ver tendencia en vivo", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(6.dp))
        }
    }
}
