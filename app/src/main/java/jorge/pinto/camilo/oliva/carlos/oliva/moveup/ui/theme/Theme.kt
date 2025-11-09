package jorge.pinto.camilo.oliva.carlos.oliva.moveup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores del mockup
val PrimaryBlue = Color(0xFF2196F3)
val PrimaryDark = Color(0xFF1976D2)
val AccentOrange = Color(0xFFFF9800)
val BackgroundLight = Color(0xFFF5F5F5)
val TextDark = Color(0xFF212121)
val TextLight = Color(0xFF757575)
val White = Color(0xFFFFFFFF)
val GrayLight = Color(0xFFE0E0E0)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    secondary = AccentOrange,
    onSecondary = White,
    background = White,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextLight
)

@Composable
fun TransportePublicoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}