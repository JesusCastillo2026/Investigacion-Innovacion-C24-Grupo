package com.tecsup.vigiaire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tecsup.vigiaire.ui.theme.*

/** Estado en memoria: se reinicia al cerrar el proceso/app, no al cerrar sesión. */
private object TutorialSessionState {
    var shownThisRun: Boolean = false
}

@Composable
fun BienvenidaPorArranque() {
    var visible by rememberSaveable { mutableStateOf(!TutorialSessionState.shownThisRun) }
    var step by rememberSaveable { mutableIntStateOf(0) }
    val titles = listOf("Conecta tu espacio", "Comprende la lectura", "Activa la extracción", "Descubre los cambios", "Explora la app")
    val descriptions = listOf(
        "Abre Equipo y enciende la simulación de Wokwi. Inicio mostrará las lecturas recibidas.",
        "Detalle explica el valor ADC y los estados VENTILE, ESPERE y ALERTA. Son datos simulados.",
        "En Equipo, pulsa Iniciar extracción de aire para activar la rotación simulada del motor paso a paso en Wokwi. Pulsa Detener para pararlo. Es visual: no mueve aire ni representa una extracción real.",
        "En Tendencia puedes elegir las últimas 10, 24 o todas las lecturas y comparar sus valores.",
        "El menú reúne Ayuda y Notificaciones. En Perfil puedes activar modo noche y ampliar el texto."
    )
    val icons = listOf(Icons.Default.Router, Icons.Default.Air, Icons.Default.Air, Icons.Default.Analytics, Icons.Default.Info)
    val finish = { TutorialSessionState.shownThisRun = true; visible = false }

    if (visible) Dialog(onDismissRequest = finish) {
        Surface(shape = RoundedCornerShape(28.dp), color = Blanco) {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 620.dp).verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bienvenido a VigiAire", style = MaterialTheme.typography.titleMedium, color = TextoOscuro)
                    Text("${step + 1}/5", color = TextoGris)
                }
                Box(
                    Modifier.fillMaxWidth().height(112.dp)
                        .background(Brush.linearGradient(listOf(AzulClaro, VerdeSuave)), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(icons[step], contentDescription = null, modifier = Modifier.size(52.dp), tint = AzulPrimario) }
                Text(titles[step], style = MaterialTheme.typography.headlineMedium, color = TextoOscuro)
                Text(descriptions[step], style = MaterialTheme.typography.bodyLarge, color = TextoGris)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    repeat(5) { index ->
                        Box(Modifier.padding(4.dp).size(if (index == step) 10.dp else 8.dp)
                            .background(if (index == step) AzulPrimario else BordeGris, CircleShape))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { if (step > 0) step-- else finish() }) { Text(if (step > 0) "Atrás" else "Omitir") }
                    Button(onClick = { if (step < 4) step++ else finish() }) { Text(if (step < 4) "Siguiente" else "Comenzar") }
                }
            }
        }
    }
}
