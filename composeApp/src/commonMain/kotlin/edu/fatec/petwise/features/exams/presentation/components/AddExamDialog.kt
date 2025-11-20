package edu.fatec.petwise.features.exams.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.fatec.petwise.features.exams.presentation.forms.addExamFormConfiguration
import edu.fatec.petwise.features.exams.presentation.viewmodel.AddExamViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.AddExamUiEvent
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.shared.form.SelectOption
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamDialog(
    addExamViewModel: AddExamViewModel,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val theme = PetWiseTheme.Light

    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        petsViewModel.onEvent(edu.fatec.petwise.features.pets.presentation.viewmodel.PetsUiEvent.LoadPets)
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
        addExamFormConfiguration.copy(
            fields = addExamFormConfiguration.fields.map { field ->
                if (field.id == "petId") {
                    field.copy(selectOptions = petOptions)
                } else {
                    field
                }
            }
        )
    }

    val formViewModel = remember(formConfiguration) {
        DynamicFormViewModel(formConfiguration)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adicionar Exame",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex("#2196F3")
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.Gray
                        )
                    }
                }

                // Form
                DynamicForm(
                    viewModel = formViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    onSubmitSuccess = { formData: Map<String, Any> ->
                        val petId = formData["petId"] as? String ?: ""
                        val examType = formData["examType"] as? String ?: ""
                        val examDate = formData["examDate"] as? String ?: ""
                        val results = formData["results"] as? String
                        val notes = formData["notes"] as? String

                        addExamViewModel.onEvent(
                            AddExamUiEvent.AddExam(
                                petId = petId,
                                examType = examType,
                                examDate = examDate,
                                results = results,
                                notes = notes
                            )
                        )
                    }
                )

                // Footer with loading/error
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.fromHex("#F44336"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.fromHex("#2196F3")
                        )
                    }
                }
            }
        }
    }

    // Handle success
    val uiState by addExamViewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
            addExamViewModel.onEvent(AddExamUiEvent.ClearState)
        }
    }
}