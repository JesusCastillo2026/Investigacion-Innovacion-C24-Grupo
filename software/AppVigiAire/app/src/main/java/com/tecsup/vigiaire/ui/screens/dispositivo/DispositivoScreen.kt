package com.tecsup.vigiaire.ui.screens.dispositivo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulOscuro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.BordeGris
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.RojoAlerta
import com.tecsup.vigiaire.ui.theme.RojoSuave
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VerdeExito
import com.tecsup.vigiaire.ui.theme.VerdeSuave
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispositivoScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val conectado by Repositorio.dispositivoConectado.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var mostrarDiagnostico by remember { mutableStateOf(false) }
    var probandoLed by remember { mutableStateOf(false) }

    val ledAlpha = remember { Animatable(1f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tu dispositivo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text(
                            text = "Conexión con el equipo físico",
                            fontSize = 12.sp,
                            color = TextoGris
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

            // Tarjeta con Estado de Conexión
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (conectado) VerdeSuave else RojoSuave),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Router,
                                    contentDescription = "Router",
                                    tint = if (conectado) VerdeExito else RojoAlerta,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (conectado) "VigiAire conectado" else "VigiAire desconectado",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = if (conectado) "Señal estable · Actualizaciones activas" else "Sin comunicación con el módulo",
                                    fontSize = 12.sp,
                                    color = if (conectado) VerdeExito else RojoAlerta,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Indicador de estado del LED
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FondoGris, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (conectado) {
                                            VerdeExito.copy(alpha = ledAlpha.value)
                                        } else {
                                            RojoAlerta
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (probandoLed) " LED Test Parpadeando..." else if (conectado) " LED Verde Encendido" else " LED Rojo Apagado",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                        }

                        // Botón simulado para conectar/desconectar
                        OutlinedButton(
                            onClick = {
                                Repositorio.alternarConexionDispositivo()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (!conectado) "Módulo VigiAire reactivado" else "Módulo desconectado"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "On/Off",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (conectado) "Desconectar" else "Conectar", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección "Acciones Disponibles"
            Text(
                text = "Acciones Disponibles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            // 1. Botón "Actualizar lectura"
            Button(
                onClick = {
                    Repositorio.generarNuevaLectura()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Nueva lectura enviada desde el sensor PM2.5")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario, contentColor = Blanco),
                shape = RoundedCornerShape(14.dp),
                enabled = conectado
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar lectura", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Botón "Probar indicador luminoso"
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        probandoLed = true
                        Repositorio.activarPruebaLed()
                        repeat(6) {
                            ledAlpha.animateTo(0.2f, animationSpec = tween(200))
                            ledAlpha.animateTo(1f, animationSpec = tween(200))
                        }
                        probandoLed = false
                        Repositorio.desactivarPruebaLed()
                        snackbarHostState.showSnackbar("Prueba del LED del módulo física completada.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = conectado
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "LED", tint = AzulPrimario)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Probar indicador luminoso", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Botón "Diagnóstico del equipo"
            OutlinedButton(
                onClick = { mostrarDiagnostico = !mostrarDiagnostico },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Build, contentDescription = "Diagnóstico", tint = AzulOscuro)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Diagnóstico del equipo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            }

            // Panel de Diagnóstico Simulado
            AnimatedVisibility(visible = mostrarDiagnostico) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Resultado del Diagnóstico Físico",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val componentes = listOf(
                            "Arduino Uno R3 (Microcontrolador)" to "OK · 5V Estable",
                            "Sensor Óptico PM2.5 (GP2Y1010AU0F)" to "OK · Calibrado",
                            "Sensor DHT11 (Temp y Humedad)" to "OK · Lectura activa",
                            "Módulo RTC DS3231 (Reloj Tiempo Real)" to "OK · Sincronizado",
                            "Pantalla LCD 16x2 I2C" to "OK · Renderizando",
                            "LEDs Indicadores (Verde / Rojo)" to "OK · Operativos"
                        )

                        componentes.forEach { (nombre, estado) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(nombre, fontSize = 12.sp, color = TextoOscuro, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "OK", tint = VerdeExito, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(estado, fontSize = 11.sp, color = VerdeExito, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nota sobre el estado del aparato
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaro)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = "Info",
                        tint = AzulOscuro,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "El aparato desconectado no envía alertas. Conéctalo para ver el aire en tiempo real.",
                        fontSize = 12.sp,
                        color = AzulOscuro,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
