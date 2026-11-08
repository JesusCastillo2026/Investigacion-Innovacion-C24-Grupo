package com.tecsup.vigiaire.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.tecsup.vigiaire.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.ui.theme.*

/** Ilustración vectorial original: ventana, vegetación y ondas de aire. */
@Composable
fun AireIllustration(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(listOf(AzulClaro, VerdeSuave))).padding(24.dp)) { Text("VIGIAIRE", style=MaterialTheme.typography.labelLarge, color=AzulPrimario); Spacer(Modifier.height(12.dp)); Text("Un espacio para respirar", style=MaterialTheme.typography.headlineMedium, color=TextoOscuro); Spacer(Modifier.height(8.dp)); Text("Conecta con lo que te rodea.", style=MaterialTheme.typography.bodyLarge, color=TextoGris) }
}

@Composable
fun InfoAccordion(title: String, body: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "accordionArrow")
    GlassCard(Modifier.fillMaxWidth().animateContentSize(), border=androidx.compose.foundation.BorderStroke(1.dp, if(expanded) AzulPrimario else BordeGris), shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=if(expanded) AzulClaro else Blanco)) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth().semantics { stateDescription = if (expanded) "Expandido" else "Contraído" }.clickable(role = Role.Button) { expanded=!expanded }.heightIn(min=48.dp),verticalAlignment=Alignment.CenterVertically) {
                Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.SemiBold)
                Icon(Icons.Default.ExpandMore,if(expanded) "Cerrar explicación" else "Abrir explicación",tint=AzulPrimario, modifier=Modifier.rotate(arrowRotation))
            }
            if(expanded) Text(body,Modifier.padding(top=8.dp),style=MaterialTheme.typography.bodyLarge,color=TextoGris)
        }
    }
}

