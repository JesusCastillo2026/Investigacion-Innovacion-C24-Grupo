package com.tecsup.vigiaire.ui.screens.informacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.ui.components.*
import com.tecsup.vigiaire.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionScreen(onVolver: () -> Unit) {
    Scaffold(containerColor=FondoGris,topBar={ TopAppBar(title={Text("Guía de VigiAire")},navigationIcon={IconButton(onClick=onVolver){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Volver")}}) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            AireIllustration()
            Text("Entiende cada lectura",style=MaterialTheme.typography.headlineMedium)
            Text("Abre una pregunta para conocer cómo funciona tu demostración.",style=MaterialTheme.typography.bodyLarge,color=TextoGris)
            InfoAccordion("¿Qué significa ADC?","Es un valor digital entre 0 y 4095. En esta demostración Wokwi genera valores simulados que varían gradualmente. No son concentraciones de gases ni mediciones de PM2.5.")
            InfoAccordion("¿Qué significa cada estado?","VENTILE: de 0 a 2100 ADC. ESPERE: de 2101 a 3099 ADC. ALERTA: de 3100 a 4095 ADC. Los colores siempre se acompañan de texto. Son umbrales de prueba, no categorías oficiales de calidad del aire.")
            TarjetaRangosEstado()
            InfoAccordion("¿El LCD y la app muestran lo mismo?","El ESP32 genera una muestra por ciclo, la muestra en el LCD y la envía por MQTT. La app recibe esa misma cifra y estado; puede existir un pequeño retraso de red. Mantén una sola simulación publicando durante la prueba.")
            InfoAccordion("¿Cómo interpreto la tendencia?","Cada punto corresponde a un mensaje recibido. Compara la lectura actual con el mínimo, promedio y máximo de la sesión. Los picos permiten localizar cambios importantes sin saturar la gráfica de números.")
            InfoAccordion("¿Qué hace el extractor?","El servo representa una compuerta: se abre en ALERTA y vuelve a su posición inicial en los demás estados. La simulación no reproduce succión física de aire.")
            InfoAccordion("¿Por qué aparece sin señal?","Inicia Wokwi y comprueba la conexión en Equipo. Si dejan de llegar mensajes, la lectura deja de considerarse vigente. Los datos anteriores siguen siendo un registro de la sesión, no una lectura actual.")
            InfoAccordion("¿Qué validamos con los usuarios?","La claridad de los estados, la lectura de máximos, la navegación y la comprensión del ADC. Por eso esta versión incluye etiquetas, rangos visibles, resúmenes y explicaciones accesibles.")
        }
    }
}
