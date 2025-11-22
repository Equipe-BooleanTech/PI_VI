package edu.fatec.petwise.features.prescriptions.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.presentation.forms.addPrescriptionFormConfiguration
import edu.fatec.petwise.features.prescriptions.presentation.forms.createEditPrescriptionFormConfiguration
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.shared.form.SelectOption
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetsUiEvent
import kotlinx.coroutines.*
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.prescriptions.di.PrescriptionDependencyContainer

@Composable
fun AddPrescriptionDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()
    
    val getUserProfileUseCase = remember { AuthDependencyContainer.provideGetUserProfileUseCase() }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        petsViewModel.onEvent(PetsUiEvent.LoadPets)
        val userProfileResult = getUserProfileUseCase.execute()
        currentUserId = userProfileResult.getOrNull()?.id
    }
    
    val petOptions = remember(petsState.pets) {
        petsState.pets.map { pet ->
            SelectOption(
                key = pet.id,
                value = "${pet.name} - ${pet.ownerName}"
            )
        }
    }

    val formConfiguration = remember(petOptions) {
        addPrescriptionFormConfiguration(petOptions)
    }

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_prescription_form") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(petOptions) {
        formViewModel.updateConfiguration(addPrescriptionFormConfiguration(petOptions))
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dialogHeight = with(LocalDensity.current) {
                (screenHeight.toPx() * 0.9f).toDp()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        color = Color.fromHex("#673AB7"),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Adicionar Prescrição",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            if (!isLoading) {
                                IconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fechar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.fromHex("#673AB7")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Adicionando prescrição...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.fromHex(theme.palette.textSecondary)
                                        )
                                    )
                                }
                            }
                        } else {
                            DynamicForm(
                                viewModel = formViewModel,
                                modifier = Modifier.fillMaxSize(),
                                onSubmitSuccess = { formData ->
                                    val updatedFormData = formData.toMutableMap()
                                    currentUserId?.let { userId ->
                                        updatedFormData["veterinarian"] = userId
                                    }
                                    onSuccess(updatedFormData)
                                },
                                onSubmitError = { error ->
                                    println("Erro no formulário: ${error.message}")
                                }
                            )
                        }

                        errorMessage?.let { message ->
                            LaunchedEffect(message) {
                                kotlinx.coroutines.delay(3000)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditPrescriptionDialog(
    prescription: Prescription,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        petsViewModel.onEvent(PetsUiEvent.LoadPets)
    }
    
    val petOptions = remember(petsState.pets) {
        petsState.pets.map { pet ->
            SelectOption(
                key = pet.id,
                value = "${pet.name} - ${pet.ownerName}"
            )
        }
    }

    val formConfiguration = remember(prescription, petOptions) {
        createEditPrescriptionFormConfiguration(prescription).copy(
            fields = createEditPrescriptionFormConfiguration(prescription).fields.map { field ->
                if (field.id == "petId") {
                    field.copy(selectOptions = petOptions)
                } else {
                    field
                }
            }
        )
    }

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_prescription_form_${prescription.id}") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(prescription, petOptions) {
        formViewModel.resetForm()
        formViewModel.updateConfiguration(
            createEditPrescriptionFormConfiguration(prescription).copy(
                fields = createEditPrescriptionFormConfiguration(prescription).fields.map { field ->
                    if (field.id == "petId") {
                        field.copy(selectOptions = petOptions)
                    } else {
                        field
                    }
                }
            )
        )
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dialogHeight = with(LocalDensity.current) {
                (screenHeight.toPx() * 0.9f).toDp()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        color = Color.fromHex("#673AB7"),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Editar Prescrição",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            if (!isLoading) {
                                IconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fechar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.fromHex("#673AB7")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Atualizando prescrição...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.fromHex(theme.palette.textSecondary)
                                        )
                                    )
                                }
                            }
                        } else {
                            DynamicForm(
                                viewModel = formViewModel,
                                modifier = Modifier.fillMaxSize(),
                                onSubmitSuccess = { formData ->
                                    onSuccess(formData)
                                },
                                onSubmitError = { error ->
                                    println("Erro no formulário: ${error.message}")
                                }
                            )
                        }

                        errorMessage?.let { message ->
                            LaunchedEffect(message) {
                                kotlinx.coroutines.delay(3000)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeletePrescriptionConfirmationDialog(
    prescriptionId: String,
    prescriptionName: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val theme = PetWiseTheme.Light

    AlertDialog(
        onDismissRequest = { if (!isLoading) onCancel() },
        title = {
            Text(
                text = "Confirmar Exclusão",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Tem certeza que deseja excluir a prescrição \"$prescriptionName\"?\n\nEsta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val result: Result<Unit> = PrescriptionDependencyContainer.deletePrescriptionUseCase(prescriptionId)
                        result.fold(
                            onSuccess = {
                                DataRefreshManager.notifyPrescriptionsUpdated()
                                onSuccess()
                            },
                            onFailure = { error ->
                                errorMessage = error.message ?: "Erro ao excluir prescrição"
                            }
                        )
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#F44336"),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Excluir")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isLoading
            ) {
                Text(
                    "Cancelar",
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}