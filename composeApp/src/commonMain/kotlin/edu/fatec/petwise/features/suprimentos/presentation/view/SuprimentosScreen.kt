package edu.fatec.petwise.features.suprimentos.presentation.view

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.features.suprimentos.domain.models.*
import edu.fatec.petwise.features.suprimentos.presentation.components.*
import edu.fatec.petwise.features.suprimentos.presentation.viewmodel.*
import edu.fatec.petwise.features.suprimentos.di.SuprimentoDependencyContainer
import edu.fatec.petwise.features.suprimentos.presentation.forms.createAddSuprimentoFormConfigurationForPet
import edu.fatec.petwise.features.suprimentos.presentation.forms.createEditSuprimentoFormConfiguration
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.presentation.shared.NumberFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuprimentosScreen(
    petId: String,
    navigationKey: Any? = null
) {
    val suprimentosViewModel = remember { SuprimentoDependencyContainer.provideSuprimentosViewModel() }
    val addSuprimentoViewModel = remember { SuprimentoDependencyContainer.provideAddSuprimentoViewModel() }
    val updateSuprimentoViewModel = remember { SuprimentoDependencyContainer.provideUpdateSuprimentoViewModel() }
    
    val theme = PetWiseTheme.Light
    val suprimentosState by suprimentosViewModel.uiState.collectAsState()
    val addSuprimentoState by addSuprimentoViewModel.uiState.collectAsState()
    val updateSuprimentoState by updateSuprimentoViewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var suprimentoToEdit by remember { mutableStateOf<Suprimento?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var selectedSuprimento by remember { mutableStateOf<Suprimento?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var suprimentoToDelete by remember { mutableStateOf<Suprimento?>(null) }

    LaunchedEffect(navigationKey, petId) {
        println("SuprimentosScreen: Carregando suprimentos - petId: $petId, navigationKey: $navigationKey")
        suprimentosViewModel.handleEvent(SuprimentosUiEvent.LoadSuprimentosByPet(petId))
    }

    LaunchedEffect(addSuprimentoState.addedSuprimento) {
        if (addSuprimentoState.addedSuprimento != null) {
            showAddDialog = false
            suprimentosViewModel.handleEvent(SuprimentosUiEvent.LoadSuprimentosByPet(petId))
            addSuprimentoViewModel.handleEvent(AddSuprimentoUiEvent.ClearSuccess)
        }
    }

    LaunchedEffect(updateSuprimentoState.updatedSuprimento) {
        if (updateSuprimentoState.updatedSuprimento != null) {
            showEditDialog = false
            suprimentoToEdit = null
            suprimentosViewModel.handleEvent(SuprimentosUiEvent.LoadSuprimentosByPet(petId))
            updateSuprimentoViewModel.handleEvent(UpdateSuprimentoUiEvent.ClearSuccess)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        SuprimentosHeader(
            suprimentoCount = suprimentosState.filteredSuprimentos.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { showFilterSheet = true },
            onAddClick = { 
                showAddDialog = true
            },
        )

        if (showSearchBar) {
            SearchBar(
                query = suprimentosState.currentSearchQuery,
                onQueryChange = { query ->
                    suprimentosViewModel.handleEvent(
                        SuprimentosUiEvent.SearchSuprimentos(
                            SuprimentoSearchCriteria(query = query)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        SuprimentosStatusCard(
            suprimentos = suprimentosState.filteredSuprimentos,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                suprimentosState.isLoading -> {
                    LoadingContent()
                }
                suprimentosState.filteredSuprimentos.isEmpty() -> {
                    EmptyContent(
                        onAddClick = { 
                            showAddDialog = true
                        }
                    )
                }
                else -> {
                    SuprimentosListContent(
                        suprimentos = suprimentosState.filteredSuprimentos,
                        onSuprimentoClick = { suprimento ->
                            selectedSuprimento = suprimento
                            showDetailsDialog = true
                        },
                        onEditClick = { suprimento ->
                            suprimentoToEdit = suprimento
                            showEditDialog = true
                        },
                        onDeleteClick = { suprimento ->
                            suprimentoToDelete = suprimento
                            showDeleteConfirmation = true
                        }
                    )
                }
            }

            suprimentosState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    SuprimentoErrorSnackbar(
                        message = errorMessage,
                        isVisible = true,
                        onDismiss = { suprimentosViewModel.handleEvent(SuprimentosUiEvent.ClearError) },
                        onRetry = { suprimentosViewModel.handleEvent(SuprimentosUiEvent.LoadSuprimentosByPet(petId)) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSuprimentoDialog(
            petId = petId,
            addSuprimentoViewModel = addSuprimentoViewModel,
            isLoading = addSuprimentoState.isLoading,
            errorMessage = addSuprimentoState.errorMessage,
            onDismiss = { 
                showAddDialog = false
                addSuprimentoViewModel.handleEvent(AddSuprimentoUiEvent.ClearError)
            },
        )
    }

    suprimentoToEdit?.let { suprimento ->
        if (showEditDialog) {
            EditSuprimentoDialog(
                suprimento = suprimento,
                updateSuprimentoViewModel = updateSuprimentoViewModel,
                isLoading = updateSuprimentoState.isLoading,
                errorMessage = updateSuprimentoState.errorMessage,
                onDismiss = {
                    showEditDialog = false
                    suprimentoToEdit = null
                    updateSuprimentoViewModel.handleEvent(UpdateSuprimentoUiEvent.ClearError)
                },
            )
        }
    }

    selectedSuprimento?.let { suprimento ->
        if (showDetailsDialog) {
            SuprimentoDetailsDialog(
                suprimento = suprimento,
                onDismiss = {
                    showDetailsDialog = false
                    selectedSuprimento = null
                },
                onEdit = { sup ->
                    showDetailsDialog = false
                    suprimentoToEdit = sup
                    showEditDialog = true
                },
                onDelete = { sup ->
                    showDetailsDialog = false
                    suprimentoToDelete = sup
                    showDeleteConfirmation = true
                }
            )
        }
    }

    suprimentoToDelete?.let { suprimento ->
        if (showDeleteConfirmation) {
            DeleteSuprimentoConfirmationDialog(
                suprimento = suprimento,
                onDismiss = {
                    showDeleteConfirmation = false
                    suprimentoToDelete = null
                },
                onConfirm = {
                    suprimentosViewModel.handleEvent(SuprimentosUiEvent.DeleteSuprimento(suprimento.id))
                    showDeleteConfirmation = false
                    suprimentoToDelete = null
                    suprimentosViewModel.handleEvent(SuprimentosUiEvent.LoadSuprimentosByPet(petId))
                }
            )
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = suprimentosState.currentFilterOptions ?: SuprimentoFilterOptions(),
            onFilterApply = { filterOptions ->
                suprimentosViewModel.handleEvent(SuprimentosUiEvent.FilterSuprimentos(filterOptions))
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun SuprimentosHeader(
    suprimentoCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddClick: () -> Unit
) {
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
                        text = "Suprimentos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (suprimentoCount > 0) 
                            "$suprimentoCount ${if (suprimentoCount == 1) "item" else "itens"} cadastrados" 
                        else 
                            "Nenhum item cadastrado",
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
                    onClick = onAddClick,
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
                        text = "Adicionar Suprimento",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }


@Composable
private fun SuprimentosStatusCard(
    suprimentos: List<Suprimento>,
    modifier: Modifier = Modifier
) {
    val totalGasto = suprimentos.sumOf { it.price.toDouble() }
    val categoriaComMaisItens = suprimentos
        .groupBy { it.category }
        .maxByOrNull { it.value.size }
        ?.key

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Estatísticas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    icon = Icons.Default.ShoppingCart,
                    label = "Total de Itens",
                    value = suprimentos.size.toString(),
                    color = Color(0xFF2196F3)
                )

                StatisticItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Gasto Total",
                    value = NumberFormatter.formatCurrency(totalGasto),
                    color = Color(0xFF4CAF50)
                )

                if (categoriaComMaisItens != null) {
                    StatisticItem(
                        icon = Icons.Default.Category,
                        label = "Categoria Principal",
                        value = categoriaComMaisItens.displayName,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
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
                    "Buscar por descrição, loja ou categoria...",
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
    onAddClick: () -> Unit
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
                imageVector = Icons.Default.Inventory,
                contentDescription = "Nenhum suprimento",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum suprimento cadastrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione seu primeiro item para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex("#00b942")
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar Suprimento")
                }
            }
        }
    }

@Composable
private fun SuprimentosListContent(
    suprimentos: List<Suprimento>,
    onSuprimentoClick: (Suprimento) -> Unit,
    onEditClick: (Suprimento) -> Unit,
    onDeleteClick: (Suprimento) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suprimentos, key = { it.id }) { suprimento ->
            SuprimentoCard(
                suprimento = suprimento,
                onClick = onSuprimentoClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun FilterBottomSheet(
    currentFilter: SuprimentoFilterOptions,
    onFilterApply: (SuprimentoFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: Implement filter bottom sheet with category, price range, date range filters
    // For now, just dismiss
    onDismiss()
}

@Composable
private fun AddSuprimentoDialog(
    petId: String,
    addSuprimentoViewModel: AddSuprimentoViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val addSuprimentoState by addSuprimentoViewModel.uiState.collectAsState()

    val formConfiguration = createAddSuprimentoFormConfigurationForPet()

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_suprimento_form") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    LaunchedEffect(addSuprimentoState.addedSuprimento) {
        if (addSuprimentoState.addedSuprimento != null) {
            formViewModel.resetForm()
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
                                text = "Adicionar Suprimento",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Registre um novo suprimento para este pet",
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
                                val category = SuprimentCategory.fromDisplayName(values["category"]?.toString() ?: "")
                                val price = values["price"]?.toString()?.toFloatOrNull() ?: 0.0f

                                val suprimento = addSuprimentoViewModel.createSuprimento(
                                    petId = petId,
                                    description = values["description"]?.toString() ?: "",
                                    category = category.displayName,
                                    price = price,
                                    orderDate = values["orderDate"]?.toString() ?: "",
                                    shopName = values["shopName"]?.toString() ?: ""
                                )

                                addSuprimentoViewModel.handleEvent(AddSuprimentoUiEvent.AddSuprimento(suprimento))
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
private fun EditSuprimentoDialog(
    suprimento: Suprimento,
    updateSuprimentoViewModel: UpdateSuprimentoViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val updateSuprimentoState by updateSuprimentoViewModel.uiState.collectAsState()

    val formConfiguration = remember(suprimento) {
        createEditSuprimentoFormConfiguration(suprimento, emptyList())
    }

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_suprimento_form_${suprimento.id}") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    LaunchedEffect(suprimento) {
        formViewModel.resetForm()
        formViewModel.updateConfiguration(createEditSuprimentoFormConfiguration(suprimento, emptyList()))
        updateSuprimentoViewModel.handleEvent(UpdateSuprimentoUiEvent.LoadSuprimento(suprimento.id))
    }

    LaunchedEffect(updateSuprimentoState.updatedSuprimento) {
        if (updateSuprimentoState.updatedSuprimento != null) {
            formViewModel.resetForm()
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
                                text = "Editar Suprimento",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Atualize as informações do suprimento",
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
                                val category = SuprimentCategory.fromDisplayName(values["category"]?.toString() ?: "")
                                val price = values["price"]?.toString()?.toFloatOrNull() ?: 0.0f

                                val updatedSuprimento = updateSuprimentoViewModel.updateSuprimentoData(
                                    current = suprimento,
                                    petId = suprimento.petId, // Keep the same petId
                                    description = values["description"]?.toString() ?: "",
                                    category = category.displayName,
                                    price = price,
                                    orderDate = values["orderDate"]?.toString() ?: "",
                                    shopName = values["shopName"]?.toString() ?: ""
                                )

                                updateSuprimentoViewModel.handleEvent(UpdateSuprimentoUiEvent.UpdateSuprimento(updatedSuprimento))
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
