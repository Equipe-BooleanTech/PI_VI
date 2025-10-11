package edu.fatec.petwise.presentation.components.NavigationBar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.presentation.theme.AzulProfundo
import edu.fatec.petwise.presentation.theme.Branco
import edu.fatec.petwise.presentation.theme.VerdeMenta

/**
 * Barra de navegação responsiva com identidade visual PetWise.
 */
@Composable
fun ResponsiveNavigationBar() {
    val navItems = listOf(
        NavItem("Início", Icons.Default.Home),
        NavItem("Pets", Icons.Default.Pets),
        NavItem("Consultas", Icons.Default.CalendarToday),
        NavItem("Medicação", Icons.Default.MedicalServices),
        NavItem("Mais", Icons.Default.MoreHoriz),
    )

    BoxWithConstraints {
        if (maxWidth < 900.dp) {
            SmallScreenScaffold(navItems)
        } else {
            LargeScreenTopNavigation(navItems)
        }
    }
}

/**
 * Layout para telas pequenas (< 900dp) com BottomNavigation fixada.
 */
@Composable
private fun SmallScreenScaffold(navItems: List<NavItem>) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(containerColor = AzulProfundo) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selectedIndex == index) VerdeMenta else Branco
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                color = if (selectedIndex == index) VerdeMenta else Branco
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = AzulProfundo
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Conteúdo da aba: ${navItems[selectedIndex].label}")
        }
    }
}

/**
 * Layout para telas grandes (>= 900dp).
 */
@Composable
private fun LargeScreenTopNavigation(navItems: List<NavItem>) {
    Surface(color = AzulProfundo, shadowElevation = 4.dp) {
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
                    TextButton(
                        onClick = {  },
                        colors = ButtonDefaults.textButtonColors(contentColor = Branco)
                    ) {
                        Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {  },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Branco)
                ) {
                    Text("Login")
                }
                Button(
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeMenta, contentColor = Branco)
                ) {
                    Text("Cadastrar")
                }
            }
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
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = VerdeMenta)) {
                append("Wise")
            }
        },
        style = MaterialTheme.typography.titleLarge,
        color = Branco
    )
}

/**
 * Modelo para itens de navegação.
 */
private data class NavItem(
    val label: String,
    val icon: ImageVector
)
