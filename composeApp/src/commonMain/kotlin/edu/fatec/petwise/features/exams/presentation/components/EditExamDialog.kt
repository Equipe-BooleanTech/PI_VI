package edu.fatec.petwise.features.exams.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.presentation.forms.createEditExamFormConfiguration
import edu.fatec.petwise.features.exams.presentation.viewmodel.UpdateExamViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.UpdateExamUiEvent
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExamDialog(
    updateExamViewModel: UpdateExamViewModel,
    exam: Exam,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val formConfiguration = remember(exam) { createEditExamFormConfiguration(exam) }
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Exame",
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

                
                DynamicForm(
                    viewModel = formViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    onSubmitSuccess = { formData: Map<String, Any> ->
                        val jsonFormData = formData.mapValues { (_, value) ->
                            when (value) {
                                is String -> JsonPrimitive(value)
                                is Number -> JsonPrimitive(value.toString())
                                is Boolean -> JsonPrimitive(value)
                                else -> JsonPrimitive(value.toString())
                            }
                        }
                        updateExamViewModel.onEvent(
                            UpdateExamUiEvent.UpdateExam(exam.id, jsonFormData)
                        )
                    }
                )

                
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

    
    val uiState by updateExamViewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
            updateExamViewModel.onEvent(UpdateExamUiEvent.ClearState)
        }
    }
}