package com.tecsup.vigiaire.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.R
import com.tecsup.vigiaire.data.network.MqttConnectionState
import com.tecsup.vigiaire.data.network.MqttVigiAireClient
import com.tecsup.vigiaire.data.repository.Repositorio
import com.tecsup.vigiaire.navigation.Pantalla
import com.tecsup.vigiaire.ui.components.RecommendationCard
import com.tecsup.vigiaire.ui.components.SignalGauge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(currentRoute: String, onNavigate: (String) -> Unit) {
    val user by Repositorio.usuarioActual.collectAsState()
    val connection by MqttVigiAireClient.state.collectAsState()
    val latest by MqttVigiAireClient.latestReading.collectAsState()
    val sessionNumber by MqttVigiAireClient.sessionNumber.collectAsState()
    val startedAt by MqttVigiAireClient.sessionStartedAt.collectAsState()
    val readingCount by MqttVigiAireClient.sessionReadingCount.collectAsState()
    val minimumReading by MqttVigiAireClient.sessionMinimum.collectAsState()
    val maximumReading by MqttVigiAireClient.sessionMaximum.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }

    val liveReading = latest?.takeIf {
        connection == MqttConnectionState.CONNECTED &&
            System.currentTimeMillis() - it.receivedAt <= MqttVigiAireClient.LIVE_TIMEOUT_MS
    }
    val fecha = remember { SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE")).format(Date()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("VIGIAIRE", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro) },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = TextoOscuro)
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(text = { Text("Noticias") }, onClick = { menuOpen = false; onNavigate(Pantalla.Noticias.ruta) })
                            DropdownMenuItem(text = { Text("Tendencia en vivo") }, onClick = { menuOpen = false; onNavigate(Pantalla.Tendencia.ruta) })
                            DropdownMenuItem(text = { Text("Consejo del circuito") }, onClick = { menuOpen = false; onNavigate(Pantalla.Recomendacion.ruta) })
                            DropdownMenuItem(text = { Text("Conexión del equipo") }, onClick = { menuOpen = false; onNavigate(Pantalla.Dispositivo.ruta) })
                        }
                    }
                },
                actions = {
                    BadgedBox(badge = { if (connection == MqttConnectionState.CONNECTED) Badge(containerColor = Color(0xFF138A55)) {} }) {
                        IconButton(onClick = { onNavigate(Pantalla.Noticias.ruta) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Noticias", tint = TextoOscuro)
                        }
                    }
                    Box(Modifier.padding(end = 12.dp).size(36.dp).clip(CircleShape).background(AzulClaro), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = AzulPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco),
                modifier = Modifier.shadow(2.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Hola, ${user?.nombre ?: "Jeremy"}", fontSize = VigiAireTypeScale.Heading, fontWeight = FontWeight.Bold, color = TextoOscuro)
                Text("Lecturas que llegan en vivo desde Wokwi.", fontSize = VigiAireTypeScale.Body, color = TextoGris)
            }

            Image(
                painter = painterResource(R.drawable.vigiaire_home_hero),
                contentDescription = "Módulo VigiAire junto a una ventana",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(168.dp).clip(RoundedCornerShape(22.dp))
            )

            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Señal en vivo", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        EstadoPill(if (liveReading != null) "EN VIVO" else "SIN SEÑAL", if (liveReading != null) Color(0xFFE4F5EC) else Color(0xFFFFE8E8), if (liveReading != null) Color(0xFF138A55) else Color(0xFFCE3434))
                    }
                    Spacer(Modifier.height(6.dp))
                    SignalGauge(liveReading, size = 148.dp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (liveReading != null) "Actualizado ${formatTime(liveReading.receivedAt)}" else "Inicia Wokwi y espera la conexión MQTT para ver datos.",
                        fontSize = VigiAireTypeScale.Label,
                        color = TextoGris
                    )
                    Text("Señal relativa MQ-2 · gases/humo · no es PM2.5", fontSize = VigiAireTypeScale.Caption, color = TextoGris)
                }
            }

            DatoDelDiaCard()

            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Router, contentDescription = null, tint = AzulPrimario)
                        Column(Modifier.padding(start = 10.dp)) {
                            Text("Resumen de hoy", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Text("Día de prueba $sessionNumber · $fecha", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                        }
                    }
                    Text("Inicio de sesión: ${startedAt?.let(::formatTime) ?: "esperando la primera lectura"}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    if (liveReading != null) {
                        Text("A las ${formatTime(liveReading.receivedAt)}: ${liveReading.gasAdc} ADC y estado ${liveReading.status}.", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
                    } else {
                        Text("No hay una lectura vigente. El resumen aparecerá al recibir datos del circuito.", fontSize = VigiAireTypeScale.Body, color = TextoGris)
                    }
                    Text("Lecturas: $readingCount  ·  mínima: ${minimumReading?.gasAdc ?: "—"} ADC  ·  máxima: ${maximumReading?.gasAdc ?: "—"} ADC", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    Text("Al reiniciar el ESP32, la app detecta el reinicio y comienza un nuevo día de prueba.", fontSize = VigiAireTypeScale.Caption, color = TextoGris)
                }
            }

            RecommendationCard(liveReading?.estado)

            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(Pantalla.Dispositivo.ruta) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(CircleShape).background(if (connection == MqttConnectionState.CONNECTED) Color(0xFFE4F5EC) else Color(0xFFFFE8E8)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Router, contentDescription = null, tint = if (connection == MqttConnectionState.CONNECTED) Color(0xFF138A55) else Color(0xFFCE3434))
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("Equipo", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Text(connection.label(), fontSize = VigiAireTypeScale.Label, color = TextoGris)
                        }
                    }
                    Text("Ver", fontSize = VigiAireTypeScale.Label, fontWeight = FontWeight.SemiBold, color = AzulPrimario)
                }
            }
        }
    }
}

