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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.presentation.components.AddExamDialog
import edu.fatec.petwise.features.exams.presentation.components.EditExamDialog
import edu.fatec.petwise.features.exams.presentation.components.DeleteExamConfirmationDialog
import edu.fatec.petwise.features.exams.presentation.viewmodel.AddExamViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.AddExamUiEvent
import edu.fatec.petwise.features.exams.presentation.viewmodel.ExamsViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.ExamsUiEvent
import edu.fatec.petwise.features.exams.presentation.viewmodel.UpdateExamViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.UpdateExamUiEvent
import edu.fatec.petwise.features.exams.di.ExamDependencyContainer
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    viewModel: ExamsViewModel,
    navigationKey: Any? = null
) {
    val addExamViewModel = remember { ExamDependencyContainer.addExamViewModel }
    val updateExamViewModel = remember { ExamDependencyContainer.updateExamViewModel }
    val uiState by viewModel.uiState.collectAsState()
    val addUiState by addExamViewModel.uiState.collectAsState()
    val updateUiState by updateExamViewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<edu.fatec.petwise.features.exams.domain.models.Exam?>(null) }
    var examToDelete by remember { mutableStateOf<edu.fatec.petwise.features.exams.domain.models.Exam?>(null) }


    val exams = uiState.filteredExams

    LaunchedEffect(navigationKey) {
        viewModel.onEvent(ExamsUiEvent.LoadExams)
    }

    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            showAddDialog = false
            viewModel.onEvent(ExamsUiEvent.LoadExams)
            addExamViewModel.onEvent(AddExamUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateUiState.isSuccess) {
        if (updateUiState.isSuccess) {
            showEditDialog = false
            examToEdit = null
            viewModel.onEvent(ExamsUiEvent.LoadExams)
            updateExamViewModel.onEvent(UpdateExamUiEvent.ClearState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        ExamsHeader(
            examCount = exams.size,
            onAddExamClick = { showAddDialog = true },
            onSearchClick = { /* TODO: Implement search */ },
            onFilterClick = { showFilterDialog = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                exams.isEmpty() -> {
                    EmptyContent(
                        onAddExamClick = { showAddDialog = true }
                    )
                }
                else -> {
                    ExamsListContent(
                        exams = exams,
                        onEditExam = { exam ->
                            examToEdit = exam
                            showEditDialog = true
                        },
                        onDeleteExam = { exam ->
                            examToDelete = exam
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
                    ExamErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { viewModel.onEvent(ExamsUiEvent.ClearError) },
                        actionLabel = "Tentar Novamente",
                        onAction = { viewModel.onEvent(ExamsUiEvent.LoadExams) }
                    )
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddExamDialog(
            addExamViewModel = addExamViewModel,
            isLoading = addUiState.isLoading,
            errorMessage = addUiState.errorMessage,
            onDismiss = {
                showAddDialog = false
                addExamViewModel.onEvent(AddExamUiEvent.ClearState)
            },
            onSuccess = {
                viewModel.onEvent(ExamsUiEvent.LoadExams)
            }
        )
    }

    // Delete Dialog
    examToDelete?.let { exam ->
        if (showDeleteDialog) {
            DeleteExamConfirmationDialog(
                examId = exam.id,
                examName = exam.examType,
                onDelete = { examId ->
                    viewModel.onEvent(ExamsUiEvent.DeleteExam(examId))
                    showDeleteDialog = false
                    examToDelete = null
                },
                onCancel = {
                    showDeleteDialog = false
                    examToDelete = null
                }
            )
        }
    }

    // Edit Dialog
    examToEdit?.let { exam ->
        if (showEditDialog) {
            EditExamDialog(
                updateExamViewModel = updateExamViewModel,
                exam = exam,
                isLoading = updateUiState.isLoading,
                errorMessage = updateUiState.errorMessage,
                onDismiss = {
                    showEditDialog = false
                    examToEdit = null
                    updateExamViewModel.onEvent(UpdateExamUiEvent.ClearState)
                },
                onSuccess = {
                    viewModel.onEvent(ExamsUiEvent.LoadExams)
                }
            )
        }
    }
}

@Composable
private fun ExamsHeader(
    examCount: Int,
    onAddExamClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.fromHex("#2196F3")
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
                        text = "Exames",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (examCount > 0) "$examCount exames registrados" else "Nenhum exame cadastrado",
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
                    "Buscar por tipo de exame ou status...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
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
                            tint = Color.Gray
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.fromHex("#2196F3"),
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
private fun ExamsListContent(
    exams: List<Exam>,
    onEditExam: (Exam) -> Unit,
    onDeleteExam: (Exam) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(exams, key = { it.id }) { exam ->
            ExamCard(
                exam = exam,
                onClick = { /* Could be used for details view */ },
                onEditClick = onEditExam,
                onDeleteClick = onDeleteExam
            )
        }
    }
}

@Composable
private fun ExamErrorSnackbar(
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
fun ExamCard(
    exam: Exam,
    onClick: (Exam) -> Unit = {},
    onEditClick: (Exam) -> Unit = {},
    onDeleteClick: (Exam) -> Unit = {}
) {
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
                    text = exam.examType,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data: ${exam.examDate.date} às ${exam.examTime}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
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
                            text = exam.examDate.date.toString(),
                            color = Color.fromHex("#2196F3"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (exam.status) {
                            "COMPLETED" -> Color.fromHex("#4CAF50").copy(alpha = 0.1f)
                            "PENDING" -> Color.fromHex("#FF9800").copy(alpha = 0.1f)
                            "IN_PROGRESS" -> Color.fromHex("#2196F3").copy(alpha = 0.1f)
                            "CANCELLED" -> Color.fromHex("#F44336").copy(alpha = 0.1f)
                            else -> Color.fromHex("#9E9E9E").copy(alpha = 0.1f)
                        },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = when (exam.status) {
                                "COMPLETED" -> "Concluído"
                                "PENDING" -> "Pendente"
                                "IN_PROGRESS" -> "Em Andamento"
                                "CANCELLED" -> "Cancelado"
                                else -> exam.status
                            },
                            color = when (exam.status) {
                                "COMPLETED" -> Color.fromHex("#4CAF50")
                                "PENDING" -> Color.fromHex("#FF9800")
                                "IN_PROGRESS" -> Color.fromHex("#2196F3")
                                "CANCELLED" -> Color.fromHex("#F44336")
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

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

                IconButton(
                    onClick = { onDeleteClick(exam) },
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

