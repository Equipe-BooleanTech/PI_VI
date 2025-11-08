package edu.fatec.petwise.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.auth.presentation.viewmodel.AuthViewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper
import edu.fatec.petwise.presentation.theme.fromHex
import org.jetbrains.compose.ui.tooling.preview.Preview
import edu.fatec.petwise.features.auth.presentation.forms.registerFormConfiguration
import edu.fatec.petwise.features.auth.presentation.forms.loginFormConfiguration
import kotlinx.serialization.json.JsonPrimitive


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navigationManager: NavigationManager,
    authViewModel: AuthViewModel = remember { AuthDependencyContainer.provideAuthViewModel() }
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Login", "Registrar")

    val formConfiguration = if (selectedTab == 0) loginFormConfiguration else registerFormConfiguration

    val formViewModel = remember(selectedTab) {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    val authUiState by authViewModel.uiState.collectAsState()
    val theme = PetWiseTheme.Light

    LaunchedEffect(selectedTab) {
        println("AuthScreen: Tab changed to $selectedTab, clearing previous errors")
        authViewModel.clearError()
    }

    LaunchedEffect(authUiState.isAuthenticated) {
        if (authUiState.isAuthenticated) {
            println("AuthScreen: User authenticated, navigating to Dashboard")
            navigationManager.navigateTo(NavigationManager.Screen.Dashboard)
        }
    }

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

                    authUiState.successMessage?.let { successMsg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.fromHex("#4CAF50").copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = successMsg,
                                color = Color.fromHex("#2E7D32"),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    authUiState.errorMessage?.let { errorMsg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    key(formConfiguration.id) {
                        DynamicForm(
                            viewModel = formViewModel,
                            colorScheme = MaterialTheme.colorScheme.copy(
                                primary = Color.fromHex(theme.palette.primary),
                                error = Color.fromHex("#d32f2f")
                            ),
                            onSubmitSuccess = { values ->
                                if (selectedTab == 0) {
                                    val email = values["email"]?.toString() ?: ""
                                    val password = values["password"]?.toString() ?: ""
                                    authViewModel.login(email, password)
                                } else {
                                    val registerRequest = edu.fatec.petwise.core.network.dto.RegisterRequest(
                                        email = values["email"]?.toString() ?: "",
                                        password = values["password"]?.toString() ?: "",
                                        fullName = values["fullName"]?.toString() ?: "",
                                        userType = values["userType"]?.toString() ?: "",
                                        phone = values["phone"]?.toString(),
                                        cpf = values["cpf"]?.toString(),
                                        cnpj = values["cnpj"]?.toString(),
                                        specialization = values["specialization"]?.toString(),
                                        companyName = values["companyName"]?.toString(),
                                        crmv = values["crmv"]?.toString(),
                                        adminCode = values["adminCode"]?.toString(),
                                        active = values["active"]?.toString()?.toBoolean()
                                    )
                                    authViewModel.register(registerRequest)
                                }
                            }
                        )
                    }

                    if (selectedTab == 0) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Esqueceu sua senha?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.fromHex(theme.palette.primary),
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .clickable {
                                    navigationManager.navigateTo(NavigationManager.Screen.ForgotPassword)
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}