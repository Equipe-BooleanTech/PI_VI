package edu.fatec.petwise.features.pets.presentation.components

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.pets.domain.models.*
import edu.fatec.petwise.features.pets.presentation.forms.addPetFormSchema
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetUiEvent
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun AddPetDialog(
    addPetViewModel: AddPetViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val scrollState = rememberScrollState()
    
    val formConfiguration = remember {
        FormConfiguration(
            id = addPetFormSchema.id,
            title = addPetFormSchema.title,
            description = addPetFormSchema.description,
            fields = addPetFormSchema.fields.map { field ->
                FormFieldDefinition(
                    id = field.id,
                    label = field.label,
                    type = when (field.type) {
                        "text" -> FormFieldType.TEXT
                        "select" -> FormFieldType.SELECT
                        "segmented" -> FormFieldType.SEGMENTED_CONTROL
                        "submit" -> FormFieldType.SUBMIT
                        else -> FormFieldType.TEXT
                    },
                    placeholder = field.placeholder,
                    options = field.options,
                    default = field.default,
                    validators = field.validators?.map { validator ->
                        ValidationRule(
                            type = when (validator.type) {
                                "required" -> ValidationType.REQUIRED
                                "minLength" -> ValidationType.MIN_LENGTH
                                "pattern" -> ValidationType.PATTERN
                                "phone" -> ValidationType.PHONE
                                else -> ValidationType.CUSTOM
                            },
                            message = validator.message,
                            value = validator.value
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#00b942")
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Adicionar Novo Pet",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Preencha as informações do pet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                        
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color.White
                            )
                        }
                    }
                }

                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#FFEBEE")
                        )
                    ) {
                        Text(
                            text = message,
                            color = Color.fromHex("#C62828"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#F8F9FA")
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        DynamicForm(
                            viewModel = formViewModel,
                            colorScheme = MaterialTheme.colorScheme.copy(
                                primary = Color.fromHex(theme.palette.primary),
                                error = Color.fromHex("#d32f2f")
                            ),
                            onSubmitSuccess = { values ->
                                val species = when (values["species"]) {
                                    "Cão" -> PetSpecies.DOG
                                    "Gato" -> PetSpecies.CAT
                                    "Ave" -> PetSpecies.BIRD
                                    "Coelho" -> PetSpecies.RABBIT
                                    else -> PetSpecies.OTHER
                                }
                                
                                val gender = when (values["gender"]) {
                                    "Macho" -> PetGender.MALE
                                    "Fêmea" -> PetGender.FEMALE
                                    else -> PetGender.MALE
                                }
                                
                                val healthStatus = when (values["healthStatus"]) {
                                    "Excelente" -> HealthStatus.EXCELLENT
                                    "Bom" -> HealthStatus.GOOD
                                    "Regular" -> HealthStatus.REGULAR
                                    "Atenção" -> HealthStatus.ATTENTION
                                    "Crítico" -> HealthStatus.CRITICAL
                                    else -> HealthStatus.GOOD
                                }

                                addPetViewModel.onEvent(
                                    AddPetUiEvent.AddPet(
                                        name = values["name"]?.toString() ?: "",
                                        breed = values["breed"]?.toString() ?: "",
                                        species = species,
                                        gender = gender,
                                        age = values["age"]?.toString() ?: "",
                                        weight = values["weight"]?.toString() ?: "",
                                        healthStatus = healthStatus,
                                        ownerName = values["ownerName"]?.toString() ?: "",
                                        ownerPhone = values["ownerPhone"]?.toString() ?: "",
                                        healthHistory = values["healthHistory"]?.toString() ?: ""
                                    )
                                )
                            }
                        )
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.fromHex(theme.palette.primary)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: PetFilterOptions,
    onFilterApply: (PetFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    var selectedSpecies by remember { mutableStateOf(currentFilter.species) }
    var selectedHealthStatus by remember { mutableStateOf(currentFilter.healthStatus) }
    var favoritesOnly by remember { mutableStateOf(currentFilter.favoritesOnly) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.fromHex("#F8F9FA"),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 40.dp, height = 4.dp),
                color = Color.fromHex(theme.palette.textSecondary).copy(alpha = 0.3f),
                shape = RoundedCornerShape(2.dp)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex("#00b942")
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Filtrar Pets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Espécie",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PetSpecies.values().forEach { species ->
                            FilterChip(
                                onClick = { 
                                    selectedSpecies = if (selectedSpecies == species) null else species
                                },
                                label = { Text(species.displayName) },
                                selected = selectedSpecies == species,
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.fromHex(theme.palette.primary),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Status de Saúde",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    HealthStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedHealthStatus == status,
                                onClick = { 
                                    selectedHealthStatus = if (selectedHealthStatus == status) null else status
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.fromHex(theme.palette.primary)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.fromHex(status.color).copy(alpha = 0.2f),
                                    modifier = Modifier.size(12.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = status.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.fromHex(theme.palette.textPrimary)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = favoritesOnly,
                        onCheckedChange = { favoritesOnly = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.fromHex(theme.palette.primary)
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Apenas favoritos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex(theme.palette.textPrimary),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedSpecies = null
                        selectedHealthStatus = null
                        favoritesOnly = false
                        onFilterApply(PetFilterOptions())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar")
                }
                
                Button(
                    onClick = {
                        onFilterApply(
                            PetFilterOptions(
                                species = selectedSpecies,
                                healthStatus = selectedHealthStatus,
                                favoritesOnly = favoritesOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex(theme.palette.primary)
                    )
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}