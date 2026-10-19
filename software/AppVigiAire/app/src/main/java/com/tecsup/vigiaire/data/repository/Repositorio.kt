package com.tecsup.vigiaire.data.repository

import com.tecsup.vigiaire.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Conserva únicamente el usuario de demostración; las lecturas ya llegan desde MQTT. */
object Repositorio {
    private val _usuarioActual = MutableStateFlow<Usuario?>(
        Usuario(email = "demo@vigiaire.com", nombre = "Jeremy")
    )
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun autenticar(correo: String, clave: String): Boolean {
        return if (correo.trim().equals("demo@vigiaire.com", ignoreCase = true) && clave == "123456") {
            _usuarioActual.value = Usuario(email = "demo@vigiaire.com", nombre = "Jeremy")
            true
        } else false
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
