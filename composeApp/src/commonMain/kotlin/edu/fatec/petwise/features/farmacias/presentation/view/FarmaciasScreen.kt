package edu.fatec.petwise.features.farmacias.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.farmacias.di.FarmaciaDependencyContainer
import edu.fatec.petwise.features.farmacias.domain.models.*
import edu.fatec.petwise.features.farmacias.presentation.components.FarmaciaCard
import edu.fatec.petwise.features.farmacias.presentation.viewmodel.*
import edu.fatec.petwise.navigation.NavigationManager

/**
 * Tela principal de listagem de farmácias.
 * 
 * Features:
 * - Listagem com busca
 * - Filtros múltiplos
 * - Ações CRUD
 * - Estados de loading/error
 * - Navegação integrada
 * 
 * @param navigationManager Gerenciador de navegação do app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmaciasScreen(
    navigationManager: NavigationManager
) {
    val viewModel = remember { FarmaciaDependencyContainer.provideFarmaciasViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Farmácias Parceiras",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filtros")
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Adicionar Farmácia")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, "Adicionar") },
                text = { Text("Nova Farmácia") },
                containerColor = Color(0xFF4CAF50)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de busca
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { 
                    viewModel.onEvent(FarmaciasUiEvent.SearchFarmacias(it)) 
                }
            )

            // Chips de filtro rápido
            FilterChips(
                showApenasAtivas = uiState.showApenasAtivas,
                onShowAtivasClick = { 
                    viewModel.onEvent(FarmaciasUiEvent.LoadApenasAtivas) 
                },
                onShowAllClick = { 
                    viewModel.onEvent(FarmaciasUiEvent.LoadFarmacias) 
                }
            )

            // Estado de loading
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Estado de erro
            else if (uiState.errorMessage != null) {
                ErrorState(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.onEvent(FarmaciasUiEvent.LoadFarmacias) },
                    onDismiss = { viewModel.onEvent(FarmaciasUiEvent.ClearError) }
                )
            }
            
            // Lista de farmácias
            else if (uiState.filteredFarmacias.isEmpty()) {
                EmptyState()
            }
            
            // Conteúdo principal
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${uiState.filteredFarmacias.size} farmácia(s) encontrada(s)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = uiState.filteredFarmacias,
                        key = { it.id }
                    ) { farmacia ->
                        FarmaciaCard(
                            farmacia = farmacia,
                            onViewDetails = { 
                                // TODO: Navigate to details screen
                            },
                            onEdit = { 
                                // TODO: Navigate to edit screen
                            },
                            onDelete = { 
                                viewModel.onEvent(FarmaciasUiEvent.DeleteFarmacia(farmacia.id)) 
                            },
                            onUpdateStatus = { status ->
                                viewModel.onEvent(
                                    FarmaciasUiEvent.UpdateStatus(farmacia.id, status, null)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showFilterDialog) {
        FilterDialog(
            currentFilters = uiState.filterOptions,
            onDismiss = { showFilterDialog = false },
            onApply = { filters ->
                viewModel.onEvent(FarmaciasUiEvent.FilterFarmacias(filters))
                showFilterDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddFarmaciaDialog(
            onDismiss = { showAddDialog = false },
            onSuccess = {
                showAddDialog = false
                viewModel.onEvent(FarmaciasUiEvent.LoadFarmacias)
            }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar farmácias...") },
        leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Limpar")
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun FilterChips(
    showApenasAtivas: Boolean,
    onShowAtivasClick: () -> Unit,
    onShowAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = showApenasAtivas,
            onClick = onShowAtivasClick,
            label = { Text("Apenas Ativas") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
        
        FilterChip(
            selected = !showApenasAtivas,
            onClick = onShowAllClick,
            label = { Text("Todas") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalPharmacy,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Text(
                text = "Nenhuma farmácia encontrada",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cadastre uma nova farmácia parceira",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Erro",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onRetry) {
                    Text("Tentar Novamente")
                }
                TextButton(onClick = onDismiss) {
                    Text("Dispensar")
                }
            }
        }
    }
}

@Composable
private fun FilterDialog(
    currentFilters: FarmaciaFilterOptions,
    onDismiss: () -> Unit,
    onApply: (FarmaciaFilterOptions) -> Unit
) {
    var selectedTipo by remember { mutableStateOf(currentFilters.tipo) }
    var selectedStatus by remember { mutableStateOf(currentFilters.status) }
    var selectedRegiao by remember { mutableStateOf(currentFilters.regiao) }
    var apenasComCredito by remember { mutableStateOf(currentFilters.apenasComCredito) }
    var apenasFreteGratis by remember { mutableStateOf(currentFilters.apenasFreteGratis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros Avançados") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tipo
                Text("Tipo de Farmácia", style = MaterialTheme.typography.labelLarge)
                // TODO: Dropdown para TipoFarmacia
                
                // Status
                Text("Status", style = MaterialTheme.typography.labelLarge)
                // TODO: Dropdown para StatusFarmacia
                
                // Região
                Text("Região", style = MaterialTheme.typography.labelLarge)
                // TODO: Dropdown para RegiaoAtuacao
                
                // Checkboxes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = apenasComCredito,
                        onCheckedChange = { apenasComCredito = it }
                    )
                    Text("Apenas com crédito disponível")
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = apenasFreteGratis,
                        onCheckedChange = { apenasFreteGratis = it }
                    )
                    Text("Apenas com frete grátis")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        FarmaciaFilterOptions(
                            tipo = selectedTipo,
                            status = selectedStatus,
                            regiao = selectedRegiao,
                            apenasComCredito = apenasComCredito,
                            apenasFreteGratis = apenasFreteGratis
                        )
                    )
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun AddFarmaciaDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val viewModel = remember { FarmaciaDependencyContainer.provideAddFarmaciaViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
            viewModel.clearState()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Farmácia") },
        text = {
            Column {
                Text(
                    "Use o formulário completo para cadastrar uma nova farmácia parceira com todos os dados necessários.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.errorMessage != null) {
                    Text(
                        uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { /* TODO: Navigate to full form */ }) {
                Text("Abrir Formulário")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
