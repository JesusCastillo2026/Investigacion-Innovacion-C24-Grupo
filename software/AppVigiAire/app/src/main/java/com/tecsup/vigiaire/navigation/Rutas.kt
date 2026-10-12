package com.tecsup.vigiaire.navigation

sealed class Pantalla(val ruta: String) {
    object Splash : Pantalla("splash")
    object Login : Pantalla("login")
    object Home : Pantalla("home")
    object Detalle : Pantalla("detalle")
    object Recomendacion : Pantalla("recomendacion")
    object Tendencia : Pantalla("tendencia")
    object Dispositivo : Pantalla("dispositivo")
    object Perfil : Pantalla("perfil")
}
