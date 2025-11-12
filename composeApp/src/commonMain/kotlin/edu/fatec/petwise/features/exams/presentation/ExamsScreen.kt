package edu.fatec.petwise.features.exams.presentation

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
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen() {
    var exams by remember { mutableStateOf<List<Exam>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedExamIds by remember { mutableStateOf(setOf<String>()) }

    val theme = PetWiseTheme.Light

    val filteredExams = remember(exams, searchQuery) {
        if (searchQuery.isEmpty()) {
            exams
        } else {
            exams.filter {
                it.examType.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        ExamsHeader(
            examCount = filteredExams.size,
            selectionMode = selectionMode,
            selectedCount = selectedExamIds.size,
            onSearchClick = { showSearchBar = !showSearchBar },
            onFilterClick = { /* TODO: Implement filter */ },
            onAddExamClick = { /* TODO: Implement add */ },
            onSelectionModeToggle = {
                selectionMode = !selectionMode
                if (!selectionMode) selectedExamIds = setOf()
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
                filteredExams.isEmpty() && searchQuery.isEmpty() -> {
                    EmptyContent(
                        onAddExamClick = { /* TODO: Implement add */ }
                    )
                }
                filteredExams.isEmpty() && searchQuery.isNotEmpty() -> {
                    NoResultsContent(onClearSearch = { searchQuery = "" })
                }
                else -> {
                    ExamsList(
                        exams = filteredExams,
                        selectionMode = selectionMode,
                        selectedIds = selectedExamIds,
                        onExamClick = { exam ->
                            if (selectionMode) {
                                selectedExamIds = if (selectedExamIds.contains(exam.id)) {
                                    selectedExamIds - exam.id
                                } else {
                                    selectedExamIds + exam.id
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
private fun ExamsHeader(
    examCount: Int,
    selectionMode: Boolean,
    selectedCount: Int,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddExamClick: () -> Unit,
    onSelectionModeToggle: () -> Unit,
    onDeleteSelected: () -> Unit
) {
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
                        text = if (selectionMode) "Selecionados" else "Exames",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (selectionMode) {
                            "$selectedCount exame(s) selecionado(s)"
                        } else {
                            if (examCount > 0) "$examCount exames registrados" else "Nenhum exame cadastrado"
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
                    onClick = onAddExamClick,
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
                        text = "Adicionar Exame",
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
                    "Buscar por tipo de exame ou status...",
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
    onAddExamClick: () -> Unit
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
                imageVector = Icons.Default.Science,
                contentDescription = "Nenhum exame",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhum exame cadastrado",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adicione seu primeiro exame para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddExamClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#2196F3")
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
                Text("Limpar busca", color = Color.fromHex("#2196F3"))
            }
        }
    }
}

@Composable
private fun ExamsList(
    exams: List<Exam>,
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onExamClick: (Exam) -> Unit,
    onEditClick: (Exam) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(exams, key = { it.id }) { exam ->
            ExamCard(
                exam = exam,
                selectionMode = selectionMode,
                isSelected = selectedIds.contains(exam.id),
                onClick = onExamClick,
                onEditClick = onEditClick
            )
        }
    }
}

@Composable
fun ExamCard(
    exam: Exam,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: (Exam) -> Unit = {},
    onEditClick: (Exam) -> Unit = {}
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
            ) { onClick(exam) }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color.fromHex("#2196F3").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color.fromHex("#2196F3"))
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
                    onCheckedChange = { onClick(exam) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.fromHex("#2196F3")
                    ),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exam.examType,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pet ID: ${exam.petId}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                Text(
                    text = "Veterinário ID: ${exam.veterinaryId}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.fromHex("#2196F3").copy(alpha = 0.1f),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = exam.examDate,
                            color = Color.fromHex("#2196F3"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.fromHex("#4CAF50").copy(alpha = 0.1f),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = exam.status,
                            color = Color.fromHex("#4CAF50"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!selectionMode) {
                IconButton(
                    onClick = { onEditClick(exam) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.fromHex("#2196F3"),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
