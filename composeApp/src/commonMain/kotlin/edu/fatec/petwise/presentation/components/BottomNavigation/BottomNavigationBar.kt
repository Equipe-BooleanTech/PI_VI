package edu.fatec.petwise.presentation.components.BottomNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.fromHex

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val tabScreen: NavigationManager.TabScreen
)

@Composable
fun BottomNavigationBar(
    navigationManager: NavigationManager
) {
    val getUserProfileUseCase = remember { AuthDependencyContainer.provideGetUserProfileUseCase() }
    var userType by remember { mutableStateOf("OWNER") }
    val currentTabScreen by navigationManager.currentTabScreen.collectAsState()

    LaunchedEffect(Unit) {
        getUserProfileUseCase.execute().fold(
            onSuccess = { userProfile ->
                // Normalize user type the same way as DashboardViewModel
                val normalizedUserType = when (userProfile.userType.uppercase()) {
                    "VETERINARY", "VETERINARIAN", "VET" -> "VETERINARY"
                    "PETSHOP" -> "PETSHOP"
                    "PHARMACY" -> "PHARMACY"
                    else -> "OWNER"
                }
                userType = normalizedUserType
                println("BottomNavigationBar - userType loaded: ${userProfile.userType}, normalized: $normalizedUserType")
            },
            onFailure = {
                userType = "OWNER"
                println("BottomNavigationBar - failed to load userType: ${it.message}")
            }
        )
    }

    println("BottomNavigationBar - current userType: $userType")

    val navItems = when (userType.uppercase()) {
        "VETERINARY" -> listOf(
            BottomNavItem("Início", Icons.Default.Home, NavigationManager.TabScreen.Home),
            BottomNavItem("Consultas", Icons.Default.MedicalServices, NavigationManager.TabScreen.Appointments),
            BottomNavItem("Vacinas", Icons.Default.HealthAndSafety, NavigationManager.TabScreen.Vaccines),
            BottomNavItem("Mais", Icons.Default.Menu, NavigationManager.TabScreen.More)
        )
        "PHARMACY" -> listOf(
            BottomNavItem("Início", Icons.Default.Home, NavigationManager.TabScreen.Home),
            BottomNavItem("Medicamentos", Icons.Default.Medication, NavigationManager.TabScreen.Medication),
            BottomNavItem("Mais", Icons.Default.Menu, NavigationManager.TabScreen.More)
        )
        "PETSHOP" -> listOf(
            BottomNavItem("Início", Icons.Default.Home, NavigationManager.TabScreen.Home),
            BottomNavItem("Produtos", Icons.Default.ShoppingCart, NavigationManager.TabScreen.Food),
            BottomNavItem("Mais", Icons.Default.Menu, NavigationManager.TabScreen.More)
        )
        else -> listOf( // OWNER
            BottomNavItem("Início", Icons.Default.Home, NavigationManager.TabScreen.Home),
            BottomNavItem("Pets", Icons.Default.Pets, NavigationManager.TabScreen.Pets),
            BottomNavItem("Mais", Icons.Default.Menu, NavigationManager.TabScreen.More)
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentTabScreen == item.tabScreen
                val itemColor = if (isSelected) Color.fromHex("#00b942") else Color.Gray

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navigationManager.navigateToTab(item.tabScreen)
                            if (item.tabScreen == NavigationManager.TabScreen.More) {
                                navigationManager.toggleMoreMenu()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = itemColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.label,
                            color = itemColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}