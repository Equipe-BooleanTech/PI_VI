import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun PetWiseThemeWrapper(
    theme: edu.fatec.petwise.presentation.theme.ThemeDefinition,
    content: @Composable () -> Unit
) {
    val colors = lightColorScheme(
        primary = Color.fromHex(theme.palette.primary),
        secondary = Color.fromHex(theme.palette.secondary),
        background = Color.fromHex(theme.palette.background),
        onBackground = Color.fromHex(theme.palette.textPrimary)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = theme.typography
    ) {
        content()
    }
}
