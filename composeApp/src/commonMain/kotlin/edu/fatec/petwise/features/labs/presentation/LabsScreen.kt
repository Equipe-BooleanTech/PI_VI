package edu.fatec.petwise.features.labs.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.presentation.components.AddLabResultDialog
import edu.fatec.petwise.features.labs.presentation.components.EditLabResultDialog
import edu.fatec.petwise.features.labs.presentation.components.DeleteLabResultConfirmationDialog
import edu.fatec.petwise.features.labs.presentation.viewmodel.LabsViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.LabsUiEvent
import edu.fatec.petwise.features.labs.presentation.viewmodel.UpdateLabResultViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.UpdateLabResultUiEvent
import edu.fatec.petwise.features.labs.presentation.viewmodel.AddLabResultViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.AddLabResultUiEvent
import edu.fatec.petwise.features.labs.di.LabDependencyContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(
    viewModel: LabsViewModel,
    navigationKey: Any? = null
) {
    val addLabResultViewModel = remember { LabDependencyContainer.addLabResultViewModel }
    val updateLabResultViewModel = remember { LabDependencyContainer.updateLabResultViewModel }
    val uiState by viewModel.uiState.collectAsState()
    val addUiState by addLabResultViewModel.uiState.collectAsState()
    val updateUiState by updateLabResultViewModel.uiState.collectAsState()
    val labResults = uiState.labResults
    val pendingLabResults = remember(labResults) {
        labResults.filter {
            it.status == "PENDING" || it.status == "IN_PROGRESS"
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var labResultToEdit by remember { mutableStateOf<LabResult?>(null) }
    var labResultToDelete by remember { mutableStateOf<LabResult?>(null) }

    LaunchedEffect(navigationKey) {
        viewModel.onEvent(LabsUiEvent.LoadLabResults)
    }

    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            showAddDialog = false
            viewModel.onEvent(LabsUiEvent.LoadLabResults)
            addLabResultViewModel.onEvent(AddLabResultUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateUiState.isSuccess) {
        if (updateUiState.isSuccess) {
            showEditDialog = false
            labResultToEdit = null
            viewModel.onEvent(LabsUiEvent.LoadLabResults)
            updateLabResultViewModel.onEvent(UpdateLabResultUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        LabsHeader(
            labResultCount = labResults.size,
            pendingCount = pendingLabResults.size,
            onAddLabResultClick = { showAddDialog = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                labResults.isEmpty() -> {
                    EmptyContent(
                        onAddLabResultClick = { showAddDialog = true }
                    )
                }
                else -> {
                    LabResultsListContent(
                        labResults = labResults,
                        pendingLabResults = pendingLabResults,
                        onEditLabResult = { labResult ->
                            labResultToEdit = labResult
                            showEditDialog = true
                        },
                        onDeleteLabResult = { labResult ->
                            labResultToDelete = labResult
                            showDeleteDialog = true
                        }
                    )
                }
            }

            uiState.error?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    LabResultErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { /* Clear error event */ },
                        actionLabel = "Tentar Novamente",
                        onAction = { viewModel.onEvent(LabsUiEvent.LoadLabResults) }
                    )
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddLabResultDialog(
            addLabResultViewModel = addLabResultViewModel,
            isLoading = addUiState.isLoading,
            errorMessage = addUiState.errorMessage,
            onDismiss = {
                showAddDialog = false
                addLabResultViewModel.onEvent(AddLabResultUiEvent.ClearState)
            },
            onSuccess = {
                viewModel.onEvent(LabsUiEvent.LoadLabResults)
            }
        )
    }

    // Delete Dialog
    labResultToDelete?.let { labResult ->
        if (showDeleteDialog) {
            DeleteLabResultConfirmationDialog(
                labResultId = labResult.id,
                labResultName = labResult.labType,
                onSuccess = {
                    showDeleteDialog = false
                    labResultToDelete = null
                    viewModel.onEvent(LabsUiEvent.LoadLabResults)
                },
                onCancel = {
                    showDeleteDialog = false
                    labResultToDelete = null
                }
            )
        }
    }

    // Edit Dialog
    labResultToEdit?.let { labResult ->
        if (showEditDialog) {
            EditLabResultDialog(
                updateLabResultViewModel = updateLabResultViewModel,
                labResult = labResult,
                isLoading = updateUiState.isLoading,
                errorMessage = updateUiState.errorMessage,
                onDismiss = {
                    showEditDialog = false
                    labResultToEdit = null
                    updateLabResultViewModel.onEvent(UpdateLabResultUiEvent.ClearState)
                },
                onSuccess = {
                    viewModel.onEvent(LabsUiEvent.LoadLabResults)
                }
            )
        }
    }
}

@Composable
private fun LabsHeader(
    labResultCount: Int,
    pendingCount: Int,
    onAddLabResultClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
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
                        text = "Exames Laboratoriais",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (labResultCount > 0) {
                            "$labResultCount exame(s) registrado(s)"
                        } else {
                            "Nenhum exame registrado"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }

                if (pendingCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFDC3545)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Pendentes",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$pendingCount",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddLabResultClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
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
                    text = "Adicionar Exame",
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
                    "Buscar por nome ou contato...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFF009688)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar",
                            tint = Color.Gray
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF009688),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
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
            color = Color(0xFF009688)
        )
    }
}

@Composable
private fun EmptyContent(
    onAddLabResultClick: () -> Unit
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
                imageVector = Icons.Default.Biotech,
                contentDescription = "Nenhum exame",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum exame laboratorial registrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Registre o primeiro exame para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = onAddLabResultClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar Exame")
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
                Text("Limpar busca", color = Color(0xFF009688))
            }
        }
    }
}

