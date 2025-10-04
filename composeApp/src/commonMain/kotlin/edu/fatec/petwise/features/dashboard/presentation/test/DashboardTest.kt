package edu.fatec.petwise.features.dashboard.presentation.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.DashboardScreen
import edu.fatec.petwise.navigation.NavigationManager

/**
 * A test component that allows switching between different user types
 * to see how the dashboard adapts to different user roles.
 */
@Composable
fun DashboardTest(navigationManager: NavigationManager) {
    val userType = remember { mutableStateOf(UserType.OWNER) }
    
    DashboardScreen(
        navigationManager = navigationManager,
        userName = "Usuário de Teste",
        userType = userType.value
    )
}