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
import edu.fatec.petwise.features.auth.presentation.forms.loginSchema
import edu.fatec.petwise.features.auth.shared.DynamicAuthFormScreen
import edu.fatec.petwise.features.auth.shared.FormStore
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper
import edu.fatec.petwise.presentation.theme.fromHex
import org.jetbrains.compose.ui.tooling.preview.Preview
import registerSchema

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Login", "Registrar")

    val schema = if (selectedTab == 0) loginSchema else registerSchema
    val formStore = remember(schema.id) { FormStore(schema) }

    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light

    PetWiseThemeWrapper(theme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.fromHex(theme.palette.background)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(max = 400.dp),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex(theme.palette.cardBackground)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
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

                    DynamicAuthFormScreen(formStore)
                }
            }
        }
    }
}