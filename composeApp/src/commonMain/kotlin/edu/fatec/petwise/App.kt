package edu.fatec.petwise

import androidx.compose.runtime.Composable
import edu.fatec.petwise.features.auth.presentation.AuthScreen
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper

@Composable
fun App() {
    PetWiseThemeWrapper(theme = PetWiseTheme.Light) {
        AuthScreen()
    }
}