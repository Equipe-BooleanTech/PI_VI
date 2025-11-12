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
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen() {
    var prescriptions by remember { mutableStateOf<List<Prescription>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedPrescriptionIds by remember { mutableStateOf(setOf<String>()) }

    val theme = PetWiseTheme.Light

    val filteredPrescriptions = remember(prescriptions, searchQuery) {
        if (searchQuery.isEmpty()) {
            prescriptions
        } else {
            prescriptions.filter {
                it.medicationName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        PrescriptionsHeader(
            prescriptionCount = filteredPrescriptions.size,
            selectionMode = selectionMode,
            selectedCount = selectedPrescriptionIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddPrescriptionClick = { /* TODO: Implement add */ },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedPrescriptionIds = setOf()
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
                filteredPrescriptions.isEmpty() && searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddPrescriptionClick = { /* TODO: Implement add */ }
                    )
                }
                filteredPrescriptions.isEmpty() && searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { searchQuery = "" })
                }
                else -> {
                    PrescriptionsList(
                        prescriptions = filteredPrescriptions,
                        selectionMode = selectionMode,
                        selectedIds = selectedPrescriptionIds,
                        onPrescriptionClick = { prescription ->
                            if (selectionMode) {
                                selectedPrescriptionIds = if (selectedPrescriptionIds.contains(prescription.id)) {
                                    selectedPrescriptionIds - prescription.id
                                } else {
                                    selectedPrescriptionIds + prescription.id
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
private fun PrescriptionsHeader(
    prescriptionCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddPrescriptionClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode) Color.fromHex("#d32f2f") else Color.fromHex("#673AB7")
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
                        text = if (selectionMode) "Selecionados" else "Prescrições",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount prescrição(ões) selecionada(s)"
                        } else {
                            if (prescriptionCount > 0) "$prescriptionCount prescrições registradas" else "Nenhuma prescrição cadastrada"
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
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onPrescriptionClick: (Prescription) -> Unit,
    onEditClick: (Prescription) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(prescriptions, key = { it.id }) { prescription ->
            PrescriptionCard(
                prescription = prescription,
                selectionMode = selectionMode,
                isSelected = selectedIds.contains(prescription.id),
                onClick = onPrescriptionClick,
                onEditClick = onEditClick
            )
        }
    }
}

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: (Prescription) -> Unit = {},
    onEditClick: (Prescription) -> Unit = {}
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
            ) { onClick(prescription) }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color.fromHex("#673AB7").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color.fromHex("#673AB7"))
        } else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick(prescription) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.fromHex("#673AB7")
                    ),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prescription.medicationName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pet ID: ${prescription.petId}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                Text(
                    text = "Veterinário ID: ${prescription.veterinaryId}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.fromHex("#673AB7").copy(alpha = 0.1f),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "Dosagem: ${prescription.dosage} - ${prescription.frequency}",
                        color = Color.fromHex("#673AB7"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!selectionMode) {
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
            }
        }
    }
}
