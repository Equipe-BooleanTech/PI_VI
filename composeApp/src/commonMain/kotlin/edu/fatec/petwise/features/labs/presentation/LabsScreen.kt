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
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.presentation.components.AddLabDialog
import edu.fatec.petwise.features.labs.presentation.components.EditLabDialog
import edu.fatec.petwise.features.labs.presentation.components.DeleteLabConfirmationDialog
import edu.fatec.petwise.features.labs.presentation.viewmodel.AddLabViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.AddLabUiEvent
import edu.fatec.petwise.features.labs.presentation.viewmodel.UpdateLabViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.UpdateLabUiEvent
import edu.fatec.petwise.features.labs.presentation.viewmodel.LabsViewModel
import edu.fatec.petwise.features.labs.presentation.viewmodel.LabsUiEvent
import edu.fatec.petwise.features.labs.di.LabDependencyContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(
    viewModel: LabsViewModel,
    navigationKey: Any? = null
) {
    val addLabViewModel = remember { LabDependencyContainer.addLabViewModel }
    val updateLabViewModel = remember { LabDependencyContainer.updateLabViewModel }
    val uiState by viewModel.uiState.collectAsState()
    val addUiState by addLabViewModel.uiState.collectAsState()
    val updateUiState by updateLabViewModel.uiState.collectAsState()
    val labs = uiState.labs
    val pendingLabs = remember(labs) {
        emptyList<Lab>() 
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var labToEdit by remember { mutableStateOf<Lab?>(null) }
    var labToDelete by remember { mutableStateOf<Lab?>(null) }

    LaunchedEffect(navigationKey) {
        viewModel.onEvent(LabsUiEvent.LoadLabs)
    }

    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            showAddDialog = false
            viewModel.onEvent(LabsUiEvent.LoadLabs)
            addLabViewModel.onEvent(AddLabUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateUiState.isSuccess) {
        if (updateUiState.isSuccess) {
            showEditDialog = false
            labToEdit = null
            viewModel.onEvent(LabsUiEvent.LoadLabs)
            updateLabViewModel.onEvent(UpdateLabUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        LabsHeader(
            labCount = labs.size,
            pendingCount = pendingLabs.size,
            onAddLabClick = { showAddDialog = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                labs.isEmpty() -> {
                    EmptyContent(
                        onAddLabClick = { showAddDialog = true }
                    )
                }
                else -> {
                    LabsListContent(
                        labs = labs,
                        pendingLabs = pendingLabs,
                        onEditLab = { lab ->
                            labToEdit = lab
                            showEditDialog = true
                        },
                        onDeleteLab = { lab ->
                            labToDelete = lab
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
                        onDismiss = {  },
                        actionLabel = "Tentar Novamente",
                        onAction = { viewModel.onEvent(LabsUiEvent.LoadLabs) }
                    )
                }
            }
        }
    }

    
    if (showAddDialog) {
        AddLabDialog(
            addLabViewModel = addLabViewModel,
            isLoading = addUiState.isLoading,
            errorMessage = addUiState.errorMessage,
            onDismiss = {
                showAddDialog = false
                addLabViewModel.onEvent(AddLabUiEvent.ClearState)
            },
            onSuccess = {
                viewModel.onEvent(LabsUiEvent.LoadLabs)
            }
        )
    }

    
    labToDelete?.let { lab ->
        if (showDeleteDialog) {
            DeleteLabConfirmationDialog(
                labId = lab.id,
                labName = lab.name,
                onSuccess = {
                    showDeleteDialog = false
                    labToDelete = null
                    viewModel.onEvent(LabsUiEvent.LoadLabs)
                },
                onCancel = {
                    showDeleteDialog = false
                    labToDelete = null
                }
            )
        }
    }

    
    labToEdit?.let { lab ->
        if (showEditDialog) {
            EditLabDialog(
                updateLabViewModel = updateLabViewModel,
                lab = lab,
                isLoading = updateUiState.isLoading,
                errorMessage = updateUiState.errorMessage,
                onDismiss = {
                    showEditDialog = false
                    labToEdit = null
                    updateLabViewModel.onEvent(UpdateLabUiEvent.ClearState)
                },
                onSuccess = {
                    viewModel.onEvent(LabsUiEvent.LoadLabs)
                }
            )
        }
    }
}

@Composable
private fun LabsHeader(
    labCount: Int,
    pendingCount: Int,
    onAddLabClick: () -> Unit
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
                        text = "Laboratórios",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (labCount > 0) {
                            "$labCount laboratório(s) registrado(s)"
                        } else {
                            "Nenhum laboratório registrado"
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
                onClick = onAddLabClick,
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
                    text = "Adicionar Laboratório",
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
    onAddLabClick: () -> Unit
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
                text = "Nenhum laboratório registrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Registre o primeiro laboratório para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = onAddLabClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar Laboratório")
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
private fun LabsListContent(
    labs: List<Lab>,
    pendingLabs: List<Lab>,
    onEditLab: (Lab) -> Unit,
    onDeleteLab: (Lab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pendingLabs.isNotEmpty()) {
            item {
                PendingLabsCard(pendingLabs = pendingLabs)
            }
        }

        item {
            LabStatsRow(
                total = labs.size
            )
        }

        items(
            items = labs,
            key = { lab: Lab -> lab.id }
        ) { lab: Lab ->
            LabCard(
                lab = lab,
                onEdit = { onEditLab(lab) },
                onDelete = { onDeleteLab(lab) }
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
private fun PendingLabsCard(pendingLabs: List<Lab>) {
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

            pendingLabs.forEach { lab: Lab ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = lab.name,
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                    Text(
                        text = lab.createdAt,
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabStatsRow(total: Int) {
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
private fun LabInfoRow(
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
private fun LabCard(
    lab: Lab,
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
                        text = lab.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lab.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (lab.contactInfo != null) {
                LabInfoRow(
                    label = "Contato:",
                    value = lab.contactInfo
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LabInfoRow(
                label = "Criado em:",
                value = lab.createdAt
            )

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
