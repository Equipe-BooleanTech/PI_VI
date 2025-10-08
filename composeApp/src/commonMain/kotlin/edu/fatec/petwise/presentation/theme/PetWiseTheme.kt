package edu.fatec.petwise.presentation.theme

import androidx.compose.material3.Typography

data class Palette(
    val primary: String,
    val primaryVariant: String,
    val secondary: String,
    val background: String,
    val textPrimary: String,
    val textSecondary: String = "#666666",
    val success: String = primary,
    val successDark: String = "#009e36",
    val accent: String = primary,
    val border: String = primary,
    val inputBackground: String = "#ffffff",
    val cardBackground: String = "#e8e8ea"
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
            primary = "#00b942",
            primaryVariant = "#00d94e",
            secondary = "#f2f2f4",
            background = "#2d3339",
            textPrimary = "#333333",
            textSecondary = "#666666",
            success = "#00b942",
            successDark = "#009e36",
            accent = "#00b942",
            border = "#00b942",
            inputBackground = "#ffffff",
            cardBackground = "#e8e8ea"
        ),
        typography = Typography(),
        spacing = Spacing()
    )


    val Dark = ThemeDefinition(
        palette = Palette(
            primary = "#00b942",
            primaryVariant = "#00d94e",
            secondary = "#f2f2f4",
            background = "#2d3339",
            textPrimary = "#333333",
            textSecondary = "#666666",
            success = "#00b942",
            successDark = "#009e36",
            accent = "#00b942",
            border = "#00b942",
            inputBackground = "#ffffff",
            cardBackground = "#e8e8ea"
        ),
        typography = Typography(),
        spacing = Spacing()
    )
}