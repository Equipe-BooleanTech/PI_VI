package edu.fatec.petwise.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTabScreen = MutableStateFlow<TabScreen>(TabScreen.Home)
    val currentTabScreen: StateFlow<TabScreen> = _currentTabScreen.asStateFlow()

    private val _showMoreMenu = MutableStateFlow(false)
    val showMoreMenu = _showMoreMenu.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateToTab(tab: TabScreen) {
        _currentTabScreen.value = tab
        if (_currentScreen.value != Screen.Dashboard) {
            _currentScreen.value = Screen.Dashboard
        }
    }

    fun toggleMoreMenu() {
        _showMoreMenu.value = !_showMoreMenu.value
    }

    fun hideMoreMenu() {
        _showMoreMenu.value = false
    }

    fun reset() {
        _currentScreen.value = Screen.Auth
        _currentTabScreen.value = TabScreen.Home
        _showMoreMenu.value = false
    }

    sealed class Screen {
        object Splash : Screen()
        object Auth : Screen()
        object Dashboard : Screen()
        object ForgotPassword : Screen()
        data class ResetPassword(val token: String = "") : Screen()
        
    }

    sealed class TabScreen {
        object Home : TabScreen()
        object Pets : TabScreen()
        object Appointments : TabScreen()
        object Medication : TabScreen()
        object More : TabScreen()
        object Settings : TabScreen()
        object Help : TabScreen()
        object Vaccines : TabScreen()
        object Veterinarians : TabScreen()
        object Supplies : TabScreen()
        object Pharmacy : TabScreen()
        object Labs : TabScreen()
        object Suprimentos : TabScreen()
        object Prescriptions : TabScreen()
        object Exams : TabScreen()
        object Food : TabScreen()
        object Hygiene : TabScreen()
        object Toys : TabScreen()
    }
}