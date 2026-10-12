package com.tecsup.vigiaire.ui.screens.home

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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.data.repository.Repositorio
import com.tecsup.vigiaire.navigation.Pantalla
import com.tecsup.vigiaire.ui.components.CircularAirGauge
import com.tecsup.vigiaire.ui.components.RecommendationCard
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val lecturas by Repositorio.lecturas.collectAsState()
    val usuario by Repositorio.usuarioActual.collectAsState()
    val conectado by Repositorio.dispositivoConectado.collectAsState()

    val lecturaActual = lecturas.firstOrNull() ?: Repositorio.obtenerUltimaLectura()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Contador de tiempo transcurrido desde la última lectura
    var segundosTranscurridos by remember { mutableLongStateOf(0L) }

    // Simulación en tiempo real: Genera lectura cada 5 segundos
    LaunchedEffect(lecturaActual.timestamp) {
        segundosTranscurridos = 0L
        while (isActive) {
            delay(1000L)
            segundosTranscurridos++
            if (segundosTranscurridos % 5 == 0L) {
                Repositorio.generarNuevaLectura()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VIGIAIRE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Menú VigiAire • TECSUP")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = TextoOscuro
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Notificación: Monitoreo activo en zona Lima.")
                        }
                    }) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = AzulPrimario) {
                                    Text("2", color = Blanco, fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = TextoOscuro
                            )
                        }
                    }

                    // Avatar de perfil rápida
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AzulClaro)
                            .clickable { onNavigate(Pantalla.Perfil.ruta) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = AzulPrimario,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blanco
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            VigiAireBottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = FondoGris
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            // Saludo personalizado
            Text(
                text = "Hola, ${usuario?.nombre ?: "Jeremy"}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = "Aquí está el estado del aire en tu zona",
                fontSize = 14.sp,
                color = TextoGris
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Tarjeta Superior con SEMÁFORO CIRCULAR GRANDE (180.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x15000000)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calidad del Aire",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AzulClaro)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Módulo Ventana",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AzulOscuro
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SEMÁFORO CIRCULAR GRANDE (180.dp)
                    CircularAirGauge(
                        pm25 = lecturaActual.pm25,
                        estado = lecturaActual.estado,
                        size = 180.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Actualizado hace $segundosTranscurridos seg",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoGris
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de Recomendación de Salud
            RecommendationCard(
                estado = lecturaActual.estado,
                textoPersonalizado = lecturaActual.estado.recomendacionHome
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta "Tu equipo" con LED simulado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Pantalla.Dispositivo.ruta) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (conectado) VerdeSuave else RojoSuave),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Router,
                                contentDescription = "Equipo",
                                tint = if (conectado) VerdeExito else RojoAlerta,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Tu equipo VigiAire",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // LED indicador simulado
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (conectado) VerdeExito else RojoAlerta)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (conectado) "VigiAire conectado" else "Desconectado",
                                    fontSize = 13.sp,
                                    color = if (conectado) VerdeExito else RojoAlerta,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AzulClaro)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Gestionar >",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPrimario
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
