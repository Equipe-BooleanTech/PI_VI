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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val currentTabScreen by navigationManager.currentTabScreen.collectAsState()
    
    val navItems = listOf(
        BottomNavItem("Início", Icons.Default.Home, NavigationManager.TabScreen.Home),
        BottomNavItem("Pets", Icons.Default.Pets, NavigationManager.TabScreen.Pets),
        BottomNavItem("Consultas", Icons.Default.MedicalServices, NavigationManager.TabScreen.Appointments),
        BottomNavItem("Medicação", Icons.Default.Medication, NavigationManager.TabScreen.Medication),
        BottomNavItem("Mais", Icons.Default.Menu, NavigationManager.TabScreen.More)
    )
    
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