package edu.fatec.petwise.features.prescriptions.presentation

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
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.PrescriptionsViewModel
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.PrescriptionsUiEvent
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.AddPrescriptionViewModel
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.AddPrescriptionUiEvent
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.UpdatePrescriptionViewModel
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.UpdatePrescriptionUiEvent
import edu.fatec.petwise.features.prescriptions.di.PrescriptionDependencyContainer
import edu.fatec.petwise.features.prescriptions.presentation.components.AddPrescriptionDialog
import edu.fatec.petwise.features.prescriptions.presentation.components.DeletePrescriptionConfirmationDialog
import edu.fatec.petwise.features.prescriptions.presentation.components.EditPrescriptionDialog
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen() {
    val viewModel = remember { PrescriptionDependencyContainer.prescriptionsViewModel }
    val addPrescriptionViewModel = remember { PrescriptionDependencyContainer.addPrescriptionViewModel }
    val updatePrescriptionViewModel = remember { PrescriptionDependencyContainer.updatePrescriptionViewModel }
    val uiState by viewModel.uiState.collectAsState()
    val addUiState by addPrescriptionViewModel.uiState.collectAsState()
    val updateUiState by updatePrescriptionViewModel.uiState.collectAsState()

    val theme = PetWiseTheme.Light

    val filteredPrescriptions = uiState.filteredPrescriptions

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showAddPrescriptionDialog by remember { mutableStateOf(false) }
    var showEditPrescriptionDialog by remember { mutableStateOf(false) }
    var prescriptionToEdit by remember { mutableStateOf<Prescription?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var prescriptionToDelete by remember { mutableStateOf<Prescription?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(PrescriptionsUiEvent.LoadPrescriptions)
    }

    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            showAddPrescriptionDialog = false
            addPrescriptionViewModel.onEvent(AddPrescriptionUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateUiState.isSuccess) {
        if (updateUiState.isSuccess) {
            showEditPrescriptionDialog = false
            prescriptionToEdit = null
            updatePrescriptionViewModel.onEvent(UpdatePrescriptionUiEvent.ClearState)
        }
    }

    // Update search when query changes
    LaunchedEffect(searchQuery) {
        viewModel.onEvent(PrescriptionsUiEvent.SearchPrescriptions(searchQuery))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        PrescriptionsHeader(
            prescriptionCount = filteredPrescriptions.size,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onAddPrescriptionClick = { showAddPrescriptionDialog = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                filteredPrescriptions.isEmpty() && searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddPrescriptionClick = { showAddPrescriptionDialog = true }
                    )
                }
                filteredPrescriptions.isEmpty() && searchQuery.isNotEmpty() -> {
                    NoResultsContent(
                        onClearSearch = { searchQuery = "" }
                    )
                }
                else -> {
                    PrescriptionsList(
                        prescriptions = filteredPrescriptions,
                        petNames = uiState.petNames,
                        veterinaryName = uiState.veterinaryName,
                        onPrescriptionClick = { /* No action needed for now */ },
                        onEditClick = { prescription ->
                            prescriptionToEdit = prescription
                            showEditPrescriptionDialog = true
                        },
                        onDeleteClick = { prescription ->
                            prescriptionToDelete = prescription
                            showDeleteConfirmation = true
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
                    PrescriptionErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { viewModel.onEvent(PrescriptionsUiEvent.ClearError) },
                        actionLabel = "Tentar Novamente",
                        onAction = { viewModel.onEvent(PrescriptionsUiEvent.LoadPrescriptions) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddPrescriptionDialog) {
        AddPrescriptionDialog(
            isLoading = addUiState.isLoading,
            errorMessage = addUiState.errorMessage,
            onDismiss = {
                showAddPrescriptionDialog = false
                addPrescriptionViewModel.onEvent(AddPrescriptionUiEvent.ClearState)
            },
            onSuccess = { formData ->
                addPrescriptionViewModel.onEvent(AddPrescriptionUiEvent.AddPrescription(formData))
            }
        )
    }

    prescriptionToDelete?.let { prescription ->
        if (showDeleteConfirmation) {
            DeletePrescriptionConfirmationDialog(
                prescriptionId = prescription.id ?: "",
                prescriptionName = prescription.medications,
                onSuccess = {
                    showDeleteConfirmation = false
                    prescriptionToDelete = null
                },
                onCancel = {
                    showDeleteConfirmation = false
                    prescriptionToDelete = null
                }
            )
        }
    }

    // Edit Dialog
    prescriptionToEdit?.let { prescription ->
        if (showEditPrescriptionDialog) {
            EditPrescriptionDialog(
                prescription = prescription,
                isLoading = updateUiState.isLoading,
                errorMessage = updateUiState.errorMessage,
                onDismiss = {
                    showEditPrescriptionDialog = false
                    prescriptionToEdit = null
                    updatePrescriptionViewModel.onEvent(UpdatePrescriptionUiEvent.ClearState)
                },
                onSuccess = { formData ->
                    updatePrescriptionViewModel.onEvent(UpdatePrescriptionUiEvent.UpdatePrescription(prescription.id ?: "", formData))
                }
            )
        }
    }
}

@Composable
private fun PrescriptionsHeader(
    prescriptionCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddPrescriptionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.fromHex("#673AB7")
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
                        text = "Prescrições",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (prescriptionCount > 0) "$prescriptionCount prescrições registradas" else "Nenhuma prescrição cadastrada",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddPrescriptionClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.fromHex("#673AB7")
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
                    text = "Adicionar Prescrição",
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
                    "Buscar por medicamento...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.fromHex("#673AB7")
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
                focusedBorderColor = Color.fromHex("#673AB7"),
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
            color = Color.fromHex("#673AB7")
        )
    }
}

@Composable
private fun EmptyContent(
    onAddPrescriptionClick: () -> Unit
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
                imageVector = Icons.Default.Description,
                contentDescription = "Nenhuma prescrição",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhuma prescrição cadastrada",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione sua primeira prescrição para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddPrescriptionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#673AB7")
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Prescrição")
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
                Text("Limpar busca", color = Color.fromHex("#673AB7"))
            }
        }
    }
}

