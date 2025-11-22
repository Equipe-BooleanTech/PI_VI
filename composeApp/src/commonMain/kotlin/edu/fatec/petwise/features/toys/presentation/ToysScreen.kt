package edu.fatec.petwise.features.toys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import edu.fatec.petwise.features.toys.domain.models.Toy
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.features.toys.di.ToyDependencyContainer
import edu.fatec.petwise.features.toys.presentation.ToysUiEvent
import edu.fatec.petwise.presentation.shared.NumberFormatter
import edu.fatec.petwise.features.toys.presentation.components.AddToyDialog
import edu.fatec.petwise.features.toys.presentation.components.EditToyDialog
import edu.fatec.petwise.features.toys.presentation.components.DeleteToyConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToysScreen() {
    val viewModel: ToysViewModel = remember { ToyDependencyContainer.toysViewModel }
    val uiState: ToysUiState by viewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedToyIds by remember { mutableStateOf(setOf<String>()) }

    // Dialog states
    var showAddToyDialog by remember { mutableStateOf(false) }
    var showEditToyDialog by remember { mutableStateOf(false) }
    var toyToEdit by remember { mutableStateOf<edu.fatec.petwise.features.toys.domain.models.Toy?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var toyToDelete by remember { mutableStateOf<edu.fatec.petwise.features.toys.domain.models.Toy?>(null) }

    val theme = PetWiseTheme.Light

    val filteredToys = remember(uiState.toys, uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty()) {
            uiState.toys
        } else {
            uiState.toys.filter {
                it.name.contains(uiState.searchQuery, ignoreCase = true) ||
                it.brand.contains(uiState.searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fromHex("#F7F7F7"))
    ) {
        ToysHeader(
            toyCount = filteredToys.size,
            selectionMode = selectionMode,
            selectedCount = selectedToyIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddToyClick = { showAddToyDialog = true },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedToyIds = setOf()
            },
            onDeleteSelected = { /* TODO: Implement delete */ }
        )

        if (showSearchBar) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onEvent(ToysUiEvent.SearchToys(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                filteredToys.isEmpty() && uiState.searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddToyClick = { showAddToyDialog = true }
                    )
                }
                filteredToys.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { viewModel.onEvent(ToysUiEvent.SearchToys("")) })
                }
                else -> {
                    ToysGrid(
                        toys = filteredToys,
                        selectionMode = selectionMode,
                        selectedIds = selectedToyIds,
                        onToyClick = { toy ->
                            if (selectionMode) {
                                selectedToyIds = if (selectedToyIds.contains(toy.id)) {
                                    selectedToyIds - toy.id
                                } else {
                                    selectedToyIds + toy.id
                                }
                            }
                        },
                        onEditClick = { toy ->
                            toyToEdit = toy
                            showEditToyDialog = true
                        },
                        onDeleteClick = { toy ->
                            toyToDelete = toy
                            showDeleteConfirmation = true
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddToyDialog) {
        AddToyDialog(
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                showAddToyDialog = false
            },
            onSuccess = { formData: Map<String, Any> ->
                // Convert form data to Toy model
                val toy = edu.fatec.petwise.features.toys.domain.models.Toy(
                    id = "",
                    name = formData["name"] as String,
                    brand = formData["brand"] as String,
                    category = formData["category"] as String,
                    description = formData["description"] as? String,
                    price = (formData["price"] as? Double) ?: 0.0,
                    stock = (formData["stock"] as? Int) ?: 0,
                    unit = formData["unit"] as String,
                    material = formData["material"] as? String,
                    ageRecommendation = formData["ageRecommendation"] as? String,
                    imageUrl = formData["imageUrl"] as? String,
                    active = true,
                    createdAt = "",
                    updatedAt = ""
                )
                viewModel.onEvent(ToysUiEvent.AddToy(toy))
                showAddToyDialog = false
            }
        )
    }

    if (showEditToyDialog && toyToEdit != null) {
        EditToyDialog(
            toy = toyToEdit!!,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                showEditToyDialog = false
                toyToEdit = null
            },
            onSuccess = { formData: Map<String, Any> ->
                // Convert form data to updated Toy model
                val updatedToy = toyToEdit!!.copy(
                    name = formData["name"] as String,
                    brand = formData["brand"] as String,
                    category = formData["category"] as String,
                    description = formData["description"] as? String,
                    price = (formData["price"] as? Double) ?: 0.0,
                    stock = (formData["stock"] as? Int) ?: 0,
                    unit = formData["unit"] as String,
                    material = formData["material"] as? String,
                    ageRecommendation = formData["ageRecommendation"] as? String,
                    imageUrl = formData["imageUrl"] as? String
                )
                viewModel.onEvent(ToysUiEvent.UpdateToy(updatedToy))
                showEditToyDialog = false
                toyToEdit = null
            }
        )
    }

    if (showDeleteConfirmation && toyToDelete != null) {
        DeleteToyConfirmationDialog(
            toyId = toyToDelete!!.id,
            toyName = toyToDelete!!.name,
            onSuccess = {
                viewModel.onEvent(ToysUiEvent.DeleteToy(toyToDelete!!.id))
                showDeleteConfirmation = false
                toyToDelete = null
            },
            onCancel = {
                showDeleteConfirmation = false
                toyToDelete = null
            }
        )
    }
}

