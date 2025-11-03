package edu.fatec.petwise.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.DashboardScreen
import edu.fatec.petwise.navigation.NavigationManager

@Composable
fun HomeScreen(
    navigationManager: NavigationManager
) {
    DashboardScreen(
        navigationManager = navigationManager,
        userType = UserType.OWNER
    )
}