@Composable
private fun PrescriptionsList(
    prescriptions: List<Prescription>,
    petNames: Map<String, String>,
    veterinaryName: String,
    onPrescriptionClick: (Prescription) -> Unit,
    onEditClick: (Prescription) -> Unit,
    onDeleteClick: (Prescription) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(prescriptions, key = { it.id ?: "" }) { prescription ->
            PrescriptionCard(
                prescription = prescription,
                petName = petNames[prescription.petId] ?: "Pet não encontrado",
                veterinaryName = veterinaryName.ifEmpty { "Veterinário" },
                onClick = onPrescriptionClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    petName: String = "",
    veterinaryName: String = "",
    onClick: (Prescription) -> Unit = {},
    onEditClick: (Prescription) -> Unit = {},
    onDeleteClick: (Prescription) -> Unit = {}
) {
    val theme = PetWiseTheme.Light
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Format date (prescriptionDate is a String like "2024-01-15T10:30:00")
    val formattedDate = remember(prescription.prescriptionDate) {
        try {
            val dateStr = prescription.prescriptionDate.substringBefore("T")
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                prescription.prescriptionDate
            }
        } catch (e: Exception) {
            prescription.prescriptionDate
        }
    }

    // Format validUntil date
    val formattedValidUntil = remember(prescription.validUntil) {
        prescription.validUntil?.let { validUntil ->
            try {
                val dateStr = validUntil.substringBefore("T")
                val parts = dateStr.split("-")
                if (parts.size == 3) {
                    "${parts[2]}/${parts[1]}/${parts[0]}"
                } else {
                    validUntil
                }
            } catch (e: Exception) {
                validUntil
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(prescription) }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) Color.White.copy(alpha = 0.9f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prescription.medications,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Pet name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color.fromHex("#673AB7"),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = petName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex("#333333"),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Veterinary name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.fromHex("#2196F3"),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dr(a). $veterinaryName",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex("#666666")
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Prescription date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Data: $formattedDate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
                
                // Valid until date
                formattedValidUntil?.let { validUntil ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.fromHex("#FF9800"),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Válido até: $validUntil",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.fromHex("#FF9800")
                            )
                        )
                    }
                }
                
                // Instructions if available
                if (prescription.instructions.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.fromHex("#4CAF50"),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = prescription.instructions,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.fromHex("#666666")
                            ),
                            maxLines = 2
                        )
                    }
                }
                
                // Diagnosis if available
                prescription.diagnosis?.let { diagnosis ->
                    if (diagnosis.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.fromHex("#F44336"),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Diagnóstico: $diagnosis",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.fromHex("#666666")
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.fromHex("#673AB7").copy(alpha = 0.1f),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = formattedDate,
                            color = Color.fromHex("#673AB7"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (prescription.status.uppercase()) {
                            "ACTIVE", "ATIVA" -> Color.fromHex("#4CAF50").copy(alpha = 0.1f)
                            "PENDING", "PENDENTE" -> Color.fromHex("#FF9800").copy(alpha = 0.1f)
                            "EXPIRED", "EXPIRADA" -> Color.fromHex("#F44336").copy(alpha = 0.1f)
                            else -> Color.fromHex("#9E9E9E").copy(alpha = 0.1f)
                        },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = when (prescription.status.uppercase()) {
                                "ACTIVE", "ATIVA" -> "Ativa"
                                "PENDING", "PENDENTE" -> "Pendente"
                                "EXPIRED", "EXPIRADA" -> "Expirada"
                                else -> prescription.status
                            },
                            color = when (prescription.status.uppercase()) {
                                "ACTIVE", "ATIVA" -> Color.fromHex("#4CAF50")
                                "PENDING", "PENDENTE" -> Color.fromHex("#FF9800")
                                "EXPIRED", "EXPIRADA" -> Color.fromHex("#F44336")
                                else -> Color.fromHex("#9E9E9E")
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row {
                IconButton(
                    onClick = { onEditClick(prescription) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.fromHex("#673AB7"),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(prescription) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color.fromHex("#F44336"),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrescriptionErrorSnackbar(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    LaunchedEffect(message) {
        delay(5000) // Auto dismiss after 5 seconds
        onDismiss()
    }

    Snackbar(
        modifier = modifier,
        action = actionLabel?.let { label ->
            {
                TextButton(
                    onClick = { onAction?.invoke() }
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissAction = {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        containerColor = if (isError) Color.fromHex("#F44336") else Color.fromHex("#4CAF50"),
        contentColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.Error else Icons.Default.Check,
                contentDescription = if (isError) "Erro" else "Sucesso",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
