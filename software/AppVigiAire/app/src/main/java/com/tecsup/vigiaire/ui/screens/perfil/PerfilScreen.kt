package com.tecsup.vigiaire.ui.screens.perfil
import com.tecsup.vigiaire.ui.components.GlassCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.ui.theme.*
import com.tecsup.vigiaire.data.repository.Repositorio
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PerfilScreen(onVolver:()->Unit,onCerrarSesion:()->Unit) {
 Scaffold(containerColor=FondoGris,topBar={TopAppBar(title={Text("Mi perfil")},navigationIcon={TextButton(onClick=onVolver){Text("Volver")}})}) { insets ->
 Column(Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)) {
 Box(Modifier.size(100.dp).background(Brush.linearGradient(listOf(Color(0xFF166B97),Color(0xFF168875))),CircleShape),contentAlignment=Alignment.Center) { Text("JC",style=MaterialTheme.typography.displayLarge,color=Color.White) }
 Text("Jeremy Cervantes",style=MaterialTheme.typography.headlineMedium)
 Text("Perfil de demostración",color=TextoGris)
 GlassCard(colors=CardDefaults.cardColors(containerColor=Blanco)) { Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
 Text("Información personal",style=MaterialTheme.typography.titleMedium)
 Text("Correo de ejemplo: jeremy.cervantes@example.com",color=TextoOscuro)
 Text("Alias: Jeremy Aire",color=TextoOscuro)
 Text("Espacio favorito: Mi hogar",color=TextoOscuro)
 Text("Los datos adicionales son ficticios y no cambian las credenciales de acceso.",style=MaterialTheme.typography.bodySmall,color=TextoGris)
 } }
 GlassCard(colors=CardDefaults.cardColors(containerColor=AzulClaro)) { Row(Modifier.fillMaxWidth().padding(20.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text("Modo noche",style=MaterialTheme.typography.titleMedium); Text("Una vista cómoda con poca luz",color=TextoGris) }; com.tecsup.vigiaire.ui.components.ThemeSwitch(checked=Apariencia.noche.value,onCheckedChange={Apariencia.noche.value=it}) } }
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Texto ampliado",Modifier.weight(1f),color=TextoOscuro); com.tecsup.vigiaire.ui.components.ThemeSwitch(Apariencia.textoGrande.value,{Apariencia.textoGrande.value=it}) }
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Contraste reforzado",Modifier.weight(1f),color=TextoOscuro); com.tecsup.vigiaire.ui.components.ThemeSwitch(Apariencia.contraste.value,{Apariencia.contraste.value=it}) }
 Text("Esta cuenta de demostración utiliza credenciales compartidas; el cambio de contraseña no está habilitado.",color=TextoGris)
 Button(onClick={Repositorio.cerrarSesion();onCerrarSesion()},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp),colors=ButtonDefaults.buttonColors(containerColor=RojoSuave,contentColor=RojoAlerta)) { Text("Cerrar sesión") }
 }
 }
}
