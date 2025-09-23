package edu.fatec.petwise.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Componente de navegação responsiva:
 * - BottomNavigation para telas < 900dp
 * - Top Navigation para telas >= 900dp
 */
@Composable
fun ResponsiveNavigationBar(modifier: Modifier = Modifier) {
    val navItems = listOf(
        NavItem("Início", Icons.Default.Home),
        NavItem("Pets", Icons.Default.Pets),
        NavItem("Consultas", Icons.Default.CalendarToday),
        NavItem("Medicação", Icons.Default.MedicalServices),
        NavItem("Mais", Icons.Default.MoreHoriz),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        BoxWithConstraints {
            if (maxWidth < 900.dp) {
                SmallScreenBottomNavigation(navItems)
            } else {
                LargeScreenTopNavigation(navItems)
            }
        }
    }
}

/**
 * Navegação para telas grandes (>= 900dp).
 * Top AppBar + botões de ação.
 */
@Composable
private fun LargeScreenTopNavigation(navItems: List<NavItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PetWiseLogo()

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            navItems.forEach { item ->
                TextButton(onClick = { /* TODO: Navegação */ }) {
                    Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { /* TODO: Login */ }) {
                Text("Login")
            }
            Button(onClick = { /* TODO: Cadastro */ }) {
                Text("Cadastrar")
            }
        }
    }
}

/**
 * Navegação para telas pequenas (< 900dp).
 * BottomNavigation semelhante à imagem enviada.
 */
@Composable
private fun SmallScreenBottomNavigation(navItems: List<NavItem>) {
    var selectedIndex by remember { mutableStateOf(0) }

    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

/**
 * Logo estilizada "PetWise".
 */
@Composable
private fun PetWiseLogo() {
    Text(
        text = buildAnnotatedString {
            append("Pet")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Wise")
            }
        },
        style = MaterialTheme.typography.titleLarge
    )
}

/**
 * Modelo para itens de navegação.
 */
private data class NavItem(
    val label: String,
    val icon: ImageVector
)
