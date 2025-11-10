package edu.fatec.petwise.features.vaccinations.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.presentation.forms.createEditVaccinationFormConfiguration
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.UpdateVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.UpdateVaccinationUiEvent
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVaccinationDialog(
    vaccination: Vaccination,
    updateVaccinationViewModel: UpdateVaccinationViewModel,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    canEditVaccinations: Boolean = true
) {
    val theme = PetWiseTheme.Light
    val formConfiguration = remember(vaccination) { 
        createEditVaccinationFormConfiguration(vaccination) 
    }
    
    val formViewModel = remember(vaccination) {
        DynamicFormViewModel(formConfiguration)
    }
    
    if (!canEditVaccinations) {
        onDismiss()
        return
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
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formConfiguration.title ?: "Editar Vacina",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.fromHex(theme.palette.textSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formConfiguration.description ?: "Atualize as informações da vacinação.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#FFEBEE")
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.fromHex("#C62828")
                            )
                        )
                    }
                }

                // Form
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    DynamicForm(
                        viewModel = formViewModel,
                        onSubmitSuccess = { formData ->
                            // Convert Map<String, Any> to Map<String, JsonPrimitive>
                            val jsonFormData = formData.mapValues { (_, value) ->
                                when (value) {
                                    is String -> JsonPrimitive(value)
                                    is Number -> JsonPrimitive(value)
                                    is Boolean -> JsonPrimitive(value)
                                    else -> JsonPrimitive(value.toString())
                                }
                            }
                            
                            updateVaccinationViewModel.onEvent(
                                UpdateVaccinationUiEvent.UpdateVaccination(
                                    vaccinationId = vaccination.id,
                                    formData = jsonFormData
                                )
                            )
                        },
                        onFieldChanged = { fieldId, oldValue, newValue ->
                            // Handle field changes if needed
                        }
                    )
                }

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.fromHex("#4CAF50")
                        )
                    }
                }
            }
        }
    }
}