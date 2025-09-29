package edu.fatec.petwise.presentation.theme

import androidx.compose.material3.Typography

data class Palette(
    val primary: String,
    val secondary: String,
    val background: String,
    val textPrimary: String,
    val textSecondary: String
)

data class Spacing(
    val small: Int = 4,
    val medium: Int = 8,
    val large: Int = 16,
    val extraLarge: Int = 32
)

data class ThemeDefinition(
    val palette: Palette,
    val typography: Typography,
    val spacing: Spacing
)

object PetWiseTheme {
    val Light = ThemeDefinition(
        palette = Palette(
            primary = "#2C3E50",
            secondary = "#27AE60",
            background = "#FFFFFF",
            textPrimary = "#000000",
            textSecondary = "#555555"
        ),
        typography = Typography(),
        spacing = Spacing()
    )

    val Dark = Light.copy(
        palette = Light.palette.copy(
            background = "#121212",
            textPrimary = "#FFFFFF",
            textSecondary = "#AAAAAA"
        )
    )
}
