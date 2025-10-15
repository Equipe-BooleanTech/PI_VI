package edu.fatec.petwise.features.pets.presentation.view

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
import edu.fatec.petwise.features.pets.domain.models.*
import edu.fatec.petwise.features.pets.presentation.components.PetCard
import edu.fatec.petwise.features.pets.presentation.components.AddPetDialog
import edu.fatec.petwise.features.pets.presentation.components.EditPetDialog
import edu.fatec.petwise.features.pets.presentation.components.FilterBottomSheet
import edu.fatec.petwise.features.pets.presentation.components.PetErrorSnackbar
import edu.fatec.petwise.features.pets.presentation.forms.addPetFormConfiguration
import edu.fatec.petwise.features.pets.presentation.viewmodel.*
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsScreen() {
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val addPetViewModel = remember { PetDependencyContainer.provideAddPetViewModel() }
    val updatePetViewModel = remember { PetDependencyContainer.provideUpdatePetViewModel() }
    val theme = PetWiseTheme.Light
    val petsState by petsViewModel.uiState.collectAsState()
    val addPetState by addPetViewModel.uiState.collectAsState()
    val updatePetState by updatePetViewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedPetIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditPetDialog by remember { mutableStateOf(false) }
    var petToEdit by remember { mutableStateOf<Pet?>(null) }



    LaunchedEffect(addPetState.isSuccess) {
        if (addPetState.isSuccess) {
            petsViewModel.onEvent(PetsUiEvent.HideAddPetDialog)
            petsViewModel.onEvent(PetsUiEvent.LoadPets)
            addPetViewModel.onEvent(AddPetUiEvent.ClearState)
        }
    }

    LaunchedEffect(updatePetState.isSuccess) {
        if (updatePetState.isSuccess) {
            showEditPetDialog = false
            petToEdit = null
            petsViewModel.onEvent(PetsUiEvent.LoadPets)
            updatePetViewModel.onEvent(UpdatePetUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        PetsHeader(
            petCount = petsState.filteredPets.size,
            selectionMode = selectionMode,
            selectedCount = selectedPetIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { showFilterSheet = true },
            onAddPetClick = { petsViewModel.onEvent(PetsUiEvent.ShowAddPetDialog) },
            onSelectionModeToggle = { 
                selectionMode = !selectionMode
                if (!selectionMode) selectedPetIds = setOf()
            },
            onDeleteSelected = { 
                if (selectedPetIds.isNotEmpty()) {
                    showDeleteConfirmation = true
                }
            }
        )

        if (showSearchBar) {
            SearchBar(
                query = petsState.searchQuery,
                onQueryChange = { query ->
                    petsViewModel.onEvent(PetsUiEvent.SearchPets(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                petsState.isLoading -> {
                    LoadingContent()
                }
                petsState.filteredPets.isEmpty() -> {
                    EmptyContent(
                        onAddPetClick = { petsViewModel.onEvent(PetsUiEvent.ShowAddPetDialog) }
                    )
                }
                else -> {
                    PetsListContent(
                        pets = petsState.filteredPets,
                        selectionMode = selectionMode,
                        selectedPetIds = selectedPetIds,
                        onPetClick = { pet ->
                            if (selectionMode) {
                                selectedPetIds = if (selectedPetIds.contains(pet.id)) {
                                    selectedPetIds - pet.id
                                } else {
                                    selectedPetIds + pet.id
                                }
                            } else {
                                petsViewModel.onEvent(PetsUiEvent.SelectPet(pet))
                            }
                        },
                        onFavoriteClick = { petId ->
                            petsViewModel.onEvent(PetsUiEvent.ToggleFavorite(petId))
                        },
                        onEditClick = { pet ->
                            petToEdit = pet
                            showEditPetDialog = true
                        }
                    )
                }
            }


            petsState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    PetErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { petsViewModel.onEvent(PetsUiEvent.ClearError) },
                        actionLabel = "Tentar Novamente",
                        onAction = { petsViewModel.onEvent(PetsUiEvent.LoadPets) }
                    )
                }
            }
        }
    }

    if (petsState.showAddPetDialog) {
        AddPetDialog(
            addPetViewModel = addPetViewModel,
            isLoading = addPetState.isLoading,
            errorMessage = addPetState.errorMessage,
            onDismiss = {
                petsViewModel.onEvent(PetsUiEvent.HideAddPetDialog)
                addPetViewModel.onEvent(AddPetUiEvent.ClearState)
            },
            onSuccess = {
                // Refresh the pets list after successful add
                petsViewModel.onEvent(PetsUiEvent.LoadPets)
            }
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = petsState.filterOptions,
            onFilterApply = { filterOptions ->
                petsViewModel.onEvent(PetsUiEvent.FilterPets(filterOptions))
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            petCount = selectedPetIds.size,
            onConfirm = {
                selectedPetIds.forEach { petId ->
                    petsViewModel.onEvent(PetsUiEvent.DeletePet(petId))
                }
                selectedPetIds = setOf()
                selectionMode = false
                showDeleteConfirmation = false
                // Refresh the pets list after delete
                petsViewModel.onEvent(PetsUiEvent.LoadPets)
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    petToEdit?.let { pet ->
        if (showEditPetDialog) {
            EditPetDialog(
                pet = pet,
                updatePetViewModel = updatePetViewModel,
                isLoading = updatePetState.isLoading,
                errorMessage = updatePetState.errorMessage,
                onDismiss = {
                    showEditPetDialog = false
                    petToEdit = null
                    updatePetViewModel.onEvent(UpdatePetUiEvent.ClearState)
                },
                onSuccess = {
                    // Refresh the pets list after successful update
                    petsViewModel.onEvent(PetsUiEvent.LoadPets)
                }
            )
        }
    }
}

@Composable
private fun PetsHeader(
    petCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddPetClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    val theme = PetWiseTheme.Light

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode) Color.fromHex("#d32f2f") else Color.fromHex("#00b942")
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectionMode) "Selecionados" else "Meus Pets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount pet(s) selecionado(s)"
                        } else {
                            if (petCount > 0) "Cuidando de $petCount pets com carinho" else "Nenhum pet cadastrado"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
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
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onSelectionModeToggle) {
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
                                contentDescription = "Selecionar",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!selectionMode) {
                Button(
                    onClick = onAddPetClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.fromHex("#00b942")
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Adicionar Pet",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else if (selectedCount > 0) {
                Button(
                    onClick = onDeleteSelected,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.fromHex("#d32f2f")
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Excluir Selecionados ($selectedCount)",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Buscar por nome, raça ou tutor...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.fromHex(theme.palette.primary)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar",
                            tint = Color.fromHex(theme.palette.textSecondary)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.fromHex(theme.palette.primary),
                unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary).copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.fromHex("#00b942")
        )
    }
}

@Composable
private fun EmptyContent(
    onAddPetClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = "Nenhum pet",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum pet cadastrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione seu primeiro pet para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddPetClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#00b942")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Pet")
            }
        }
    }
}

@Composable
private fun PetsListContent(
    pets: List<Pet>,
    selectionMode: Boolean,
    selectedPetIds: Set<String>,
    onPetClick: (Pet) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onEditClick: (Pet) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pets, key = { it.id }) { pet ->
            PetCard(
                pet = pet,
                selectionMode = selectionMode,
                isSelected = selectedPetIds.contains(pet.id),
                onClick = onPetClick,
                onFavoriteClick = onFavoriteClick,
                onEditClick = onEditClick
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    petCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Aviso",
                tint = Color.fromHex("#d32f2f"),
                modifier = Modifier.size(48.dp)
            )
        },
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
            Text(
                text = "Tem certeza que deseja excluir ${if (petCount == 1) "este pet" else "estes $petCount pets"}? Esta ação não pode ser desfeita.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#d32f2f")
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Excluir", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.fromHex(theme.palette.textPrimary)
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}