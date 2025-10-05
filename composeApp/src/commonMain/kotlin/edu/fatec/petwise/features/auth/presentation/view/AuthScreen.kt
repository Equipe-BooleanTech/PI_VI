package edu.fatec.petwise.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import edu.fatec.petwise.features.auth.shared.DynamicAuthFormScreen
import edu.fatec.petwise.features.auth.shared.FormStore
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper
import edu.fatec.petwise.presentation.theme.fromHex
import org.jetbrains.compose.ui.tooling.preview.Preview
import edu.fatec.petwise.features.auth.presentation.forms.registerSchema
import edu.fatec.petwise.features.auth.presentation.forms.loginSchema


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navigationManager: NavigationManager) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Login", "Registrar")

    val schema = if (selectedTab == 0) loginSchema else registerSchema

    val formStore = remember(selectedTab) { FormStore(schema) }

    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light

    PetWiseThemeWrapper(theme) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.fromHex(theme.palette.background)),
            contentAlignment = Alignment.Center
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            
            val cardWidth = when {
                screenWidth < 600.dp -> screenWidth * 0.9f
                screenWidth < 840.dp -> screenWidth * 0.7f
                else -> 500.dp
            }
            
            val cardPadding = when {
                screenWidth < 400.dp -> 16.dp
                screenWidth < 600.dp -> 20.dp
                else -> 24.dp
            }
            
            Card(
                modifier = Modifier
                    .padding(cardPadding)
                    .widthIn(max = cardWidth)
                    .heightIn(max = screenHeight * 0.9f),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex(theme.palette.cardBackground)
                )
            ) {
                val innerPadding = when {
                    screenWidth < 400.dp -> 16.dp
                    screenWidth < 600.dp -> 24.dp
                    else -> 32.dp
                }
                
                Column(
                    modifier = Modifier.padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PetWise",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.fromHex(theme.palette.primary),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Sistema de Gestão Veterinária",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex(theme.palette.primary)
                        )
                    )
                    Spacer(Modifier.height(24.dp))

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        titles.forEachIndexed { index, title ->
                            SegmentedButton(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                shape = SegmentedButtonDefaults.itemShape(index, titles.size),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color.Transparent,
                                    activeContentColor = Color.fromHex(theme.palette.textPrimary),
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = Color.fromHex(theme.palette.textSecondary)
                                )
                            ) {
                                Text(title)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    DynamicAuthFormScreen(
                        formStore = formStore,
                        onLoginSuccess = { 
                            navigationManager.navigateTo(NavigationManager.Screen.Dashboard)
                        }
                    )
                }
            }
        }
    }
}