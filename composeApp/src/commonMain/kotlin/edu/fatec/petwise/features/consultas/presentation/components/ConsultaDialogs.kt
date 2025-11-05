package edu.fatec.petwise.features.consultas.presentation.components

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
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.consultas.domain.models.*
import edu.fatec.petwise.features.consultas.presentation.forms.createAddConsultaFormConfiguration
import edu.fatec.petwise.features.consultas.presentation.forms.createEditConsultaFormConfiguration
import edu.fatec.petwise.features.consultas.presentation.viewmodel.AddConsultaViewModel
import edu.fatec.petwise.features.consultas.presentation.viewmodel.AddConsultaUiEvent
import edu.fatec.petwise.features.consultas.presentation.viewmodel.UpdateConsultaViewModel
import edu.fatec.petwise.features.consultas.presentation.viewmodel.UpdateConsultaUiEvent
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import kotlinx.coroutines.flow.firstOrNull
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField

@Composable
fun AddConsultaDialog(
    addConsultaViewModel: AddConsultaViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val addConsultaState by addConsultaViewModel.uiState.collectAsState()
    
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()
    
    val petOptions = remember(petsState.pets) {
        petsState.pets.map { pet ->
            SelectOption(
                key = pet.id,
                value = pet.name
            )
        }
    }

    val formConfiguration = remember(petOptions) {
        createAddConsultaFormConfiguration(petOptions)
    }

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_consulta_form") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(petOptions) {
        formViewModel.updateConfiguration(createAddConsultaFormConfiguration(petOptions))
    }
    
    LaunchedEffect(addConsultaState.isSuccess) {
        if (addConsultaState.isSuccess) {
            formViewModel.resetForm()
            onSuccess()
        }
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
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#2196F3")
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
                                text = "Agendar Nova Consulta",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Preencha as informações da consulta",
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
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#F8F9FA")
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        DynamicForm(
                            viewModel = formViewModel,
                            modifier = Modifier.fillMaxSize(),
                            colorScheme = MaterialTheme.colorScheme.copy(
                                primary = Color.fromHex("#2196F3"),
                                error = Color.fromHex("#d32f2f")
                            ),
                            onSubmitSuccess = { values ->
                                val petId = values["petId"]?.toString() ?: ""
                                val selectedPet = petsState.pets.find { it.id == petId }
                                val petName = selectedPet?.name ?: ""
                                
                                val consultaType = when (values["consultaType"]) {
                                    "Consulta de Rotina" -> ConsultaType.ROUTINE
                                    "Emergência" -> ConsultaType.EMERGENCY
                                    "Retorno" -> ConsultaType.FOLLOW_UP
                                    "Vacinação" -> ConsultaType.VACCINATION
                                    "Cirurgia" -> ConsultaType.SURGERY
                                    "Exame" -> ConsultaType.EXAM
                                    "Odontologia" -> ConsultaType.DENTAL
                                    "Estética" -> ConsultaType.GROOMING
                                    else -> ConsultaType.OTHER
                                }

                                addConsultaViewModel.onEvent(
                                    AddConsultaUiEvent.AddConsulta(
                                        petId = petId,
                                        petName = petName,
                                        veterinarianName = values["veterinarianName"]?.toString() ?: "",
                                        consultaType = consultaType,
                                        consultaDate = values["consultaDate"]?.toString() ?: "",
                                        consultaTime = values["consultaTime"]?.toString() ?: "",
                                        symptoms = values["symptoms"]?.toString() ?: "",
                                        notes = values["notes"]?.toString() ?: "",
                                        price = values["price"]?.toString() ?: "0",
                                        ownerName = values["ownerName"]?.toString() ?: "",
                                        ownerPhone = values["ownerPhone"]?.toString() ?: ""
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
                            color = Color.fromHex("#2196F3")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditConsultaDialog(
    consulta: Consulta,
    updateConsultaViewModel: UpdateConsultaViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val updateConsultaState by updateConsultaViewModel.uiState.collectAsState()
    
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()
    
    val petOptions = remember(petsState.pets) {
        petsState.pets.map { pet ->
            SelectOption(
                key = pet.id,
                value = pet.name
            )
        }
    }

    val formConfiguration = remember(consulta, petOptions) {
        createEditConsultaFormConfiguration(consulta, petOptions)
    }

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_consulta_form_${consulta.id}") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }
    
    LaunchedEffect(consulta, petOptions) {
        formViewModel.resetForm()
        formViewModel.updateConfiguration(createEditConsultaFormConfiguration(consulta, petOptions))
    }
    
    LaunchedEffect(updateConsultaState.isSuccess) {
        if (updateConsultaState.isSuccess) {
            formViewModel.resetForm()
            onSuccess()
        }
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
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#2196F3")
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
                                text = "Editar Consulta",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Atualize as informações da consulta",
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
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#F8F9FA")
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        DynamicForm(
                            viewModel = formViewModel,
                            modifier = Modifier.fillMaxSize(),
                            colorScheme = MaterialTheme.colorScheme.copy(
                                primary = Color.fromHex("#2196F3"),
                                error = Color.fromHex("#d32f2f")
                            ),
                            onSubmitSuccess = { values ->
                                val petId = values["petId"]?.toString() ?: consulta.petId
                                val selectedPet = petsState.pets.find { it.id == petId }
                                val petName = selectedPet?.name ?: consulta.petName
                                
                                val consultaType = when (values["consultaType"]) {
                                    "Consulta de Rotina" -> ConsultaType.ROUTINE
                                    "Emergência" -> ConsultaType.EMERGENCY
                                    "Retorno" -> ConsultaType.FOLLOW_UP
                                    "Vacinação" -> ConsultaType.VACCINATION
                                    "Cirurgia" -> ConsultaType.SURGERY
                                    "Exame" -> ConsultaType.EXAM
                                    "Odontologia" -> ConsultaType.DENTAL
                                    "Estética" -> ConsultaType.GROOMING
                                    else -> ConsultaType.OTHER
                                }

                                updateConsultaViewModel.onEvent(
                                    UpdateConsultaUiEvent.UpdateConsulta(
                                        id = consulta.id,
                                        petId = petId,
                                        petName = petName,
                                        veterinarianName = values["veterinarianName"]?.toString() ?: consulta.veterinarianName,
                                        consultaType = consultaType,
                                        consultaDate = values["consultaDate"]?.toString() ?: consulta.consultaDate,
                                        consultaTime = values["consultaTime"]?.toString() ?: consulta.consultaTime,
                                        symptoms = values["symptoms"]?.toString() ?: consulta.symptoms,
                                        diagnosis = values["diagnosis"]?.toString() ?: consulta.diagnosis,
                                        treatment = values["treatment"]?.toString() ?: consulta.treatment,
                                        prescriptions = values["prescriptions"]?.toString() ?: consulta.prescriptions,
                                        notes = values["notes"]?.toString() ?: consulta.notes,
                                        nextAppointment = values["nextAppointment"]?.toString(),
                                        price = values["price"]?.toString() ?: consulta.price.toString(),
                                        ownerName = values["ownerName"]?.toString() ?: consulta.ownerName,
                                        ownerPhone = values["ownerPhone"]?.toString() ?: consulta.ownerPhone
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
                            color = Color.fromHex("#2196F3")
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterConsultasBottomSheet(
    currentFilter: ConsultaFilterOptions,
    onFilterApply: (ConsultaFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    var selectedType by remember { mutableStateOf(currentFilter.consultaType) }
    var selectedStatus by remember { mutableStateOf(currentFilter.status) }
    // Pet selector + date range (follow pets filter pattern)
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val petsState by petsViewModel.uiState.collectAsState()
    var selectedPetId by remember { mutableStateOf(currentFilter.petId) }
    var startDate by remember { mutableStateOf(currentFilter.dateRange?.startDate ?: "") }
    var endDate by remember { mutableStateOf(currentFilter.dateRange?.endDate ?: "") }
    var expandedPetDropdown by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex("#2196F3")
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Filtrar Consultas",
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
                        text = "Tipo de Consulta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ConsultaType.values().forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = {
                                    selectedType = if (selectedType == type) null else type
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.fromHex("#2196F3")
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.fromHex(theme.palette.textPrimary)
                                )
                            )
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
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ConsultaStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = {
                                    selectedStatus = if (selectedStatus == status) null else status
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.fromHex("#2196F3")
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

            // Pet selector (reuses available pets list)
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
                        text = "Pet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedPetDropdown,
                        onExpandedChange = { expandedPetDropdown = !expandedPetDropdown }
                    ) {
                        OutlinedTextField(
                            value = petsState.pets.find { it.id == selectedPetId }?.name ?: "Todos",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPetDropdown) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPetDropdown,
                            onDismissRequest = { expandedPetDropdown = false }
                        ) {
                            DropdownMenuItem(text = { Text("Todos") }, onClick = {
                                selectedPetId = null
                                expandedPetDropdown = false
                            })

                            petsState.pets.forEach { pet ->
                                DropdownMenuItem(text = { Text(pet.name) }, onClick = {
                                    selectedPetId = pet.id
                                    expandedPetDropdown = false
                                })
                            }
                        }
                    }
                }
            }

            // Date range card (simple text inputs)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Intervalo de Data",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Data início (yyyy-MM-dd)") },
                        placeholder = { Text("2025-01-01") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Data fim (yyyy-MM-dd)") },
                        placeholder = { Text("2025-12-31") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedType = null
                        selectedStatus = null
                        selectedPetId = null
                        startDate = ""
                        endDate = ""
                        onFilterApply(ConsultaFilterOptions())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar")
                }

                Button(
                    onClick = {
                        val dateRange = if (startDate.isNotBlank() && endDate.isNotBlank()) {
                            DateRange(startDate, endDate)
                        } else null

                        onFilterApply(
                            ConsultaFilterOptions(
                                consultaType = selectedType,
                                status = selectedStatus,
                                petId = selectedPetId,
                                dateRange = dateRange
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex("#2196F3")
                    )
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}
