package edu.fatec.petwise.features.medications.presentation.components

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
import edu.fatec.petwise.features.medications.domain.models.*
import edu.fatec.petwise.features.medications.presentation.forms.addMedicationFormConfiguration
import edu.fatec.petwise.features.medications.presentation.forms.createEditMedicationFormConfiguration
import edu.fatec.petwise.features.medications.presentation.viewmodel.AddMedicationViewModel
import edu.fatec.petwise.features.medications.presentation.viewmodel.UpdateMedicationViewModel
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun AddMedicationDialog(
    addMedicationViewModel: AddMedicationViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val addMedicationState by addMedicationViewModel.uiState.collectAsState()

    val formConfiguration = addMedicationFormConfiguration

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_medication_form") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(addMedicationState.isSuccess) {
        if (addMedicationState.isSuccess) {
            formViewModel.resetForm()
            onSuccess()
        }
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
                        color = Color.fromHex(theme.palette.primary),
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
                                text = "Adicionar Medicamento",
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
                                        color = Color.fromHex(theme.palette.primary)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Adicionando medicamento...",
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
                                    addMedicationViewModel.addMedication(formData)
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
fun EditMedicationDialog(
    medication: Medication,
    updateMedicationViewModel: UpdateMedicationViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val updateMedicationState by updateMedicationViewModel.uiState.collectAsState()

    val formConfiguration = createEditMedicationFormConfiguration(medication)

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_medication_form_${medication.id}") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(updateMedicationState.isSuccess) {
        if (updateMedicationState.isSuccess) {
            onSuccess()
        }
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
                        color = Color.fromHex(theme.palette.primary),
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
                                text = "Editar Medicamento",
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
                                        color = Color.fromHex(theme.palette.primary)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Atualizando medicamento...",
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
                                    updateMedicationViewModel.updateMedication(medication.id, formData)
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