private data class DatoInformativo(val titulo: String, val texto: String)

private val datosInformativos = listOf(
    DatoInformativo("El aire puede variar durante el día", "El tráfico y otras fuentes cercanas pueden hacer que las condiciones del aire cambien con el paso de las horas."),
    DatoInformativo("Ventilar también depende del exterior", "Abrir una ventana renueva el aire interior, pero conviene considerar si afuera hay humo o polvo."),
    DatoInformativo("Una lectura es solo un momento", "Observar varias mediciones a lo largo del tiempo ayuda a reconocer cambios que una sola lectura no muestra."),
    DatoInformativo("La ubicación influye", "Una medición cerca de una ventana puede ser distinta de otra tomada en el centro de la habitación."),
    DatoInformativo("Pequeñas fuentes también cuentan", "El humo de cocina, velas o tabaco puede afectar el aire dentro de una habitación."),
    DatoInformativo("Mantén libres las entradas de aire", "El polvo acumulado frente a una rejilla o sensor puede dificultar la circulación y alterar su funcionamiento.")
)

@Composable
private fun DatoDelDiaCard() {
    val dato = remember { datosInformativos.random() }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Air, contentDescription = null, tint = AzulPrimario)
            Column(
                Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Dato del día", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(dato.titulo, fontSize = VigiAireTypeScale.Label, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
                Text(dato.texto, fontSize = VigiAireTypeScale.Label, color = TextoOscuro)
                Text("Dato informativo · no corresponde a una medición del circuito", fontSize = VigiAireTypeScale.Caption, color = TextoGris)
            }
        }
    }
}

@Composable
private fun EstadoPill(texto: String, fondo: Color, color: Color) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(fondo).padding(horizontal = 11.dp, vertical = 6.dp)) {
        Text(texto, fontSize = VigiAireTypeScale.Caption, fontWeight = FontWeight.Bold, color = color)
    }
}

private fun MqttConnectionState.label(): String = when (this) {
    MqttConnectionState.CONNECTED -> "Conectado · recibe lecturas"
    MqttConnectionState.CONNECTING -> "Conectando al simulador…"
    MqttConnectionState.ERROR -> "Error de conexión"
    MqttConnectionState.DISCONNECTED -> "Desconectado"
}

private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
