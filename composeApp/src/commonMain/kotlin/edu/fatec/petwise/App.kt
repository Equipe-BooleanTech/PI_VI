package edu.fatec.petwise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import edu.fatec.petwise.features.auth.presentation.AuthScreen
import edu.fatec.petwise.features.auth.presentation.view.ForgotPasswordScreen
import edu.fatec.petwise.features.auth.presentation.view.ResetPasswordScreen
import edu.fatec.petwise.features.home.presentation.HomeScreen
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper

@Composable
fun App() {

    val navigationManager = remember { NavigationManager() }

    val currentScreen = navigationManager.currentScreen.collectAsState()

    PetWiseThemeWrapper(theme = PetWiseTheme.Light) {

        when (val screen = currentScreen.value) {
            is NavigationManager.Screen.Auth -> {
                AuthScreen(navigationManager)
            }
            is NavigationManager.Screen.Dashboard -> {
                HomeScreen(navigationManager)
            }
            
            is NavigationManager.Screen.ForgotPassword -> {
                ForgotPasswordScreen(navigationManager)
            }
            is NavigationManager.Screen.ResetPassword -> {
                ResetPasswordScreen(navigationManager, screen.token)
            }
            is NavigationManager.Screen.Splash -> {

                AuthScreen(navigationManager)
            }
        }
    }
}