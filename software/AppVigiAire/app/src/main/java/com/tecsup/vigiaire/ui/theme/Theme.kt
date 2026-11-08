package com.tecsup.vigiaire.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme get() = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = if (Apariencia.noche.value) androidx.compose.ui.graphics.Color(0xFF123440) else androidx.compose.ui.graphics.Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = AzulOscuro,
    onSecondary = Blanco,
    background = FondoGris,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoGris,
    inverseSurface = TextoOscuro,
    inverseOnSurface = FondoGris,
    outline = TextoGris
)

@Composable
fun VigiAireTheme(
    content: @Composable () -> Unit
) {
    val density=androidx.compose.ui.platform.LocalDensity.current
    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, density.fontScale * if(Apariencia.textoGrande.value) 1.2f else 1f)) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = androidx.compose.material3.Shapes(small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)),
        content = content
    )
    }
}

