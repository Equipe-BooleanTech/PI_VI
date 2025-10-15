package edu.fatec.petwise.features.consultas.presentation.view

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
import edu.fatec.petwise.features.consultas.domain.models.*
import edu.fatec.petwise.features.consultas.presentation.components.*
import edu.fatec.petwise.features.consultas.presentation.viewmodel.*
import edu.fatec.petwise.features.consultas.di.ConsultaDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultasScreen() {
    val consultasViewModel = remember { ConsultaDependencyContainer.provideConsultasViewModel() }
    val addConsultaViewModel = remember { ConsultaDependencyContainer.provideAddConsultaViewModel() }
    val updateConsultaViewModel = remember { ConsultaDependencyContainer.provideUpdateConsultaViewModel() }
    val theme = PetWiseTheme.Light
    val consultasState by consultasViewModel.uiState.collectAsState()
    val addConsultaState by addConsultaViewModel.uiState.collectAsState()
    val updateConsultaState by updateConsultaViewModel.uiState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedConsultaIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditConsultaDialog by remember { mutableStateOf(false) }
    var consultaToEdit by remember { mutableStateOf<Consulta?>(null) }

    LaunchedEffect(addConsultaState.isSuccess) {
        if (addConsultaState.isSuccess) {
            consultasViewModel.onEvent(ConsultasUiEvent.HideAddConsultaDialog)
            consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas)
            addConsultaViewModel.onEvent(AddConsultaUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateConsultaState.isSuccess) {
        if (updateConsultaState.isSuccess) {
            showEditConsultaDialog = false
            consultaToEdit = null
            consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas)
            updateConsultaViewModel.onEvent(UpdateConsultaUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        ConsultasHeader(
            consultaCount = consultasState.filteredConsultas.size,
            selectionMode = selectionMode,
            selectedCount = selectedConsultaIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { showFilterSheet = true },
            onAddConsultaClick = { consultasViewModel.onEvent(ConsultasUiEvent.ShowAddConsultaDialog) },
            onSelectionModeToggle = { 
                selectionMode = !selectionMode
                if (!selectionMode) selectedConsultaIds = setOf()
            },
            onDeleteSelected = { 
                if (selectedConsultaIds.isNotEmpty()) {
                    showDeleteConfirmation = true
                }
            }
        )

        if (showSearchBar) {
            SearchBar(
                query = consultasState.searchQuery,
                onQueryChange = { query ->
                    consultasViewModel.onEvent(ConsultasUiEvent.SearchConsultas(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                consultasState.isLoading -> {
                    LoadingContent()
                }
                consultasState.filteredConsultas.isEmpty() -> {
                    EmptyContent(
                        onAddConsultaClick = { consultasViewModel.onEvent(ConsultasUiEvent.ShowAddConsultaDialog) }
                    )
                }
                else -> {
                    ConsultasListContent(
                        consultas = consultasState.filteredConsultas,
                        selectionMode = selectionMode,
                        selectedConsultaIds = selectedConsultaIds,
                        onConsultaClick = { consulta ->
                            if (selectionMode) {
                                selectedConsultaIds = if (selectedConsultaIds.contains(consulta.id)) {
                                    selectedConsultaIds - consulta.id
                                } else {
                                    selectedConsultaIds + consulta.id
                                }
                            } else {
                                consultasViewModel.onEvent(ConsultasUiEvent.SelectConsulta(consulta))
                            }
                        },
                        onEditClick = { consulta ->
                            consultaToEdit = consulta
                            showEditConsultaDialog = true
                        },
                        onStatusChange = { consultaId ->
                        },
                        onMarkAsPaid = { consultaId ->
                            consultasViewModel.onEvent(ConsultasUiEvent.MarkAsPaid(consultaId))
                        }
                    )
                }
            }

            consultasState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    ConsultaErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { consultasViewModel.onEvent(ConsultasUiEvent.ClearError) },
                        actionLabel = "Tentar Novamente",
                        onAction = { consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas) }
                    )
                }
            }
        }
    }

    if (consultasState.showAddConsultaDialog) {
        AddConsultaDialog(
            addConsultaViewModel = addConsultaViewModel,
            isLoading = addConsultaState.isLoading,
            errorMessage = addConsultaState.errorMessage,
            onDismiss = {
                consultasViewModel.onEvent(ConsultasUiEvent.HideAddConsultaDialog)
                addConsultaViewModel.onEvent(AddConsultaUiEvent.ClearState)
            },
            onSuccess = {
                consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas)
            }
        )
    }

    if (showEditConsultaDialog && consultaToEdit != null) {
        EditConsultaDialog(
            consulta = consultaToEdit!!,
            updateConsultaViewModel = updateConsultaViewModel,
            isLoading = updateConsultaState.isLoading,
            errorMessage = updateConsultaState.errorMessage,
            onDismiss = {
                showEditConsultaDialog = false
                consultaToEdit = null
                updateConsultaViewModel.onEvent(UpdateConsultaUiEvent.ClearState)
            },
            onSuccess = {
                consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas)
            }
        )
    }

    if (showFilterSheet) {
        FilterConsultasBottomSheet(
            currentFilter = consultasState.filterOptions,
            onFilterApply = { filterOptions ->
                consultasViewModel.onEvent(ConsultasUiEvent.FilterConsultas(filterOptions))
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            consultaCount = selectedConsultaIds.size,
            onConfirm = {
                selectedConsultaIds.forEach { consultaId ->
                    consultasViewModel.onEvent(ConsultasUiEvent.DeleteConsulta(consultaId))
                }
                selectedConsultaIds = setOf()
                selectionMode = false
                showDeleteConfirmation = false
                // Refresh the consultas list after delete
                consultasViewModel.onEvent(ConsultasUiEvent.LoadConsultas)
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@Composable
private fun ConsultasHeader(
    consultaCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddConsultaClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    val theme = PetWiseTheme.Light

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode) Color.fromHex("#d32f2f") else Color.fromHex("#2196F3")
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
                        text = if (selectionMode) "Selecionados" else "Consultas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount consulta(s) selecionada(s)"
                        } else {
                            if (consultaCount > 0) "$consultaCount consulta(s) agendada(s)" else "Nenhuma consulta agendada"
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
                                contentDescription = "Cancelar consultas selecionadas",
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
                    onClick = onAddConsultaClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.fromHex("#2196F3")
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
                        text = "Agendar Consulta",
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
                        contentDescription = "Cancelar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancelar Selecionadas ($selectedCount)",
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
                    "Buscar por pet, veterinário ou sintomas...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.fromHex("#2196F3")
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
                focusedBorderColor = Color.fromHex("#2196F3"),
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
            color = Color.fromHex("#2196F3")
        )
    }
}

@Composable
private fun EmptyContent(
    onAddConsultaClick: () -> Unit
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
                imageVector = Icons.Default.EventNote,
                contentDescription = "Nenhuma consulta",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhuma consulta agendada",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Agende a primeira consulta para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddConsultaClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#2196F3")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agendar Consulta")
            }
        }
    }
}

@Composable
private fun ConsultasListContent(
    consultas: List<Consulta>,
    selectionMode: Boolean,
    selectedConsultaIds: Set<String>,
    onConsultaClick: (Consulta) -> Unit,
    onEditClick: (Consulta) -> Unit,
    onStatusChange: ((String) -> Unit)? = null,
    onMarkAsPaid: ((String) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(consultas, key = { it.id }) { consulta ->
            ConsultaCard(
                consulta = consulta,
                selectionMode = selectionMode,
                isSelected = selectedConsultaIds.contains(consulta.id),
                onClick = onConsultaClick,
                onEditClick = onEditClick,
                onStatusChange = onStatusChange,
                onMarkAsPaid = onMarkAsPaid
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    consultaCount: Int,
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
                text = "Confirmar Cancelamento",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
        },
        text = {
            Text(
                text = "Tem certeza que deseja cancelar ${if (consultaCount == 1) "esta consulta" else "estas $consultaCount consultas"}? O status será alterado para \"Cancelada\".",
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
                Text("Cancelar Consulta${if (consultaCount > 1) "s" else ""}", color = Color.White)
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
