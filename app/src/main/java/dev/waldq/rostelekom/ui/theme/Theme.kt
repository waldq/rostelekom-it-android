package dev.waldq.rostelekom.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme( // TODO это затычка, в тёмной теме цвета могут отличаться
    primary = Purple,
    onPrimary = White,
    primaryContainer = WhiteF0E5FF,
    secondary = Orange,
    background = WhiteF5F6F8,
    surface = White,
    onSurface = Graphite,
    onSurfaceVariant = Gray,
    outline = WhiteD9DCE2,
    error = DarkOrange,
    errorContainer = WhiteFDE8E8
)

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    primaryContainer = WhiteF0E5FF,
    secondary = Orange,
    background = WhiteF5F6F8,
    surface = White,
    onSurface = Graphite,
    onSurfaceVariant = Gray,
    outline = WhiteD9DCE2,
    error = DarkOrange,
    errorContainer = WhiteFDE8E8
)

@Composable
fun RostelekomitandroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // TODO полноценной тёмной темы пока нет, поэтому ограничил до чисто светлой
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}