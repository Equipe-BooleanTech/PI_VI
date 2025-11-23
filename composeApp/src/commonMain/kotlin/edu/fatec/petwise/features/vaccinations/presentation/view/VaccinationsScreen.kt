package edu.fatec.petwise.features.vaccinations.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.vaccinations.presentation.components.AddVaccinationDialog
import edu.fatec.petwise.features.vaccinations.presentation.components.EditVaccinationDialog
import edu.fatec.petwise.features.vaccinations.presentation.components.DeleteVaccinationConfirmationDialog
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.VaccinationsViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.VaccinationsUiEvent
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.UpdateVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.UpdateVaccinationUiEvent
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationUiEvent
import edu.fatec.petwise.features.vaccinations.di.VaccinationDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

fun formatDateTime(dateTimeString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateTimeString)
        val format = LocalDateTime.Format {
            dayOfMonth()
            char('/')
            monthNumber()
            char('/')
            year()
            chars(" às ")
            hour()
            char(':')
            minute()
        }
        dateTime.format(format)
    } catch (e: Exception) {
        dateTimeString // fallback
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationsScreen(
    viewModel: VaccinationsViewModel,
    navigationKey: Any? = null
) {
    val addVaccinationViewModel = remember { VaccinationDependencyContainer.provideAddVaccinationViewModel() }
    val updateVaccinationViewModel = remember { VaccinationDependencyContainer.provideUpdateVaccinationViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val addUiState by addVaccinationViewModel.uiState.collectAsState()
    val updateUiState by updateVaccinationViewModel.uiState.collectAsState()
    val vaccinations = uiState.vaccinations
    val pets = uiState.pets
    val pendingVaccinations = remember(vaccinations) {
        vaccinations.filter {
            it.status == VaccinationStatus.ATRASADA || it.status == VaccinationStatus.AGENDADA
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var vaccinationToEdit by remember { mutableStateOf<Vaccination?>(null) }
    var vaccinationToDelete by remember { mutableStateOf<Vaccination?>(null) }
    
    LaunchedEffect(navigationKey) {
        viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
    }

    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            showAddDialog = false
            viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
            addVaccinationViewModel.onEvent(AddVaccinationUiEvent.ClearState)
        }
    }

    LaunchedEffect(updateUiState.isSuccess) {
        if (updateUiState.isSuccess) {
            showEditDialog = false
            vaccinationToEdit = null
            viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
            updateVaccinationViewModel.onEvent(UpdateVaccinationUiEvent.ClearState)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        VaccinationsHeader(
            vaccinationCount = vaccinations.size,
            pendingCount = pendingVaccinations.size,
            onAddVaccinationClick = { showAddDialog = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                vaccinations.isEmpty() -> {
                    EmptyContent(
                        onAddVaccinationClick = { showAddDialog = true }
                    )
                }
                else -> {
                    VaccinationsListContent(
                        vaccinations = vaccinations,
                        pendingVaccinations = pendingVaccinations,
                        pets = pets,
                        onEditVaccination = { vaccination ->
                            vaccinationToEdit = vaccination
                            showEditDialog = true
                        },
                        onDeleteVaccination = { vaccination ->
                            vaccinationToDelete = vaccination
                            showDeleteDialog = true
                        },
                        onScheduleVaccination = { /* TODO: Implement scheduling */ }
                    )
                }
            }

            uiState.error?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    VaccinationErrorSnackbar(
                        message = errorMessage,
                        isError = true,
                        onDismiss = { /* Clear error event */ },
                        actionLabel = "Tentar Novamente",
                        onAction = { viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations) }
                    )
                }
            }
        }
    }
    
    // Add Dialog
    if (showAddDialog) {
        AddVaccinationDialog(
            addVaccinationViewModel = addVaccinationViewModel,
            isLoading = addUiState.isLoading,
            errorMessage = addUiState.errorMessage,
            onDismiss = {
                showAddDialog = false
                addVaccinationViewModel.onEvent(AddVaccinationUiEvent.ClearState)
            },
            onSuccess = {
                viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
            }
        )
    }

    // Delete Dialog
    vaccinationToDelete?.let { vaccination ->
        if (showDeleteDialog) {
            DeleteVaccinationConfirmationDialog(
                vaccinationId = vaccination.id,
                vaccinationName = vaccination.vaccineType.getDisplayName(),
                onSuccess = {
                    showDeleteDialog = false
                    vaccinationToDelete = null
                    viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
                },
                onCancel = {
                    showDeleteDialog = false
                    vaccinationToDelete = null
                }
            )
        }
    }

    // Edit Dialog
    vaccinationToEdit?.let { vaccination ->
        if (showEditDialog) {
            EditVaccinationDialog(
                updateVaccinationViewModel = updateVaccinationViewModel,
                vaccination = vaccination,
                isLoading = updateUiState.isLoading,
                errorMessage = updateUiState.errorMessage,
                onDismiss = {
                    showEditDialog = false
                    vaccinationToEdit = null
                    updateVaccinationViewModel.onEvent(UpdateVaccinationUiEvent.ClearState)
                },
                onSuccess = {
                    viewModel.onEvent(VaccinationsUiEvent.LoadVaccinations)
                }
            )
        }
    }
}

