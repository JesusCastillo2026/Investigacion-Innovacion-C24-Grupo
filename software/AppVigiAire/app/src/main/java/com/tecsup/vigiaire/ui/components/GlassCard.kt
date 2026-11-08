package com.tecsup.vigiaire.ui.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.ui.theme.*
/** Superficie translúcida con reflejo, borde y profundidad; no difumina el texto. */
@Composable fun GlassCard(modifier:Modifier=Modifier, shape:Shape=RoundedCornerShape(24.dp), colors:CardColors=CardDefaults.cardColors(containerColor=Blanco), border:BorderStroke?=null, elevation:CardElevation=CardDefaults.cardElevation(defaultElevation=2.dp), content:@Composable ColumnScope.()->Unit) {
 val night=Apariencia.noche.value
 val base=colors.containerColor
 if(Apariencia.contraste.value) { Card(modifier=modifier,shape=shape,border=BorderStroke(2.dp,TextoOscuro),colors=CardDefaults.cardColors(containerColor=Blanco,contentColor=TextoOscuro),content=content); return }
 Card(modifier=modifier,shape=shape,border=border?:BorderStroke(1.dp,if(night) Color(0xFF557782) else Color.White),elevation=elevation,colors=CardDefaults.cardColors(containerColor=base,contentColor=TextoOscuro)) {
 Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(if(night) Color(0xFF35515D).copy(alpha=.65f) else Color.White.copy(alpha=.85f),base.copy(alpha=.72f),if(night) Color(0xFF23423E).copy(alpha=.4f) else Color(0xFFDCEFEA).copy(alpha=.45f)))),content=content)
 }
}
@Composable fun ThemeSwitch(checked:Boolean,onCheckedChange:(Boolean)->Unit) {
 Switch(checked=checked,onCheckedChange=onCheckedChange,colors=SwitchDefaults.colors(checkedThumbColor=Color(0xFF103B49),checkedTrackColor=Color(0xFF7DD9ED),checkedBorderColor=Color(0xFF7DD9ED),uncheckedThumbColor=Color(0xFF17657B),uncheckedTrackColor=Color(0xFFB8D6DF),uncheckedBorderColor=Color(0xFF416C79)))
}
