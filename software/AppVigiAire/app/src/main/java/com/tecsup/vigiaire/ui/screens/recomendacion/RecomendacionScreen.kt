package com.tecsup.vigiaire.ui.screens.recomendacion

import com.tecsup.vigiaire.ui.theme.VigiAireTypeScale

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.vigiaire.data.model.EstadoCalidad
import com.tecsup.vigiaire.data.repository.Repositorio
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionScreen(
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
                            text = "¿Ventilo ahora?",
                            fontSize = VigiAireTypeScale.Section,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text(
                            text = "Esta recomendación se basa en la lectura del módulo",
                            fontSize = VigiAireTypeScale.Caption,
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

            // Semáforo visual con 3 círculos (Rojo, Amarillo, Verde) con el activo resaltado
            Text(
                text = "Indicador de Semáforo",
                fontSize = VigiAireTypeScale.Body,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(8.dp))

            TrafficLightWidget(
                estadoActivo = lecturaActual.estado
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Tarjeta grande con la recomendación
            RecommendationCard(
                estado = lecturaActual.estado,
                textoPersonalizado = lecturaActual.estado.recomendacionVentilacion
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sección "¿Qué puedo hacer mientras?"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AzulClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Acciones",
                                tint = AzulPrimario,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "¿Qué puedo hacer mientras?",
                            fontSize = VigiAireTypeScale.Body,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val acciones = when (lecturaActual.estado) {
                        EstadoCalidad.BUENA -> listOf(
                            "Aprovecha para abrir ventanas en lados opuestos de tu hogar.",
                            "Ventila áreas cerradas como dormitorios y cocina por 15 a 20 min.",
                            "Ideal para realizar ejercicio físico o caminatas al aire libre."
                        )
                        EstadoCalidad.MODERADA -> listOf(
                            "Ventila por periodos breves (máximo 10 minutos).",
                            "Evita la ventilación durante las horas punta de tráfico vehicular.",
                            "Si eres sensible, mantén un purificador de aire encendido."
                        )
                        EstadoCalidad.MALA -> listOf(
                            "Mantén ventanas y puertas bien cerradas para evitar el ingreso del polvo.",
                            "Utiliza purificador de aire con filtro HEPA si cuentas con uno.",
                            "Evita actividades físicas exigentes en exteriores.",
                            "Usa mascarilla si necesitas desplazarte por avenidas con tráfico alto."
                        )
                    }

                    acciones.forEach { accion ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Tip",
                                tint = lecturaActual.estado.color,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = accion,
                                fontSize = VigiAireTypeScale.Label,
                                color = TextoGris,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Ver historial y lecturas"
            Button(
                onClick = { onNavigate(Pantalla.Tendencia.ruta) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrimario,
                    contentColor = Blanco
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Ver historial y lecturas",
                    fontSize = VigiAireTypeScale.Body,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Histórico",
                    tint = Blanco
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
