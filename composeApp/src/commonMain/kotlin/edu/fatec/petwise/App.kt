package edu.fatec.petwise

import PetWiseThemeWrapper
import androidx.compose.runtime.Composable
import edu.fatec.petwise.features.auth.presentation.LoginScreen
import edu.fatec.petwise.presentation.theme.PetWiseTheme

@Composable
fun App() {
    PetWiseThemeWrapper(theme = PetWiseTheme.Light) {
        LoginScreen()
    }
}