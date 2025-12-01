package edu.fatec.petwise.features.profile.presentation.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.features.profile.di.ProfileDependencyContainer
import edu.fatec.petwise.features.profile.presentation.forms.editProfileFormConfiguration
import edu.fatec.petwise.features.profile.presentation.viewmodel.EditProfileViewModel
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.presentation.shared.form.FormFieldDefinition
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navigationManager: NavigationManager,
    viewModel: EditProfileViewModel = remember { ProfileDependencyContainer.provideEditProfileViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val theme = PetWiseTheme.Light
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditConfirmDialog by remember { mutableStateOf(false) }
    var pendingUpdateRequest by remember { mutableStateOf<UpdateProfileRequest?>(null) }
    var emailWillChange by remember { mutableStateOf(false) }
    
    val userProfileKey = uiState.userProfile?.id
    
    val formConfiguration = remember(uiState.userProfile?.userType, userProfileKey) {
        uiState.userProfile?.let { profile ->
            val fieldsToInclude = when (profile.userType.uppercase()) {
                "OWNER" -> listOf("fullName", "email", "phone", "cpf", "submitEditProfile")
                "VETERINARY" -> listOf("fullName", "email", "phone", "crmv", "specialization", "submitEditProfile")
                "PHARMACY", "PETSHOP" -> listOf("fullName", "email", "phone", "cnpj", "companyName", "submitEditProfile")
                else -> listOf("fullName", "email", "phone", "submitEditProfile")
            }
            
            editProfileFormConfiguration.copy(
                fields = editProfileFormConfiguration.fields.filter { field ->
                    field.id in fieldsToInclude
                }
            )
        } ?: editProfileFormConfiguration
    }
    
    val formViewModel = remember(formConfiguration, userProfileKey) {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    LaunchedEffect(uiState.userProfile, userProfileKey) {
        uiState.userProfile?.let { profile ->
            println("EditProfileScreen: Loading user data into form - ${profile.fullName}")
            
            formViewModel.updateFieldValue("fullName", profile.fullName)
            formViewModel.updateFieldValue("email", profile.email)
            formViewModel.updateFieldValue("phone", profile.phone ?: "")
        } ?: run {
            println("EditProfileScreen: User profile is null, resetting form")
            formViewModel.resetForm()
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        style = theme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.fromHex("#1a1a1a")
                    )
                },
                navigationIcon = {
                    Text(
                        text = "←",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.fromHex("#1a1a1a")
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
        ) {
            if (uiState.isLoading && uiState.userProfile == null) {
                
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.fromHex("#00b942")
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    
                    uiState.successMessage?.let { message ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.fromHex("#34C759").copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = message,
                                color = Color.fromHex("#34C759"),
                                style = theme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    
                    uiState.errorMessage?.let { message ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.fromHex("#FF3B30").copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = message,
                                color = Color.fromHex("#FF3B30"),
                                style = theme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    
                    DynamicForm(
                        viewModel = formViewModel,
                        modifier = Modifier.fillMaxWidth(),
                        onSubmitSuccess = { values ->
                            println("EditProfileScreen: Form submitted with values: $values")
                            
                            val updateRequest = UpdateProfileRequest(
                                fullName = values["fullName"]?.toString(),
                                email = values["email"]?.toString(),
                                phone = values["phone"]?.toString()?.takeIf { it.isNotBlank() },
                                cpf = values["cpf"]?.toString()?.takeIf { it.isNotBlank() },
                                crmv = values["crmv"]?.toString()?.takeIf { it.isNotBlank() },
                                specialization = values["specialization"]?.toString()?.takeIf { it.isNotBlank() },
                                cnpj = values["cnpj"]?.toString()?.takeIf { it.isNotBlank() },
                                companyName = values["companyName"]?.toString()?.takeIf { it.isNotBlank() }
                            )
                            
                            val currentEmail = uiState.userProfile?.email
                            val newEmail = updateRequest.email
                            emailWillChange = currentEmail != null && newEmail != null && currentEmail != newEmail
                            
                            pendingUpdateRequest = updateRequest
                            showEditConfirmDialog = true
                        },
                        onSubmitError = { error ->
                            println("EditProfileScreen: Form submission error - ${error.message}")
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#FF3B30").copy(alpha = 0.05f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Zona de Perigo",
                                style = theme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.fromHex("#FF3B30")
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "A exclusão da conta é permanente e não pode ser desfeita. Todos os seus dados serão removidos do sistema.",
                                style = theme.typography.bodySmall,
                                color = Color.fromHex("#666666"),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            Button(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.fromHex("#FF3B30")
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Excluir Conta",
                                    style = theme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            
            if (uiState.isLoading && uiState.userProfile != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.fromHex("#00b942")
                            )
                            Text(
                                text = "Atualizando perfil...",
                                style = theme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Confirmar Exclusão",
                    style = theme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita e todos os seus dados serão permanentemente removidos.",
                    style = theme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteProfile()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex("#FF3B30")
                    )
                ) {
                    Text(
                        text = "Excluir",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Edit confirmation dialog
    if (showEditConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                showEditConfirmDialog = false
                pendingUpdateRequest = null
            },
            title = {
                Text(
                    text = "Confirmar Alterações",
                    style = theme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tem certeza de que deseja salvar as alterações no seu perfil?",
                        style = theme.typography.bodyMedium
                    )
                    if (emailWillChange) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.fromHex("#FF9500").copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "⚠️",
                                    style = theme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Você está alterando seu e-mail. Após confirmar, você será desconectado e precisará fazer login novamente com o novo e-mail.",
                                    style = theme.typography.bodySmall,
                                    color = Color.fromHex("#FF9500")
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEditConfirmDialog = false
                        pendingUpdateRequest?.let { request ->
                            viewModel.updateProfile(request)
                        }
                        pendingUpdateRequest = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex("#00b942")
                    )
                ) {
                    Text(
                        text = "Confirmar",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { 
                        showEditConfirmDialog = false
                        pendingUpdateRequest = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
