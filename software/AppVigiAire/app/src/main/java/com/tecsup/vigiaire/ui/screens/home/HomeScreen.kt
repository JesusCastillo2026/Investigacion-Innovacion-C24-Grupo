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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.R
import com.tecsup.vigiaire.data.model.DatoHistoricoBarra
import com.tecsup.vigiaire.data.model.LecturaAire
import com.tecsup.vigiaire.data.network.MqttConnectionState
import com.tecsup.vigiaire.data.network.MqttGasReading
import com.tecsup.vigiaire.data.network.MqttVigiAireClient
import com.tecsup.vigiaire.data.repository.Repositorio
import com.tecsup.vigiaire.navigation.Pantalla
import com.tecsup.vigiaire.ui.components.CircularAirGauge
import com.tecsup.vigiaire.ui.components.RecommendationCard
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AmarilloSuave
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
import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val lecturas by Repositorio.lecturas.collectAsState()
    val historico by Repositorio.historico12Horas.collectAsState()
    val usuario by Repositorio.usuarioActual.collectAsState()
    val mqttState by MqttVigiAireClient.state.collectAsState()
    val latestMqttReading by MqttVigiAireClient.latestReading.collectAsState()
    val conectado = mqttState == MqttConnectionState.CONNECTED
    val lecturaActual = lecturas.firstOrNull() ?: Repositorio.obtenerUltimaLectura()
    val muestra14h = historico.firstOrNull { it.horaLabel.startsWith("14h") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "VIGIAIRE",
                        fontSize = VigiAireTypeScale.Section,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro,
                        letterSpacing = 1.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Menú VigiAire • TECSUP") }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = TextoOscuro)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Monitoreo ${if (conectado) "activo" else "en modo demostración"}.")
                        }
                    }) {
                        BadgedBox(badge = { Badge(containerColor = AzulPrimario) { Text("2", color = Blanco) } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AzulClaro)
                            .clickable { onNavigate(Pantalla.Perfil.ruta) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = AzulPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco),
                modifier = Modifier.shadow(2.dp)
            )
        },
        bottomBar = { VigiAireBottomBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = FondoGris
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Hola, ${usuario?.nombre ?: "Jeremy"}",
                    fontSize = VigiAireTypeScale.Heading,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "Revisa el aire antes de abrir la ventana.",
                    fontSize = VigiAireTypeScale.Body,
                    color = TextoGris
                )
            }

            Image(
                painter = painterResource(R.drawable.vigiaire_home_hero),
                contentDescription = "Sensor VigiAire junto a una ventana con vista a la ciudad",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lectura de muestra", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        EstadoPill("Simulada", AzulClaro, AzulOscuro)
                    }
                    Spacer(Modifier.height(8.dp))
                    CircularAirGauge(
                        pm25 = lecturaActual.pm25,
                        estado = lecturaActual.estado,
                        size = 148.dp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "El dato MQTT del MQ-2 se muestra en la sección Dispositivo.",
                        fontSize = VigiAireTypeScale.Label,
                        color = TextoGris
                    )
                }
            }

            ResumenJornadaCard(muestra14h, latestMqttReading)

            RecommendationCard(
                estado = lecturaActual.estado,
                textoPersonalizado = lecturaActual.estado.recomendacionHome
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Pantalla.Dispositivo.ruta) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(if (conectado) VerdeSuave else RojoSuave),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Router, contentDescription = "Simulador", tint = if (conectado) VerdeExito else RojoAlerta)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Simulador Wokwi", fontSize = VigiAireTypeScale.Body, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Text(
                                text = if (conectado) "Conectado por MQTT" else "Sin conexión MQTT",
                                fontSize = VigiAireTypeScale.Label,
                                color = if (conectado) VerdeExito else TextoGris
                            )
                        }
                    }
                    Text("Ver dispositivo", fontSize = VigiAireTypeScale.Label, fontWeight = FontWeight.SemiBold, color = AzulPrimario)
                }
            }
        }
    }
}

@Composable
private fun ResumenJornadaCard(
    muestra14h: DatoHistoricoBarra?,
    ultimaSenal: MqttGasReading?
) {
    val valorPm25 = muestra14h?.pm25 ?: 58.0
    val estadoMuestra = LecturaAire.calcularEstado(valorPm25)
    val fechaHoy = remember {
        SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE"))
            .format(Date())
            .replaceFirstChar { it.titlecase(Locale("es", "PE")) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(AmarilloSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = AzulOscuro)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Resumen de hoy", fontSize = VigiAireTypeScale.Section, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    Text(fechaHoy, fontSize = VigiAireTypeScale.Label, color = TextoGris)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("14:00 · muestra PM2.5", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${valorPm25.toInt()}", fontSize = VigiAireTypeScale.Metric, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Spacer(Modifier.width(5.dp))
                        Text("µg/m³", fontSize = VigiAireTypeScale.Label, color = TextoGris, modifier = Modifier.padding(bottom = 5.dp))
                    }
                }
                EstadoPill(estadoMuestra.titulo, estadoMuestra.colorFondo, estadoMuestra.color)
            }

            Text(
                text = "Hoy a las 2:00 p. m. la muestra fue ${valorPm25.toInt()} µg/m³: nivel ${estadoMuestra.titulo.lowercase(Locale("es", "PE"))}.",
                fontSize = VigiAireTypeScale.Body,
                color = TextoOscuro
            )
            Text(
                text = "Registro de demostración; el MQ-2 simulado entrega una señal relativa, no PM2.5.",
                fontSize = VigiAireTypeScale.Label,
                color = TextoGris
            )

            if (ultimaSenal != null) {
                Divider(color = FondoGris)
                Text("Última señal de Wokwi · ${formatHora(ultimaSenal.receivedAt)}", fontSize = VigiAireTypeScale.Label, color = TextoGris)
                Text(
                    text = "${ultimaSenal.gasAdc} ADC · ${ultimaSenal.status}",
                    fontSize = VigiAireTypeScale.Body,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoOscuro
                )
            }
        }
    }
}

@Composable
private fun EstadoPill(texto: String, fondo: Color, color: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(fondo).padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(texto, fontSize = VigiAireTypeScale.Label, fontWeight = FontWeight.SemiBold, color = color)
    }
}

private fun formatHora(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale("es", "PE")).format(Date(timestamp))