@Composable
private fun VaccinationsHeader(
    vaccinationCount: Int,
    pendingCount: Int,
    onAddVaccinationClick: () -> Unit
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
                        text = "Vacinas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (vaccinationCount > 0) {
                            "$vaccinationCount vacina(s) registrada(s)"
                        } else {
                            "Nenhuma vacina registrada"
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
                    onClick = onAddVaccinationClick,
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
                        text = "Adicionar Vacina",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun EmptyContent(
    onAddVaccinationClick: () -> Unit
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
                imageVector = Icons.Default.Vaccines,
                contentDescription = "Nenhuma vacina",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhuma vacina registrada",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Registre a primeira vacina para começar!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = onAddVaccinationClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar Vacina")
                }
        }
    }
}

@Composable
private fun VaccinationsListContent(
    vaccinations: List<Vaccination>,
    pendingVaccinations: List<Vaccination>,
    pets: List<Pet>,
    onEditVaccination: (Vaccination) -> Unit,
    onDeleteVaccination: (Vaccination) -> Unit,
    onScheduleVaccination: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pendingVaccinations.isNotEmpty()) {
            item {
                PendingVaccinationsCard(pendingVaccinations = pendingVaccinations, pets = pets)
            }
        }
        
        item {
            VaccinationStatsRow(
                total = vaccinations.size,
                upcoming = pendingVaccinations.size
            )
        }
        
        items(
            items = vaccinations,
            key = { vaccination: Vaccination -> vaccination.id }
        ) { vaccination: Vaccination ->
            VaccinationCard(
                vaccination = vaccination,
                pets = pets,
                onEdit = { onEditVaccination(vaccination) },
                onDelete = { onDeleteVaccination(vaccination) },
                onSchedule = { onScheduleVaccination(vaccination.id) }
            )
        }
    }
}

@Composable
private fun VaccinationErrorSnackbar(
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
private fun PendingVaccinationsCard(pendingVaccinations: List<Vaccination>, pets: List<Pet>) {
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
                    text = "Reforços de Vacina Pendentes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF856404)
                )
            }
            
            pendingVaccinations.forEach { vaccination: Vaccination ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${pets.find { it.id == vaccination.petId }?.name ?: vaccination.petId} - ${vaccination.vaccineType.getDisplayName()}",
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                    Text(
                        text = vaccination.nextDoseDate?.let { formatDateTime(it) } ?: "",
                        fontSize = 14.sp,
                        color = Color(0xFF856404)
                    )
                }
            }
        }
    }
}

@Composable
private fun VaccinationStatsRow(total: Int, upcoming: Int) {
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
                    imageVector = Icons.Default.Vaccines,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Próximos",
            value = upcoming.toString(),
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
private fun VaccinationInfoRow(
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
private fun VaccinationCard(
    vaccination: Vaccination,
    pets: List<Pet>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSchedule: () -> Unit
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
                        text = pets.find { it.id == vaccination.petId }?.name ?: vaccination.petId,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (vaccination.status == VaccinationStatus.ATRASADA) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDC3545).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = vaccination.status.getDisplayName(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 13.sp,
                            color = Color(0xFFDC3545),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                if (vaccination.status == VaccinationStatus.APLICADA) {
                    Text(
                        text = formatDateTime(vaccination.vaccinationDate),
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = vaccination.vaccineType.getDisplayName(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            VaccinationInfoRow(
                label = "Data de Aplicação:",
                value = formatDateTime(vaccination.vaccinationDate)
            )
            
            if (vaccination.nextDoseDate != null) {
                VaccinationInfoRow(
                    label = "Próximo Reforço:",
                    value = formatDateTime(vaccination.nextDoseDate),
                    valueColor = if (vaccination.status == VaccinationStatus.ATRASADA) 
                        Color(0xFFDC3545) else Color(0xFF757575)
                )
            }
            
            VaccinationInfoRow(
                label = "Doses Totais:",
                value = "${vaccination.totalDoses}"
            )
            
            if (vaccination.manufacturer != null) {
                VaccinationInfoRow(
                    label = "Fabricante:",
                    value = vaccination.manufacturer
                )
            }
            
            if (vaccination.observations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Observações:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F1F1F)
                )
                Text(
                    text = vaccination.observations,
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

