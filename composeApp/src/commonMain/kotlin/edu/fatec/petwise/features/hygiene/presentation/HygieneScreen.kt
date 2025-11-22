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
import edu.fatec.petwise.presentation.shared.NumberFormatter
import edu.fatec.petwise.features.hygiene.di.HygieneDependencyContainer
import edu.fatec.petwise.features.hygiene.presentation.HygieneUiEvent
import edu.fatec.petwise.features.hygiene.presentation.components.AddHygieneDialog
import edu.fatec.petwise.features.hygiene.presentation.components.EditHygieneDialog
import edu.fatec.petwise.features.hygiene.presentation.components.DeleteHygieneConfirmationDialog
@Composable
fun HygieneScreen() {
    val viewModel: HygieneViewModel = remember { HygieneDependencyContainer.hygieneViewModel }
    val uiState: HygieneUiState by viewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedProductIds by remember { mutableStateOf(setOf<String>()) }

    // Dialog states
    var showAddHygieneDialog by remember { mutableStateOf(false) }
    var showEditHygieneDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<HygieneProduct?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<HygieneProduct?>(null) }

    val theme = PetWiseTheme.Light

    val filteredProducts = remember(uiState.products, uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty()) {
            uiState.products
        } else {
            uiState.products.filter {
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
        HygieneHeader(
            productCount = filteredProducts.size,
            selectionMode = selectionMode,
            selectedCount = selectedProductIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddProductClick = { showAddHygieneDialog = true },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedProductIds = setOf()
            },
            onDeleteSelected = { /* TODO: Implement delete */ }
        )

        if (showSearchBar) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onEvent(HygieneUiEvent.SearchProducts(it)) },
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
                filteredProducts.isEmpty() && uiState.searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddProductClick = { showAddHygieneDialog = true }
                    )
                }
                filteredProducts.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { viewModel.onEvent(HygieneUiEvent.SearchProducts("")) })
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
                        onEditClick = { product ->
                            productToEdit = product
                            showEditHygieneDialog = true
                        },
                        onDeleteClick = { product ->
                            productToDelete = product
                            showDeleteConfirmation = true
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddHygieneDialog) {
        AddHygieneDialog(
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                showAddHygieneDialog = false
            },
            onSuccess = { formData: Map<String, Any> ->
                // Convert form data to HygieneProduct model
                val product = HygieneProduct(
                    id = "",
                    name = formData["name"] as String,
                    brand = formData["brand"] as String,
                    category = formData["category"] as String,
                    description = formData["description"] as? String,
                    price = (formData["price"] as? Double) ?: 0.0,
                    stock = (formData["stock"] as? Int) ?: 0,
                    unit = formData["unit"] as String,
                    expiryDate = formData["expiryDate"] as? String,
                    imageUrl = formData["imageUrl"] as? String,
                    active = true,
                    createdAt = "",
                    updatedAt = ""
                )
                viewModel.onEvent(HygieneUiEvent.AddProduct(product))
                showAddHygieneDialog = false
            }
        )
    }

    if (showEditHygieneDialog && productToEdit != null) {
        EditHygieneDialog(
            product = productToEdit!!,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                showEditHygieneDialog = false
                productToEdit = null
            },
            onSuccess = { formData: Map<String, Any> ->
                // Convert form data to updated HygieneProduct model
                val updatedProduct = productToEdit!!.copy(
                    name = formData["name"] as String,
                    brand = formData["brand"] as String,
                    category = formData["category"] as String,
                    description = formData["description"] as? String,
                    price = (formData["price"] as? Double) ?: 0.0,
                    stock = (formData["stock"] as? Int) ?: 0,
                    unit = formData["unit"] as String,
                    expiryDate = formData["expiryDate"] as? String,
                    imageUrl = formData["imageUrl"] as? String
                )
                viewModel.onEvent(HygieneUiEvent.UpdateProduct(updatedProduct))
                showEditHygieneDialog = false
                productToEdit = null
            }
        )
    }

    if (showDeleteConfirmation && productToDelete != null) {
        DeleteHygieneConfirmationDialog(
            productId = productToDelete!!.id,
            productName = productToDelete!!.name,
            onSuccess = {
                viewModel.onEvent(HygieneUiEvent.DeleteProduct(productToDelete!!.id))
                showDeleteConfirmation = false
                productToDelete = null
            },
            onCancel = {
                showDeleteConfirmation = false
                productToDelete = null
            }
        )
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
            containerColor = if (selectionMode) fromHex("#d32f2f") else fromHex("#9C27B0")
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
                        contentColor = fromHex("#9C27B0")
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
                    tint = fromHex("#9C27B0")
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
                focusedBorderColor = fromHex("#9C27B0"),
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
            color = fromHex("#9C27B0")
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
                    containerColor = fromHex("#9C27B0")
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
                Text("Limpar busca", color = fromHex("#9C27B0"))
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
    onEditClick: (HygieneProduct) -> Unit,
    onDeleteClick: (HygieneProduct) -> Unit
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
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
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
    onEditClick: (HygieneProduct) -> Unit = {},
    onDeleteClick: (HygieneProduct) -> Unit = {}
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
                isSelected -> fromHex("#9C27B0").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, fromHex("#9C27B0"))
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
                            checkedColor = fromHex("#9C27B0")
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDeleteClick(product) },
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
                        onClick = { onEditClick(product) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = fromHex("#9C27B0"),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = fromHex(theme.palette.textPrimary)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = fromHex(theme.palette.textSecondary)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = NumberFormatter.formatCurrency(product.price),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = fromHex("#9C27B0"),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Estoque: ${product.stock} ${product.unit}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = fromHex(theme.palette.textSecondary)
                )
            )
        }
    }
}