@Composable
private fun ToysHeader(
    toyCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddToyClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode) fromHex("#d32f2f") else fromHex("#E91E63")
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
                        text = if (selectionMode) "Selecionados" else "Brinquedos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount brinquedo(s) selecionado(s)"
                        } else {
                            if (toyCount > 0) "$toyCount brinquedos em estoque" else "Nenhum brinquedo cadastrado"
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
                    onClick = onAddToyClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = fromHex("#E91E63")
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
                        text = "Adicionar Brinquedo",
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
                        contentColor = fromHex("#d32f2f")
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
                    "Buscar por nome ou marca...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = fromHex(theme.palette.textSecondary)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = fromHex("#E91E63")
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar",
                            tint = fromHex(theme.palette.textSecondary)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = fromHex("#E91E63"),
                unfocusedBorderColor = fromHex(theme.palette.textSecondary).copy(alpha = 0.3f),
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
            color = fromHex("#E91E63")
        )
    }
}

@Composable
private fun EmptyContent(
    onAddToyClick: () -> Unit
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
                imageVector = Icons.Default.Toys,
                contentDescription = "Nenhum brinquedo",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum brinquedo cadastrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione seu primeiro brinquedo para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddToyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = fromHex("#E91E63")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Brinquedo")
            }
        }
    }
}

@Composable
private fun NoResultsContent(
    onClearSearch: () -> Unit
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
                imageVector = Icons.Default.SearchOff,
                contentDescription = "Sem resultados",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum resultado encontrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tente usar outros termos de busca",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onClearSearch) {
                Text("Limpar busca", color = fromHex("#E91E63"))
            }
        }
    }
}

@Composable
private fun ToysGrid(
    toys: List<Toy>,
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onToyClick: (Toy) -> Unit,
    onEditClick: (Toy) -> Unit,
    onDeleteClick: (Toy) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(toys, key = { it.id }) { toy ->
            ToyCard(
                toy = toy,
                selectionMode = selectionMode,
                isSelected = selectedIds.contains(toy.id),
                onClick = onToyClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
fun ToyCard(
    toy: Toy,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: (Toy) -> Unit = {},
    onEditClick: (Toy) -> Unit = {},
    onDeleteClick: (Toy) -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(toy) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> fromHex("#E91E63").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, fromHex("#E91E63"))
        } else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (selectionMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick(toy) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = fromHex("#E91E63")
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDeleteClick(toy) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { onEditClick(toy) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = fromHex("#E91E63"),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = toy.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = fromHex(theme.palette.textPrimary)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = toy.brand,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = fromHex(theme.palette.textSecondary)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "R$ ${NumberFormatter.formatCurrency(toy.price)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = fromHex("#E91E63"),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Estoque: ${toy.stock} ${toy.unit}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = fromHex(theme.palette.textSecondary)
                )
            )
        }
    }
}
