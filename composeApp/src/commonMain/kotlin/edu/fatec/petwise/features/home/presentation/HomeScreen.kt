package edu.fatec.petwise.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.DashboardScreen
import edu.fatec.petwise.navigation.NavigationManager

@Composable
fun HomeScreen(
    navigationManager: NavigationManager
) {
    val getUserProfileUseCase = remember { AuthDependencyContainer.provideGetUserProfileUseCase() }
    var userType by remember { mutableStateOf(UserType.OWNER) }
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getUserProfileUseCase.execute().fold(
            onSuccess = { userProfile ->
                userName = userProfile.fullName
                userType = when (userProfile.userType.uppercase()) {
                    "VETERINARY", "VETERINARIAN", "VET" -> UserType.VETERINARY
                    "PETSHOP" -> UserType.PETSHOP
                    "PHARMACY" -> UserType.PHARMACY
                    else -> UserType.OWNER
                }
            },
            onFailure = {
                userType = UserType.OWNER
                userName = ""
            }
        )
    }

    DashboardScreen(
        navigationManager = navigationManager,
        userType = userType,
        userName = userName
    )
}