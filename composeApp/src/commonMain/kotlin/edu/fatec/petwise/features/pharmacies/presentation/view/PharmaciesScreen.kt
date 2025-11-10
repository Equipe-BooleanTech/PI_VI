package edu.fatec.petwise.features.pharmacies.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.pharmacies.domain.models.*
import edu.fatec.petwise.features.pharmacies.presentation.components.*
import edu.fatec.petwise.features.pharmacies.presentation.viewmodel.*
import edu.fatec.petwise.features.pharmacies.presentation.forms.SearchPharmacyFormSchema
import edu.fatec.petwise.features.pharmacies.di.PharmacyDependencyContainer
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.presentation.shared.form.FormEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmaciesScreen(
    navigationKey: Any? = null
) {
    println("PharmaciesScreen - Carregando farmácias")
    
    val pharmaciesViewModel = remember {
        PharmaciesViewModel(PharmacyDependencyContainer.providePharmacyUseCases())
    }
    
    val searchFormViewModel = remember {
        DynamicFormViewModel(SearchPharmacyFormSchema.configuration)
    }
    
    val theme = PetWiseTheme.Light
    val pharmaciesState by pharmaciesViewModel.uiState.collectAsStateWithLifecycle()
    
    var showSearchSheet by remember { mutableStateOf(false) }

    LaunchedEffect(navigationKey) {
        println("PharmaciesScreen: Recarregando farmácias - navigationKey: $navigationKey")
        pharmaciesViewModel.handleEvent(PharmaciesUiEvent.LoadPharmacies)
    }

    LaunchedEffect(Unit) {
        searchFormViewModel.events.collect { event ->
            when (event) {
                is FormEvent.FormSubmitted -> {
                    if (event.isValid) {
                        val formData = event.values
                        val searchQuery = formData["query"] as? String ?: ""
                        val verified = formData["verified"] as? Boolean
                        
                        val filterOptions = PharmacyFilterOptions(
                            verified = verified,
                            searchQuery = searchQuery
                        )
                        
                        pharmaciesViewModel.handleEvent(PharmaciesUiEvent.FilterPharmacies(filterOptions))
                        showSearchSheet = false
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            PharmaciesTopBar(
                searchQuery = pharmaciesState.searchQuery,
                onSearchQueryChange = { query ->
                    pharmaciesViewModel.handleEvent(PharmaciesUiEvent.SearchPharmacies(query))
                },
                onSearchClick = { showSearchSheet = true },
                onRefresh = {
                    pharmaciesViewModel.handleEvent(PharmaciesUiEvent.LoadPharmacies)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when {
                pharmaciesState.isLoading -> {
                    PharmaciesLoadingState()
                }
                pharmaciesState.errorMessage != null -> {
                    val currentErrorMessage = pharmaciesState.errorMessage
                    PharmaciesErrorState(
                        errorMessage = currentErrorMessage!!,
                        onRetry = {
                            pharmaciesViewModel.handleEvent(PharmaciesUiEvent.ClearError)
                            pharmaciesViewModel.handleEvent(PharmaciesUiEvent.LoadPharmacies)
                        }
                    )
                }
                pharmaciesState.filteredPharmacies.isEmpty() -> {
                    PharmaciesEmptyState(
                        isSearching = pharmaciesState.searchQuery.isNotBlank() || 
                                    pharmaciesState.filterOptions.searchQuery.isNotBlank(),
                        onClearSearch = {
                            pharmaciesViewModel.handleEvent(PharmaciesUiEvent.SearchPharmacies(""))
                            pharmaciesViewModel.handleEvent(
                                PharmaciesUiEvent.FilterPharmacies(PharmacyFilterOptions())
                            )
                        }
                    )
                }
                else -> {
                    PharmaciesContent(
                        pharmacies = pharmaciesState.filteredPharmacies,
                        onPharmacyClick = { pharmacy ->
                            pharmaciesViewModel.handleEvent(PharmaciesUiEvent.SelectPharmacy(pharmacy))
                            pharmaciesViewModel.handleEvent(PharmaciesUiEvent.ShowPharmacyDetails)
                        }
                    )
                }
            }

            pharmaciesState.errorMessage?.let { errorMessage ->
                PharmacyErrorSnackbar(
                    message = errorMessage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (showSearchSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSearchSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filtros de Busca",
                    style = theme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                DynamicForm(
                    viewModel = searchFormViewModel,
                    modifier = Modifier.fillMaxWidth(),
                    onSubmitSuccess = { /* Handled in LaunchedEffect */ },
                    onSubmitError = { error ->
                        println("Erro no formulário de busca: ${error.message}")
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (pharmaciesState.showPharmacyDetails && pharmaciesState.selectedPharmacy != null) {
        val currentSelectedPharmacy = pharmaciesState.selectedPharmacy
        PharmacyDetailsDialog(
            pharmacy = currentSelectedPharmacy!!,
            onDismiss = {
                pharmaciesViewModel.handleEvent(PharmaciesUiEvent.HidePharmacyDetails)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PharmaciesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit
) {
    val theme = PetWiseTheme.Light

    TopAppBar(
        title = {
            Text(
                text = "Farmácias",
                style = theme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtrar",
                    tint = Color.White
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.fromHex("#4CAF50")
        )
    )
}

@Composable
private fun PharmaciesLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.fromHex("#4CAF50"),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Carregando farmácias...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PharmaciesErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Erro",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "Ops! Algo deu errado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#4CAF50")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tentar novamente")
            }
        }
    }
}

@Composable
private fun PharmaciesEmptyState(
    isSearching: Boolean,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.LocalPharmacy,
                contentDescription = if (isSearching) "Nenhum resultado" else "Nenhuma farmácia",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = if (isSearching) "Nenhuma farmácia encontrada" else "Nenhuma farmácia cadastrada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (isSearching) 
                    "Tente ajustar os filtros de busca ou pesquisar por outros termos." 
                else 
                    "Ainda não há farmácias cadastradas na plataforma.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            if (isSearching) {
                OutlinedButton(
                    onClick = onClearSearch,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.fromHex("#4CAF50")
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpar filtros")
                }
            }
        }
    }
}

@Composable
private fun PharmaciesContent(
    pharmacies: List<Pharmacy>,
    onPharmacyClick: (Pharmacy) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = pharmacies,
            key = { it.id }
        ) { pharmacy ->
            PharmacyCard(
                pharmacy = pharmacy,
                onClick = onPharmacyClick
            )
        }
    }
}
