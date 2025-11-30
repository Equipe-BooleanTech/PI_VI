package edu.fatec.petwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.ThemeDefinition
import edu.fatec.petwise.core.storage.KeyValueStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        
        KeyValueStorage.init(this)

        setContent {
            PetWiseThemeWrapper(theme = PetWiseTheme.Light) {
                App()
            }
        }
    }
}


@Composable
fun PetWiseThemeWrapper(
    theme: ThemeDefinition,
    content: @Composable () -> Unit
) {
    val colors = lightColorScheme(
        primary = Color(theme.palette.primary.toColorInt()),
        secondary = Color(theme.palette.secondary.toColorInt()),
        background = Color(theme.palette.background.toColorInt()),
        onBackground = Color(theme.palette.textPrimary.toColorInt())
    )

    MaterialTheme(
        colorScheme = colors,
        typography = theme.typography
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    PetWiseThemeWrapper(theme = PetWiseTheme.Light) {
        App()
    }
}
