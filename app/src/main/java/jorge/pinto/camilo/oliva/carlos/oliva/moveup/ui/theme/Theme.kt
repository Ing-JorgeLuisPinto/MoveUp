package jorge.pinto.camilo.oliva.carlos.oliva.moveup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores personalizados
val Primary = Color(0xFF2196F3)
val PrimaryDark = Color(0xFF1976D2)
val Accent = Color(0xFFFF5722)
val GrayLight = Color(0xFFF5F5F5)
val TextDark = Color(0xFF212121)
val TextLight = Color(0xFF757575)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Accent,
    onSecondary = Color.White,
    tertiary = PrimaryDark,
    background = Color.White,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
)

@Composable
fun TransportePublicoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}