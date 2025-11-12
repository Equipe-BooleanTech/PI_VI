package edu.fatec.petwise.features.veterinaries.presentation.view

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
import edu.fatec.petwise.features.veterinaries.domain.models.*
import edu.fatec.petwise.features.veterinaries.presentation.components.*
import edu.fatec.petwise.features.veterinaries.presentation.viewmodel.*
import edu.fatec.petwise.features.veterinaries.presentation.forms.SearchVeterinaryFormSchema
import edu.fatec.petwise.features.veterinaries.di.VeterinaryDependencyContainer
import edu.fatec.petwise.presentation.shared.form.DynamicForm
import edu.fatec.petwise.presentation.shared.form.DynamicFormViewModel
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.presentation.shared.form.FormEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinariesScreen(
    navigationKey: Any? = null
) {
    println("VeterinariesScreen - Carregando veterinários")
    
    val veterinariesViewModel = remember {
        VeterinariesViewModel(VeterinaryDependencyContainer.provideVeterinaryUseCases())
    }
    
    val searchFormViewModel = remember {
        DynamicFormViewModel(SearchVeterinaryFormSchema.configuration)
    }
    
    val theme = PetWiseTheme.Light
    val veterinariesState by veterinariesViewModel.uiState.collectAsStateWithLifecycle()
    
    var showSearchSheet by remember { mutableStateOf(false) }

    LaunchedEffect(navigationKey) {
        println("VeterinariesScreen: Recarregando veterinários - navigationKey: $navigationKey")
        veterinariesViewModel.handleEvent(VeterinariesUiEvent.LoadVeterinaries)
    }

    LaunchedEffect(Unit) {
        searchFormViewModel.events.collect { event ->
            when (event) {
                is FormEvent.FormSubmitted -> {
                    if (event.isValid) {
                        val formData = event.values
                        val searchQuery = formData["query"] as? String ?: ""
                        val verified = formData["verified"] as? Boolean
                        
                        val filterOptions = VeterinaryFilterOptions(
                            verified = verified,
                            searchQuery = searchQuery
                        )
                        
                        veterinariesViewModel.handleEvent(VeterinariesUiEvent.FilterVeterinaries(filterOptions))
                        showSearchSheet = false
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            VeterinariesTopBar(
                onSearchClick = { showSearchSheet = true },
                onRefresh = {
                    veterinariesViewModel.handleEvent(VeterinariesUiEvent.LoadVeterinaries)
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
                veterinariesState.isLoading -> {
                    VeterinariesLoadingState()
                }
                veterinariesState.errorMessage != null -> {
                    val currentErrorMessage = veterinariesState.errorMessage
                    VeterinariesErrorState(
                        errorMessage = currentErrorMessage!!,
                        onRetry = {
                            veterinariesViewModel.handleEvent(VeterinariesUiEvent.ClearError)
                            veterinariesViewModel.handleEvent(VeterinariesUiEvent.LoadVeterinaries)
                        }
                    )
                }
                veterinariesState.filteredVeterinaries.isEmpty() -> {
                    VeterinariesEmptyState(
                        isSearching = veterinariesState.searchQuery.isNotBlank() || 
                                    veterinariesState.filterOptions.searchQuery.isNotBlank(),
                        onClearSearch = {
                            veterinariesViewModel.handleEvent(
                                VeterinariesUiEvent.FilterVeterinaries(VeterinaryFilterOptions())
                            )
                        }
                    )
                }
                else -> {
                    VeterinariesContent(
                        veterinaries = veterinariesState.filteredVeterinaries,
                        onVeterinaryClick = { veterinary ->
                            veterinariesViewModel.handleEvent(VeterinariesUiEvent.SelectVeterinary(veterinary))
                            veterinariesViewModel.handleEvent(VeterinariesUiEvent.ShowVeterinaryDetails)
                        }
                    )
                }
            }

            veterinariesState.errorMessage?.let { errorMessage ->
                VeterinaryErrorSnackbar(
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

    if (veterinariesState.showVeterinaryDetails && veterinariesState.selectedVeterinary != null) {
        val currentSelectedVeterinary = veterinariesState.selectedVeterinary
        VeterinaryDetailsDialog(
            veterinary = currentSelectedVeterinary!!,
            onDismiss = {
                veterinariesViewModel.handleEvent(VeterinariesUiEvent.HideVeterinaryDetails)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VeterinariesTopBar(
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit
) {
    val theme = PetWiseTheme.Light

    TopAppBar(
        title = {
            Text(
                text = "Veterinários",
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
private fun VeterinariesLoadingState() {
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
                text = "Carregando veterinários...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun VeterinariesErrorState(
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
private fun VeterinariesEmptyState(
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
                imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.LocalHospital,
                contentDescription = if (isSearching) "Nenhum resultado" else "Nenhum veterinário",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = if (isSearching) "Nenhum veterinário encontrado" else "Nenhum veterinário cadastrado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (isSearching) 
                    "Tente ajustar os filtros de busca ou pesquisar por outros termos." 
                else 
                    "Ainda não há veterinários cadastrados na plataforma.",
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
private fun VeterinariesContent(
    veterinaries: List<Veterinary>,
    onVeterinaryClick: (Veterinary) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = veterinaries,
            key = { it.id }
        ) { veterinary ->
            VeterinaryCard(
                veterinary = veterinary,
                onClick = onVeterinaryClick
            )
        }
    }
}