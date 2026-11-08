package com.tecsup.vigiaire.ui.screens.auth
import com.tecsup.vigiaire.ui.components.GlassCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.tecsup.vigiaire.data.repository.Repositorio
import com.tecsup.vigiaire.ui.components.*
import com.tecsup.vigiaire.ui.theme.*
@Composable
fun LoginScreen(onLoginExitoso: () -> Unit) {
 var email by rememberSaveable { mutableStateOf("demo@vigiaire.com") }
 var password by rememberSaveable { mutableStateOf("123456") }
 var visible by rememberSaveable { mutableStateOf(false) }
 var error by rememberSaveable { mutableStateOf(false) }
 Surface(color=FondoGris) {
  Column(Modifier.fillMaxSize().statusBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp, Alignment.CenterVertically),horizontalAlignment=Alignment.CenterHorizontally) {
   Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) { VigiAireLogo(size=110.dp,showGlow=true); Text("VigiAire",style=MaterialTheme.typography.headlineMedium) }
   
   Text("Tu espacio, más claro.",style=MaterialTheme.typography.headlineMedium)
   Text("Explora las lecturas, reconoce los cambios y comprende cada estado.",style=MaterialTheme.typography.bodyLarge,color=TextoGris,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
   GlassCard(shape=RoundedCornerShape(30.dp), border=BorderStroke(1.dp, AzulPrimario.copy(alpha=0.25f)), colors=CardDefaults.cardColors(containerColor=Blanco.copy(alpha=0.7f))) {
    Column(Modifier.background(Brush.linearGradient(listOf(AzulClaro.copy(alpha=0.6f),Blanco.copy(alpha=0.3f),com.tecsup.vigiaire.ui.theme.VerdeSuave.copy(alpha=0.4f)))).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
     Text("Inicia sesión",style=MaterialTheme.typography.titleLarge)
     OutlinedTextField(value=email,onValueChange={email=it;error=false},label={Text("Correo electrónico")},leadingIcon={Icon(Icons.Default.Email,null)},shape=RoundedCornerShape(18.dp),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth(),isError=error)
     OutlinedTextField(value=password,onValueChange={password=it;error=false},label={Text("Contraseña")},leadingIcon={Icon(Icons.Default.Lock,null)},trailingIcon={IconButton(onClick={visible=!visible}){Icon(if(visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,if(visible) "Ocultar contraseña" else "Mostrar contraseña")}},shape=RoundedCornerShape(18.dp),singleLine=true,visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth(),isError=error)
     if(error) Text("Revisa tus datos o usa la cuenta de demostración.",color=MaterialTheme.colorScheme.error)
     Button(onClick={if(Repositorio.autenticar(email.trim(),password)) onLoginExitoso() else error=true},colors=ButtonDefaults.buttonColors(containerColor=Color.Transparent,contentColor=Color.White),modifier=Modifier.fillMaxWidth().heightIn(min=56.dp).background(Brush.horizontalGradient(listOf(Color(0xFF166B97),Color(0xFF168875))),RoundedCornerShape(28.dp))) { Text("Entrar a VigiAire") }
    }
   }
   InfoAccordion("Cuenta de demostración","Correo: demo@vigiaire.com\nContraseña: 123456\nExplora el prototipo académico conectado a Wokwi.")
  }
 }
}

