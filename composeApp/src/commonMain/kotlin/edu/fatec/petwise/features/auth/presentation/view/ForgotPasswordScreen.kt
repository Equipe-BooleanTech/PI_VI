package edu.fatec.petwise.features.auth.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.features.auth.presentation.forms.forgotPasswordSchema
import edu.fatec.petwise.features.auth.presentation.viewmodel.ForgotPasswordViewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navigationManager: NavigationManager,
    viewModel: ForgotPasswordViewModel = viewModel { ForgotPasswordViewModel() }
) {
    val formConfiguration = remember {
        FormConfiguration(
            id = forgotPasswordSchema.id,
            title = forgotPasswordSchema.title,
            description = forgotPasswordSchema.description,
            fields = forgotPasswordSchema.fields.map { field ->
                FormFieldDefinition(
                    id = field.id,
                    label = field.label,
                    type = when (field.type) {
                        "email" -> FormFieldType.EMAIL
                        "submit" -> FormFieldType.SUBMIT
                        else -> FormFieldType.TEXT
                    },
                    placeholder = field.placeholder,
                    validators = field.validators?.map { validator ->
                        ValidationRule(
                            type = when (validator.type) {
                                "required" -> ValidationType.REQUIRED
                                "email" -> ValidationType.EMAIL
                                else -> ValidationType.CUSTOM
                            },
                            message = validator.message,
                            value = validator.value,
                            field = validator.field
                        )
                    } ?: emptyList()
                )
            },
            styling = FormStyling(
                primaryColor = "#00b942", 
                errorColor = "#d32f2f",
                successColor = "#00b942"
            )
        )
    }
    
    val formViewModel = viewModel<DynamicFormViewModel> {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val theme = PetWiseTheme.Light
    
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

        Card(
            modifier = Modifier
                .widthIn(max = cardWidth)
                .heightIn(max = screenHeight * 0.9f)
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.fromHex(theme.palette.cardBackground))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        navigationManager.navigateTo(NavigationManager.Screen.Auth)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (uiState.requestSent) {
                    SuccessContent(
                        message = uiState.successMessage ?: "Link de recuperação enviado com sucesso!",
                        onBackToLogin = { navigationManager.navigateTo(NavigationManager.Screen.Auth) }
                    )
                } else {
                    Text(
                        text = "Recuperar Senha",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Digite seu email para receber instruções de redefinição de senha.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    uiState.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    DynamicForm(
                        viewModel = formViewModel,
                        colorScheme = MaterialTheme.colorScheme.copy(
                            primary = Color.fromHex(theme.palette.primary),
                            error = Color.fromHex("#d32f2f")
                        ),
                        onSubmitSuccess = { values ->
                            val email = values["email"]?.toString() ?: ""
                            viewModel.requestPasswordReset(email)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    message: String,
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Sucesso",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Sucesso!",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onBackToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
                        Text("Voltar para Login")
        }
    }
}
