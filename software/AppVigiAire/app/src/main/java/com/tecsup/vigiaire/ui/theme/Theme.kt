package com.tecsup.vigiaire.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = AzulOscuro,
    onSecondary = Blanco,
    background = FondoGris,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    outline = BordeGris
)

@Composable
fun VigiAireTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
