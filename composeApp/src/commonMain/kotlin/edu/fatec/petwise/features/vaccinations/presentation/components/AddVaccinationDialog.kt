package edu.fatec.petwise.features.vaccinations.presentation.components

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
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import edu.fatec.petwise.features.vaccinations.presentation.forms.addVaccinationFormConfiguration
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationUiEvent
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.shared.form.SelectOption
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccinationDialog(
    addVaccinationViewModel: AddVaccinationViewModel,
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
        addVaccinationFormConfiguration.copy(
            fields = addVaccinationFormConfiguration.fields.map { field ->
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
                        text = "Adicionar Vacina",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.fromHex(theme.palette.textSecondary)
                        )
                    }
                }

                // Form Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DynamicForm(
                        viewModel = formViewModel,
                        onSubmitSuccess = { formData ->
                            val vaccineType = when (formData["vaccineType"]) {
                                "Vacina V8" -> VaccineType.V8
                                "Vacina V10" -> VaccineType.V10
                                "Antirrábica" -> VaccineType.ANTIRABICA
                                "Gripe Canina" -> VaccineType.GRIPE_CANINA
                                "Giárdia" -> VaccineType.GIARDIA
                                "Leptospirose" -> VaccineType.LEPTOSPIROSE
                                "Tríplice Felina" -> VaccineType.TRIPLE_FELINA
                                "Quádrupla Felina" -> VaccineType.QUADRUPLA_FELINA
                                "Leucemia Felina" -> VaccineType.LEUCEMIA_FELINA
                                "Raiva Felina" -> VaccineType.RAIVA_FELINA
                                else -> VaccineType.OUTRAS
                            }

                            addVaccinationViewModel.onEvent(
                                AddVaccinationUiEvent.AddVaccination(
                                    petId = formData["petId"]?.toString() ?: "",
                                    vaccineType = vaccineType,
                                    vaccinationDate = formData["vaccinationDate"]?.toString() ?: "",
                                    nextDoseDate = formData["nextDoseDate"]?.toString(),
                                    totalDoses = formData["totalDoses"]?.toString() ?: "",
                                    manufacturer = formData["manufacturer"]?.toString() ?: "",
                                    observations = formData["observations"]?.toString() ?: ""
                                )
                            )
                        }
                    )
                }

                // Error Message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFFC62828),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Loading Indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.fromHex("#4CAF50")
                        )
                    }
                }

                // Buttons
                if (!isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.fromHex(theme.palette.textPrimary)
                            )
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                formViewModel.submitForm()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.fromHex("#4CAF50")
                            )
                        ) {
                            Text("Adicionar")
                        }
                    }
                }
            }
        }
    }
}