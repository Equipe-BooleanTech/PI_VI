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

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    onItemSelected: (String) -> Unit,
    selectedRoute: String = "home"
) {
    val navItems = listOf(
        BottomNavItem("Início", Icons.Default.Home, "home"),
        BottomNavItem("Pets", Icons.Default.Pets, "pets"),
        BottomNavItem("Consultas", Icons.Default.MedicalServices, "appointments"),
        BottomNavItem("Medicação", Icons.Default.Medication, "medication"),
        BottomNavItem("Mais", Icons.Default.Menu, "more")
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
                val isSelected = selectedRoute == item.route
                val itemColor = if (isSelected) Color.fromHex("#00b942") else Color.Gray
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelected(item.route) },
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

private fun Color.Companion.fromHex(colorString: String): Color {
    val hex = colorString.removePrefix("#")
    val red = hex.substring(0, 2).toInt(16)
    val green = hex.substring(2, 4).toInt(16)
    val blue = hex.substring(4, 6).toInt(16)
    return Color(red, green, blue)
}