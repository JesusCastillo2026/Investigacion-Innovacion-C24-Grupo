package com.tecsup.vigiaire.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.data.model.DatoHistoricoBarra
import com.tecsup.vigiaire.data.model.EstadoCalidad
import com.tecsup.vigiaire.navigation.Pantalla
import com.tecsup.vigiaire.ui.theme.AmarilloAlerta
import com.tecsup.vigiaire.ui.theme.AzulClaro
import com.tecsup.vigiaire.ui.theme.AzulOscuro
import com.tecsup.vigiaire.ui.theme.AzulPrimario
import com.tecsup.vigiaire.ui.theme.Blanco
import com.tecsup.vigiaire.ui.theme.RojoAlerta
import com.tecsup.vigiaire.ui.theme.TextoGris
import com.tecsup.vigiaire.ui.theme.TextoOscuro
import com.tecsup.vigiaire.ui.theme.VerdeExito

/**
 * Logo oficial de VigiAire: Ícono circular azul con ondas de aire
 */
@Composable
fun VigiAireLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showGlow: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showGlow) Modifier.shadow(12.dp, CircleShape, spotColor = AzulPrimario)
                else Modifier
            )
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(AzulPrimario, AzulOscuro)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Air,
            contentDescription = "Logo VigiAire",
            tint = Blanco,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/**
 * Semáforo Circular Grande (180.dp) con anillo de progreso animado
 */
@Composable
fun CircularAirGauge(
    pm25: Double,
    estado: EstadoCalidad,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    val targetProgress = (pm25 / 150.0).toFloat().coerceIn(0.05f, 1.0f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "GaugeAnimation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 14.dp.toPx()

            // Anillo base gris
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Anillo de progreso animado
            drawArc(
                color = estado.color,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = pm25.toInt().toString(),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = "µg/m³ PM2.5",
                fontSize = 12.sp,
                color = TextoGris,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Badge pill de estado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(estado.colorFondo)
                    .border(1.dp, estado.color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = estado.titulo,
                    color = estado.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Semáforo visual de 3 luces (Rojo, Amarillo, Verde) con la luz activa resaltada
 */
@Composable
fun TrafficLightWidget(
    estadoActivo: EstadoCalidad,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Luz Roja
            TrafficLightCircle(
                color = RojoAlerta,
                isActivo = estadoActivo == EstadoCalidad.MALA,
                label = "MALA"
            )

            // Luz Amarilla
            TrafficLightCircle(
                color = AmarilloAlerta,
                isActivo = estadoActivo == EstadoCalidad.MODERADA,
                label = "MODERADA"
            )

            // Luz Verde
            TrafficLightCircle(
                color = VerdeExito,
                isActivo = estadoActivo == EstadoCalidad.BUENA,
                label = "BUENA"
            )
        }
    }
}

@Composable
private fun TrafficLightCircle(
    color: Color,
    isActivo: Boolean,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isActivo) 48.dp else 36.dp)
                .then(
                    if (isActivo) Modifier.shadow(12.dp, CircleShape, spotColor = color)
                    else Modifier
                )
                .clip(CircleShape)
                .background(if (isActivo) color else color.copy(alpha = 0.25f))
                .border(
                    width = if (isActivo) 3.dp else 1.dp,
                    color = if (isActivo) Blanco else Color.Transparent,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActivo) FontWeight.Bold else FontWeight.Normal,
            color = if (isActivo) Blanco else Color(0xFF94A3B8)
        )
    }
}

/**
 * Tarjeta individual para métricas (PM2.5, PM10, Temp, Humedad)
 */
@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x10000000)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoGris
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoGris,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

/**
 * Tarjeta de Recomendación contextual de salud
 */
@Composable
fun RecommendationCard(
    estado: EstadoCalidad,
    textoPersonalizado: String? = null,
    modifier: Modifier = Modifier
) {
    val icon = when (estado) {
        EstadoCalidad.BUENA -> Icons.Default.CheckCircle
        EstadoCalidad.MODERADA -> Icons.Default.Info
        EstadoCalidad.MALA -> Icons.Default.Warning
    }

    val mensaje = textoPersonalizado ?: estado.recomendacionHome

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x10000000)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = estado.colorFondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, estado.color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(estado.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Recomendación",
                    tint = estado.color,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Recomendación de Ventilación",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = estado.color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mensaje,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoOscuro
                )
            }
        }
    }
}

/**
 * Gráfico de Barras Simple para Tendencias
 */
@Composable
fun BarChartWidget(
    datos: List<DatoHistoricoBarra>,
    modifier: Modifier = Modifier
) {
    val maxPm25 = (datos.maxOfOrNull { it.pm25 } ?: 100.0).coerceAtLeast(60.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                datos.forEach { punto ->
                    val barHeightFraction = (punto.pm25 / maxPm25).toFloat().coerceIn(0.1f, 1.0f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight(barHeightFraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(punto.estado.color)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = punto.horaLabel.take(3),
                            fontSize = 9.sp,
                            color = TextoGris,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * NavigationBar inferior con 5 destinos
 */
@Composable
fun VigiAireBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Blanco,
        tonalElevation = 8.dp,
        modifier = Modifier.shadow(8.dp)
    ) {
        NavigationBarItem(
            selected = currentRoute == Pantalla.Home.ruta,
            onClick = { onNavigate(Pantalla.Home.ruta) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrimario,
                selectedTextColor = AzulPrimario,
                indicatorColor = AzulClaro,
                unselectedIconColor = TextoGris,
                unselectedTextColor = TextoGris
            )
        )

        NavigationBarItem(
            selected = currentRoute == Pantalla.Detalle.ruta,
            onClick = { onNavigate(Pantalla.Detalle.ruta) },
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Detalle") },
            label = { Text("Detalle", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrimario,
                selectedTextColor = AzulPrimario,
                indicatorColor = AzulClaro,
                unselectedIconColor = TextoGris,
                unselectedTextColor = TextoGris
            )
        )

        NavigationBarItem(
            selected = currentRoute == Pantalla.Recomendacion.ruta,
            onClick = { onNavigate(Pantalla.Recomendacion.ruta) },
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = "Ventilar") },
            label = { Text("Consejo", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrimario,
                selectedTextColor = AzulPrimario,
                indicatorColor = AzulClaro,
                unselectedIconColor = TextoGris,
                unselectedTextColor = TextoGris
            )
        )

        NavigationBarItem(
            selected = currentRoute == Pantalla.Tendencia.ruta,
            onClick = { onNavigate(Pantalla.Tendencia.ruta) },
            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Tendencia") },
            label = { Text("Tendencia", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrimario,
                selectedTextColor = AzulPrimario,
                indicatorColor = AzulClaro,
                unselectedIconColor = TextoGris,
                unselectedTextColor = TextoGris
            )
        )

        NavigationBarItem(
            selected = currentRoute == Pantalla.Dispositivo.ruta,
            onClick = { onNavigate(Pantalla.Dispositivo.ruta) },
            icon = { Icon(Icons.Default.Router, contentDescription = "Equipo") },
            label = { Text("Equipo", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrimario,
                selectedTextColor = AzulPrimario,
                indicatorColor = AzulClaro,
                unselectedIconColor = TextoGris,
                unselectedTextColor = TextoGris
            )
        )
    }
}
