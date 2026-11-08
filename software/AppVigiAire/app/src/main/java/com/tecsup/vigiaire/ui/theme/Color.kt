package com.tecsup.vigiaire.ui.theme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
object Apariencia { val noche = mutableStateOf(false); val textoGrande=mutableStateOf(false); val contraste=mutableStateOf(false) }
fun estadoColor(titulo: String) = when(titulo.uppercase()) { "VENTILE" -> VerdeExito; "ESPERE" -> AmarilloAlerta; "ALERTA" -> RojoAlerta; else -> TextoGris }
fun estadoFondo(titulo: String) = when(titulo.uppercase()) { "VENTILE" -> VerdeSuave; "ESPERE" -> AmarilloSuave; "ALERTA" -> RojoSuave; else -> AzulClaro }
private fun tono(claro: Long, oscuro: Long) = Color(if (Apariencia.noche.value) oscuro else claro)
val AzulPrimario get() = tono(0xFF16799C,0xFF7DD9ED)
val AzulOscuro get() = tono(0xFF173C50,0xFFDEFAFF)
val AzulClaro get() = tono(0xFFDEF3F7,0xFF203D49)
val FondoGris get() = tono(0xFFF1F7F8,0xFF101E25)
val Blanco get() = tono(0xFFF8FCFC,0xFF1B303A)
val TextoOscuro get() = tono(0xFF173743,0xFFE8F7FA)
val TextoGris get() = tono(0xFF526C78,0xFFB8D0D8)
val BordeGris get() = tono(0xFFCDE5E8,0xFF3C5964)
val VerdeExito get() = tono(0xFF147D60,0xFF7EE1BA)
val AmarilloAlerta get() = tono(0xFF976000,0xFFFFCC76)
val RojoAlerta get() = tono(0xFFBC354F,0xFFFF9CAE)
val VerdeSuave get() = tono(0xFFDCF5E9,0xFF183F35)
val AmarilloSuave get() = tono(0xFFFFEBCB,0xFF49391D)
val RojoSuave get() = tono(0xFFFFE1E8,0xFF4B2933)
val AzulSuaveBg get() = tono(0xFFE2F4F0,0xFF203C39)
