package com.tecsup.vigiaire.ui.screens.informacion
import com.tecsup.vigiaire.ui.components.GlassCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.data.network.*
import com.tecsup.vigiaire.ui.theme.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun NotificacionesScreen(onVolver:()->Unit) {
 val context=androidx.compose.ui.platform.LocalContext.current
 val avisos by MqttVigiAireClient.avisos.collectAsState()
 val reading by MqttVigiAireClient.latestReading.collectAsState()
 val connection by MqttVigiAireClient.state.collectAsState()
 var now by remember { mutableStateOf(System.currentTimeMillis()) }
 LaunchedEffect(Unit) { while(true) { kotlinx.coroutines.delay(1000); now=System.currentTimeMillis() } }
 val fresh=connection==MqttConnectionState.CONNECTED && reading?.let { now-it.receivedAt <= MqttVigiAireClient.LIVE_TIMEOUT_MS }==true
 Scaffold(containerColor=FondoGris,topBar={TopAppBar(title={Text("Notificaciones")},navigationIcon={TextButton(onClick=onVolver){Text("Volver")}})}) { padding ->
 Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
 GlassCard(colors=CardDefaults.cardColors(containerColor=AzulClaro)) { Column(Modifier.fillMaxWidth().padding(20.dp)) { Text(if(fresh) "Dispositivo enviando lecturas" else "Sin señal del circuito",style=MaterialTheme.typography.titleMedium); Text(if(fresh) "Último dato: ${reading?.gasAdc} ADC · ${reading?.status}" else "Inicia Wokwi y revisa la conexión. La ausencia de datos no confirma que el dispositivo esté apagado.",color=TextoGris) } }
 Text("Historial de días de prueba",style=MaterialTheme.typography.titleLarge)
 if(avisos.isEmpty()) Text("Los resúmenes aparecerán cuando se detecte un reinicio del circuito.",color=TextoGris)
 avisos.forEach { GlassCard(colors=CardDefaults.cardColors(containerColor=Blanco)) { Text(it,Modifier.padding(20.dp),color=TextoOscuro); TextButton(onClick={context.startActivity(android.content.Intent.createChooser(android.content.Intent(android.content.Intent.ACTION_SEND).apply { type="text/plain"; putExtra(android.content.Intent.EXTRA_TEXT,"VigiAire · Resumen simulado\n"+it) },"Compartir resumen"))}) { Text("Compartir resumen") } } }
 Text("Se guardan en este dispositivo los últimos 50 días de prueba detectados.",color=TextoGris)
 }
 }
}