@Composable
private fun LabResultsListContent(
    labResults: List<LabResult>,
    pendingLabResults: List<LabResult>,
    onEditLabResult: (LabResult) -> Unit,
    onDeleteLabResult: (LabResult) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pendingLabResults.isNotEmpty()) {
            item {
                PendingLabResultsCard(pendingLabResults = pendingLabResults)
            }
        }

        item {
            LabResultStatsRow(
                total = labResults.size,
                pending = pendingLabResults.size
            )
        }

        items(
            items = labResults,
            key = { labResult: LabResult -> labResult.id }
        ) { labResult: LabResult ->
            LabResultCard(
                labResult = labResult,
                onEdit = { onEditLabResult(labResult) },
                onDelete = { onDeleteLabResult(labResult) }
            )
        }
    }
}

@Composable
private fun LabResultErrorSnackbar(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFDC3545) else Color(0xFF28A745)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingLabResultsCard(pendingLabResults: List<LabResult>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3CD),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Exames Laboratoriais Pendentes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF856404)
                )
            }

            pendingLabResults.forEach { labResult: LabResult ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${labResult.petId} - ${labResult.labType}",
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                    Text(
                        text = labResult.labDate,
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabResultStatsRow(total: Int, pending: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Total",
            value = total.toString(),
            iconTint = Color(0xFF2196F3),
            icon = {
                Icon(
                    imageVector = Icons.Default.Biotech,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        StatCard(
            modifier = Modifier.weight(1f),
            label = "Pendentes",
            value = pending.toString(),
            iconTint = Color(0xFFFF9800),
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    iconTint: Color,
    icon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        iconTint.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    icon()
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
            }
        }
    }
}

@Composable
private fun LabResultInfoRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF757575)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F1F1F)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

@Composable
private fun LabResultCard(
    labResult: LabResult,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2196F3).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = labResult.petId,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }

                val statusColor = when (labResult.status) {
                    "COMPLETED" -> Color(0xFF4CAF50)
                    "IN_PROGRESS" -> Color(0xFFFF9800)
                    "CANCELLED" -> Color(0xFFDC3545)
                    else -> Color(0xFF757575)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = when (labResult.status) {
                            "PENDING" -> "Pendente"
                            "IN_PROGRESS" -> "Em Andamento"
                            "COMPLETED" -> "Concluído"
                            "CANCELLED" -> "Cancelado"
                            else -> labResult.status
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 13.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = labResult.labType,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LabResultInfoRow(
                label = "Data do Exame:",
                value = labResult.labDate
            )

            if (labResult.results != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Resultados:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F1F1F)
                )
                Text(
                    text = labResult.results,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    lineHeight = 20.sp
                )
            }

            if (labResult.notes != null && labResult.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Observações:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F1F1F)
                )
                Text(
                    text = labResult.notes,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF666666)
                    )
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC3545)
                    )
                ) {
                    Text(
                        text = "Excluir",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
