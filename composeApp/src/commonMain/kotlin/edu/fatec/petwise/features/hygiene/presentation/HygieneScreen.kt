package edu.fatec.petwise.features.hygiene.presentation

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
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HygieneScreen() {
    var hygieneProducts by remember { mutableStateOf<List<HygieneProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedProductIds by remember { mutableStateOf(setOf<String>()) }

    val theme = PetWiseTheme.Light

    val filteredProducts = remember(hygieneProducts, searchQuery) {
        if (searchQuery.isEmpty()) {
            hygieneProducts
        } else {
            hygieneProducts.filter {
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
        HygieneHeader(
            productCount = filteredProducts.size,
            selectionMode = selectionMode,
            selectedCount = selectedProductIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddProductClick = { /* TODO: Implement add */ },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedProductIds = setOf()
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
                filteredProducts.isEmpty() && searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddProductClick = { /* TODO: Implement add */ }
                    )
                }
                filteredProducts.isEmpty() && searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { searchQuery = "" })
                }
                else -> {
                    ProductsGrid(
                        products = filteredProducts,
                        selectionMode = selectionMode,
                        selectedIds = selectedProductIds,
                        onProductClick = { product ->
                            if (selectionMode) {
                                selectedProductIds = if (selectedProductIds.contains(product.id)) {
                                    selectedProductIds - product.id
                                } else {
                                    selectedProductIds + product.id
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
private fun HygieneHeader(
    productCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode) Color.fromHex("#d32f2f") else Color.fromHex("#9C27B0")
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
                        text = if (selectionMode) "Selecionados" else "Produtos de Higiene",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount produto(s) selecionado(s)"
                        } else {
                            if (productCount > 0) "$productCount produtos em estoque" else "Nenhum produto cadastrado"
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
                    onClick = onAddProductClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.fromHex("#9C27B0")
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
                        text = "Adicionar Produto",
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
                    tint = Color.fromHex("#9C27B0")
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
                focusedBorderColor = Color.fromHex("#9C27B0"),
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
            color = Color.fromHex("#9C27B0")
        )
    }
}

@Composable
private fun EmptyContent(
    onAddProductClick: () -> Unit
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
                imageVector = Icons.Default.CleaningServices,
                contentDescription = "Nenhum produto",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum produto de higiene cadastrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione seu primeiro produto para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddProductClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#9C27B0")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Produto")
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
                Text("Limpar busca", color = Color.fromHex("#9C27B0"))
            }
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<HygieneProduct>,
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onProductClick: (HygieneProduct) -> Unit,
    onEditClick: (HygieneProduct) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            HygieneCard(
                product = product,
                selectionMode = selectionMode,
                isSelected = selectedIds.contains(product.id),
                onClick = onProductClick,
                onEditClick = onEditClick
            )
        }
    }
}

@Composable
fun HygieneCard(
    product: HygieneProduct,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: (HygieneProduct) -> Unit = {},
    onEditClick: (HygieneProduct) -> Unit = {}
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
            ) { onClick(product) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color.fromHex("#9C27B0").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color.fromHex("#9C27B0"))
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
                        onCheckedChange = { onClick(product) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.fromHex("#9C27B0")
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onEditClick(product) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.fromHex("#9C27B0"),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "R$ ${String.format("%.2f", product.price)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex("#9C27B0"),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Estoque: ${product.stock} ${product.unit}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
        }
    }
}
