package com.tecsup.vigiaire.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
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
    Canvas(modifier.fillMaxWidth().height(152.dp).clip(RoundedCornerShape(24.dp)).background(AzulOscuro)) {
        val w=size.width; val h=size.height
        drawCircle(Color(0xFF24535B), h*.8f, Offset(w*.85f,h*.2f))
        drawRoundRect(Color(0xFF87C9B9),Offset(w*.58f,h*.15f),Size(w*.25f,h*.7f),androidx.compose.ui.geometry.CornerRadius(16f))
        drawRoundRect(Color(0xFFE0F3E9),Offset(w*.60f,h*.19f),Size(w*.21f,h*.62f),androidx.compose.ui.geometry.CornerRadius(10f))
        drawLine(AzulOscuro,Offset(w*.705f,h*.2f),Offset(w*.705f,h*.8f),5f)
        drawLine(AzulOscuro,Offset(w*.6f,h*.5f),Offset(w*.81f,h*.5f),5f)
        for(i in 0..2){
            val y=h*(.35f+i*.15f)
            val path=Path().apply { moveTo(w*.08f,y); cubicTo(w*.24f,y-h*.2f,w*.32f,y+h*.2f,w*.5f,y) }
            drawPath(path,Color(0xFF8DDFC6),style=Stroke(width=5f,cap=StrokeCap.Round))
        }
        drawLine(Color(0xFF68B49F),Offset(w*.89f,h*.86f),Offset(w*.89f,h*.5f),5f)
        drawOval(Color(0xFF73C9AC),Offset(w*.84f,h*.48f),Size(w*.055f,h*.19f))
        drawOval(Color(0xFFB9E7D3),Offset(w*.89f,h*.58f),Size(w*.055f,h*.16f))
    }
}

@Composable
fun InfoAccordion(title: String, body: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "accordionArrow")
    Card(Modifier.fillMaxWidth().animateContentSize(),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Blanco)) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth().semantics { stateDescription = if (expanded) "Expandido" else "Contraído" }.clickable(role = Role.Button) { expanded=!expanded }.heightIn(min=48.dp),verticalAlignment=Alignment.CenterVertically) {
                Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.SemiBold)
                Icon(Icons.Default.ExpandMore,if(expanded) "Cerrar explicación" else "Abrir explicación",tint=AzulPrimario, modifier=Modifier.rotate(arrowRotation))
            }
            if(expanded) Text(body,Modifier.padding(top=8.dp),style=MaterialTheme.typography.bodyLarge,color=TextoGris)
        }
    }
}
