package edu.fatec.petwise.features.medications.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.features.medications.domain.models.*
import edu.fatec.petwise.features.medications.presentation.components.MedicationCard
import edu.fatec.petwise.features.medications.presentation.components.AddMedicationDialog
import edu.fatec.petwise.features.medications.presentation.components.EditMedicationDialog
import edu.fatec.petwise.features.medications.presentation.components.DeleteMedicationConfirmationDialog
import edu.fatec.petwise.features.medications.presentation.components.MedicationErrorSnackbar
import edu.fatec.petwise.features.medications.presentation.viewmodel.*
import edu.fatec.petwise.features.medications.di.MedicationDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
    navigationKey: Any? = null
) {
    
    val medicationsViewModel = remember { MedicationDependencyContainer.provideMedicationsViewModel() }
    val addMedicationViewModel = remember { MedicationDependencyContainer.provideAddMedicationViewModel() }
    val updateMedicationViewModel = remember { MedicationDependencyContainer.provideUpdateMedicationViewModel() }
    val theme = PetWiseTheme.Light
    val medicationsState by medicationsViewModel.uiState.collectAsState()
    val addMedicationState by addMedicationViewModel.uiState.collectAsState()
    val updateMedicationState by updateMedicationViewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedMedicationIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditMedicationDialog by remember { mutableStateOf(false) }
    var medicationToEdit by remember { mutableStateOf<Medication?>(null) }
    var medicationToDelete by remember { mutableStateOf<Medication?>(null) }

    LaunchedEffect(navigationKey) {
        println("MedicationsScreen: Recarregando medicamentos - navigationKey: $navigationKey")
        medicationsViewModel.onEvent(MedicationsUiEvent.LoadMedications)
    }

    LaunchedEffect(addMedicationState.isSuccess) {
        if (addMedicationState.isSuccess) {
            medicationsViewModel.onEvent(MedicationsUiEvent.HideAddMedicationDialog)
            medicationsViewModel.onEvent(MedicationsUiEvent.LoadMedications)
            addMedicationViewModel.clearState()
        }
    }

    LaunchedEffect(updateMedicationState.isSuccess) {
        if (updateMedicationState.isSuccess) {
            showEditMedicationDialog = false
            medicationToEdit = null
            medicationsViewModel.onEvent(MedicationsUiEvent.LoadMedications)
            updateMedicationViewModel.clearState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        MedicationsHeader(
            medicationCount = medicationsState.filteredMedications.size,
            selectionMode = selectionMode,
            selectedCount = selectedMedicationIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { showFilterSheet = true },
            onAddMedicationClick = { medicationsViewModel.onEvent(MedicationsUiEvent.ShowAddMedicationDialog) },
            onSelectionModeToggle = { 
                selectionMode = !selectionMode
                if (!selectionMode) selectedMedicationIds = setOf()
            },
            onDeleteSelected = { 
                if (selectedMedicationIds.isNotEmpty()) {
                    showDeleteConfirmation = true
                }
            }
        )

        if (showSearchBar) {
            SearchBar(
                query = medicationsState.searchQuery,
                onQueryChange = { query ->
                    medicationsViewModel.onEvent(MedicationsUiEvent.SearchMedications(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                medicationsState.isLoading -> {
                    LoadingContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                medicationsState.medications.isEmpty() -> {
                    EmptyMedicationsContent(
                        onAddMedicationClick = { 
                            medicationsViewModel.onEvent(MedicationsUiEvent.ShowAddMedicationDialog) 
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                medicationsState.filteredMedications.isEmpty() -> {
                    NoResultsContent(
                        searchQuery = medicationsState.searchQuery,
                        onClearSearch = { 
                            medicationsViewModel.onEvent(MedicationsUiEvent.SearchMedications("")) 
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    MedicationsList(
                        medications = medicationsState.filteredMedications,
                        selectionMode = selectionMode,
                        selectedIds = selectedMedicationIds,
                        onMedicationClick = { medication ->
                            if (selectionMode) {
                                selectedMedicationIds = if (selectedMedicationIds.contains(medication.id)) {
                                    selectedMedicationIds - medication.id
                                } else {
                                    selectedMedicationIds + medication.id
                                }
                            } else {
                                // Handle medication details view if needed
                            }
                        },
                        onEditClick = { medication ->
                            medicationToEdit = medication
                            updateMedicationViewModel.loadMedication(medication.id)
                            showEditMedicationDialog = true
                        },
                        onDeleteClick = { medication ->
                            // Handle single medication delete
                            selectedMedicationIds = setOf(medication.id)
                            showDeleteConfirmation = true
                        },
                        onMarkAsCompletedClick = { medicationId ->
                            medicationsViewModel.onEvent(MedicationsUiEvent.MarkAsCompleted(medicationId))
                        },
                        onPauseResumeClick = { medicationId ->
                            // Toggle between pause and resume
                            medicationsViewModel.onEvent(MedicationsUiEvent.PauseMedication(medicationId))
                        }
                    )
                }
            }

            // Error message
            medicationsState.errorMessage?.let { message ->
                MedicationErrorSnackbar(
                    errorMessage = message,
                    onDismiss = { medicationsViewModel.onEvent(MedicationsUiEvent.ClearError) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Dialogs
    if (medicationsState.showAddMedicationDialog) {
        AddMedicationDialog(
            addMedicationViewModel = addMedicationViewModel,
            isLoading = addMedicationState.isLoading,
            errorMessage = addMedicationState.errorMessage,
            onDismiss = { 
                medicationsViewModel.onEvent(MedicationsUiEvent.HideAddMedicationDialog)
                addMedicationViewModel.clearState()
            },
            onSuccess = {
                medicationsViewModel.onEvent(MedicationsUiEvent.HideAddMedicationDialog)
                addMedicationViewModel.clearState()
            }
        )
    }

    if (showEditMedicationDialog && medicationToEdit != null) {
        EditMedicationDialog(
            medication = medicationToEdit!!,
            updateMedicationViewModel = updateMedicationViewModel,
            isLoading = updateMedicationState.isLoading,
            errorMessage = updateMedicationState.errorMessage,
            onDismiss = { 
                showEditMedicationDialog = false
                medicationToEdit = null
                updateMedicationViewModel.clearState()
            },
            onSuccess = {
                showEditMedicationDialog = false
                medicationToEdit = null
                updateMedicationViewModel.clearState()
            }
        )
    }

    if (showDeleteConfirmation) {
        val medicationNames = selectedMedicationIds.mapNotNull { id ->
            medicationsState.filteredMedications.find { it.id == id }?.medicationName
        }.joinToString(", ")
        
        DeleteMedicationConfirmationDialog(
            medicationId = selectedMedicationIds.first(), // For bulk delete, use first ID (though this should be updated for proper bulk handling)
            medicationName = if (selectedMedicationIds.size == 1) medicationNames else "${selectedMedicationIds.size} medicamentos",
            onSuccess = {
                selectedMedicationIds.forEach { medicationId ->
                    medicationsViewModel.onEvent(MedicationsUiEvent.DeleteMedication(medicationId))
                }
                selectedMedicationIds = setOf()
                selectionMode = false
                showDeleteConfirmation = false
            },
            onCancel = {
                showDeleteConfirmation = false
            }
        )
    }
}

@Composable
private fun MedicationsHeader(
    medicationCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddMedicationClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    Surface(
        color = Color.fromHex(theme.palette.primary),
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectionMode) "Selecionados: $selectedCount" else "Medicamentos",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (!selectionMode) {
                        Text(
                            text = "$medicationCount medicamentos",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                Row {
                    if (selectionMode) {
                        IconButton(
                            onClick = onDeleteSelected,
                            enabled = selectedCount > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir selecionados",
                                tint = if (selectedCount > 0) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(
                            onClick = onSelectionModeToggle
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar seleção",
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onFilterClick) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtrar",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onSelectionModeToggle) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Modo seleção",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onAddMedicationClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar medicamento",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar medicamentos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpar"
                    )
                }
            }
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            color = Color.fromHex(theme.palette.primary)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Carregando medicamentos...",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.fromHex(theme.palette.textSecondary)
            )
        )
    }
}

@Composable
private fun EmptyMedicationsContent(
    onAddMedicationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = null,
            tint = Color.fromHex(theme.palette.textSecondary),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum medicamento cadastrado",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.fromHex(theme.palette.textPrimary)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Adicione o primeiro medicamento para começar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.fromHex(theme.palette.textSecondary)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
                onClick = onAddMedicationClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex(theme.palette.primary)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Medicamento")
            }
        }
    }


@Composable
private fun NoResultsContent(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = Color.fromHex(theme.palette.textSecondary),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum resultado encontrado",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.fromHex(theme.palette.textPrimary)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tente usar outros termos de busca",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.fromHex(theme.palette.textSecondary)
            )
        )
        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = onClearSearch) {
                Text(
                    "Limpar busca",
                    color = Color.fromHex(theme.palette.primary)
                )
            }
        }
    }
}

@Composable
private fun MedicationsList(
    medications: List<Medication>,
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onMedicationClick: (Medication) -> Unit,
    onEditClick: (Medication) -> Unit,
    onDeleteClick: (Medication) -> Unit,
    onMarkAsCompletedClick: (String) -> Unit,
    onPauseResumeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(
            items = medications,
            key = { it.id }
        ) { medication ->
            MedicationCard(
                medication = medication,
                onClick = onMedicationClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onMarkAsCompletedClick = onMarkAsCompletedClick,
                onPauseResumeClick = onPauseResumeClick,
                selectionMode = selectionMode,
                isSelected = selectedIds.contains(medication.id)
            )
        }
    }
}