package edu.fatec.petwise.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalPalette = staticCompositionLocalOf { Palette("#000000", "#000000", "#FFFFFF", "#000000", "#555555") }

@Composable
fun PetWiseThemeWrapper(
    theme: ThemeDefinition,
    content: @Composable () -> Unit
) {
    val colors = lightColorScheme(
        primary = Color.fromHex(theme.palette.primary),
        secondary = Color.fromHex(theme.palette.secondary),
        background = Color.fromHex(theme.palette.background),
        surface = Color.fromHex(theme.palette.background),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.fromHex(theme.palette.textPrimary),
        onSurface = Color.fromHex(theme.palette.textPrimary),
        error = Color(0xFFB00020),
        onError = Color.White
    )

    CompositionLocalProvider(
        LocalSpacing provides theme.spacing,
        LocalPalette provides theme.palette
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = theme.typography,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}
