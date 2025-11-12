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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToysScreen() {
    var toys by remember { mutableStateOf<List<Toy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedToyIds by remember { mutableStateOf(setOf<String>()) }

    val theme = PetWiseTheme.Light

    val filteredToys = remember(toys, searchQuery) {
        if (searchQuery.isEmpty()) {
            toys
        } else {
            toys.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        ToysHeader(
            toyCount = filteredToys.size,
            selectionMode = selectionMode,
            selectedCount = selectedToyIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddToyClick = { /* TODO: Implement add */ },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedToyIds = setOf()
            },
            onDeleteSelected = { /* TODO: Implement delete */ }
        )

        if (showSearchBar) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    LoadingContent()
                }
                filteredToys.isEmpty() && searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddToyClick = { /* TODO: Implement add */ }
                    )
                }
                filteredToys.isEmpty() && searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { searchQuery = "" })
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
                        onEditClick = { /* TODO: Implement edit */ }
                    )
                }
            }
        }
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
            containerColor = if (selectionMode) Color.fromHex("#d32f2f") else Color.fromHex("#E91E63")
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
                        contentColor = Color.fromHex("#E91E63")
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
                    "Buscar por nome ou marca...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.fromHex("#E91E63")
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
                focusedBorderColor = Color.fromHex("#E91E63"),
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
            color = Color.fromHex("#E91E63")
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
                    containerColor = Color.fromHex("#E91E63")
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
                Text("Limpar busca", color = Color.fromHex("#E91E63"))
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
    onEditClick: (Toy) -> Unit
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
                onEditClick = onEditClick
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
    onEditClick: (Toy) -> Unit = {}
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
                isSelected -> Color.fromHex("#E91E63").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color.fromHex("#E91E63"))
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
                            checkedColor = Color.fromHex("#E91E63")
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onEditClick(toy) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.fromHex("#E91E63"),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = toy.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = toy.brand,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "R$ ${String.format("%.2f", toy.price)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex("#E91E63"),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Estoque: ${toy.stock} ${toy.unit}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
        }
    }
}
