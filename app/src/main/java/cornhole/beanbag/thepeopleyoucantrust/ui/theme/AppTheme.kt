package cornhole.beanbag.thepeopleyoucantrust.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Day mode — matches values/themes.xml
private val LightColors = lightColorScheme(
    primary          = Color(0xFFF6EFE3), // custom_tan
    onPrimary        = Color(0xFF000000), // black
    secondary        = Color(0xFF03DAC5), // teal_200
    onSecondary      = Color(0xFF000000), // black
    background       = Color(0xFFF6EFE3), // custom_tan
    onBackground     = Color(0xFF000000), // black
    surface          = Color(0xFFF6EFE3), // custom_tan
    onSurface        = Color(0xFF000000), // black
)

// Night mode — matches values-night/themes.xml
private val DarkColors = darkColorScheme(
    primary          = Color(0xFF000000), // black
    onPrimary        = Color(0xFFFFFFFF), // white
    secondary        = Color(0xFF03DAC5), // teal_200
    onSecondary      = Color(0xFF000000), // black
    background       = Color(0xFF000000), // black
    onBackground     = Color(0xFFFFFFFF), // white
    surface          = Color(0xFF202020), // dark_grey
    onSurface        = Color(0xFFFFFFFF), // white
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
