package com.tecsup.vigiaire.ui.screens.splash
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.ui.components.*
import com.tecsup.vigiaire.ui.theme.*
@Composable
fun SplashScreen(onComenzarClick: () -> Unit) {
 Surface(color=FondoGris) {
  Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(28.dp),verticalArrangement=Arrangement.spacedBy(24.dp)) {
   VigiAireLogo(size=64.dp,showGlow=false)
   Text("VigiAire",style=MaterialTheme.typography.displayLarge)
   AireIllustration()
   Text("Observa. Comprende. Decide.",style=MaterialTheme.typography.headlineMedium)
   Text("Explora el monitoreo del aire con lecturas en vivo, tendencias claras y una guía para interpretar cada estado.",style=MaterialTheme.typography.bodyLarge,color=TextoGris)
   Text("01  Conecta tu simulación\n02  Explora la lectura y sus cambios\n03  Consulta la guía cuando lo necesites",style=MaterialTheme.typography.bodyLarge)
   Button(onClick=onComenzarClick,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)){Text("Comenzar")}
   Text("Proyecto académico · simulación de señal ADC",style=MaterialTheme.typography.labelSmall,color=TextoGris)
  }
 }
}
