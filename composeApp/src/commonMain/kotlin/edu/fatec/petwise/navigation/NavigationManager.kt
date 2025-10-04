package edu.fatec.petwise.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NavigationManager {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Auth)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()
    
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
    

    sealed class Screen {
        object Auth : Screen()
        object Home : Screen()
        data class Profile(val userId: String? = null) : Screen()
    }
}