package com.tecsup.vigiaire.ui.screens.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.data.repository.Repositorio
import com.tecsup.vigiaire.ui.components.MetricCard
import com.tecsup.vigiaire.ui.components.VigiAireBottomBar
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.FondoGris
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val lecturas by Repositorio.lecturas.collectAsState()
    val lecturaActual = lecturas.firstOrNull() ?: Repositorio.obtenerUltimaLectura()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Detalle del aire",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text(
                            text = "Lectura del módulo de ventana",
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
        containerColor = FondoGris
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {

            // Tarjeta grande con el valor principal PM2.5
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(lecturaActual.estado.colorFondo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "PM2.5",
                            tint = lecturaActual.estado.color,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${lecturaActual.pm25} µg/m³",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Text(
                        text = "PM2.5 · Partículas finas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoGris
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Badge de Estado
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(lecturaActual.estado.colorFondo)
                            .border(1.dp, lecturaActual.estado.color, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "ESTADO: ${lecturaActual.estado.titulo}",
                            color = lecturaActual.estado.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sección "¿Qué significa?"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AzulClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Información",
                                tint = AzulPrimario,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "¿Qué significa?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Las partículas PM2.5 son contaminantes de tamaño microscópico (2.5 micrómetros) provenientes de la combustión de vehículos y procesos industriales en zonas urbanas como Lima.",
                        fontSize = 13.sp,
                        color = TextoGris,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (lecturaActual.estado) {
                            com.tecsup.vigiaire.data.model.EstadoCalidad.BUENA -> " Un nivel menor a 35 µg/m³ indica que el aire en tu entorno es limpio y seguro para realizar actividades normales."
                            com.tecsup.vigiaire.data.model.EstadoCalidad.MODERADA -> " Un nivel entre 35 y 75 µg/m³ indica una calidad moderada. Personas sensibles deben tomar precauciones."
                            com.tecsup.vigiaire.data.model.EstadoCalidad.MALA -> " Un nivel mayor a 75 µg/m³ implica aire contaminado de riesgo. Se recomienda mantener puertas y ventanas cerradas."
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = lecturaActual.estado.color,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Grid de 2 tarjetas: Temperatura y Humedad
            Text(
                text = "Variables Ambientales",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "PM10 Coarse",
                    value = "${lecturaActual.pm10}",
                    unit = "µg/m³",
                    icon = Icons.Default.Cloud,
                    iconColor = Color(0xFF8E24AA),
                    iconBgColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Temperatura",
                    value = "${lecturaActual.temperatura}",
                    unit = "°C",
                    icon = Icons.Default.Thermostat,
                    iconColor = Color(0xFFE65100),
                    iconBgColor = Color(0xFFFFE0B2),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MetricCard(
                title = "Humedad Relativa",
                value = "${lecturaActual.humedad}",
                unit = "%",
                icon = Icons.Default.WaterDrop,
                iconColor = Color(0xFF0288D1),
                iconBgColor = Color(0xFFE1F5FE)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
