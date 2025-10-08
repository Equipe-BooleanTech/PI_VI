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
import edu.fatec.petwise.features.pets.presentation.components.FilterBottomSheet
import edu.fatec.petwise.features.pets.presentation.components.PetErrorSnackbar
import edu.fatec.petwise.features.pets.presentation.forms.addPetFormSchema
import edu.fatec.petwise.features.pets.presentation.viewmodel.*
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsScreen() {
    val petsViewModel = remember { PetDependencyContainer.providePetsViewModel() }
    val addPetViewModel = remember { PetDependencyContainer.provideAddPetViewModel() }
    val theme = PetWiseTheme.Light
    val petsState by petsViewModel.uiState.collectAsState()
    val addPetState by addPetViewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }



    LaunchedEffect(addPetState.isSuccess) {
        if (addPetState.isSuccess) {
            petsViewModel.onEvent(PetsUiEvent.HideAddPetDialog)
            petsViewModel.onEvent(PetsUiEvent.LoadPets)
            addPetViewModel.onEvent(AddPetUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        PetsHeader(
            petCount = petsState.filteredPets.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { showFilterSheet = true },
            onAddPetClick = { petsViewModel.onEvent(PetsUiEvent.ShowAddPetDialog) }
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
                        onPetClick = { pet ->
                            petsViewModel.onEvent(PetsUiEvent.SelectPet(pet))
                        },
                        onFavoriteClick = { petId ->
                            petsViewModel.onEvent(PetsUiEvent.ToggleFavorite(petId))
                        },
                        onEditClick = { pet ->
                            // TODO: Navegar à tela de edição
                        }
                    )
                }
            }

            // Enhanced error handling
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
}

@Composable
private fun PetsHeader(
    petCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddPetClick: () -> Unit
) {
    val theme = PetWiseTheme.Light

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.fromHex("#00b942")
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
                        text = "Meus Pets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (petCount > 0) "Cuidando de $petCount pets com carinho" else "Nenhum pet cadastrado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }

                Row {
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
    onPetClick: (Pet) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onEditClick: (Pet) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pets) { pet ->
            PetCard(
                pet = pet,
                onClick = onPetClick,
                onFavoriteClick = onFavoriteClick,
                onEditClick = onEditClick
            )
        }
    }
}