package edu.fatec.petwise.features.pets.presentation.view

import androidx.compose.foundation.background
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
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.presentation.components.PetInfoCard
import edu.fatec.petwise.features.pets.presentation.components.HealthRecordCard
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetDetailsUiEvent
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetDetailsViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetDetailsUiState
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    pet: Pet,
    onBackClick: () -> Unit
) {
    val petDetailsViewModel: PetDetailsViewModel = remember { PetDependencyContainer.providePetDetailsViewModel() }
    val uiState: PetDetailsUiState by petDetailsViewModel.uiState.collectAsState()
    val theme = PetWiseTheme.Light

    LaunchedEffect(pet.id) {
        petDetailsViewModel.onEvent(PetDetailsUiEvent.LoadPetDetails(pet.id))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.fromHex("#00b942"),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.fromHex("#F7F7F7"))
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.fromHex("#00b942"))
                    }
                }
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Erro",
                                tint = Color.fromHex("#d32f2f"),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = uiState.errorMessage ?: "Erro ao carregar dados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.fromHex("#d32f2f")
                            )
                            Button(
                                onClick = { petDetailsViewModel.onEvent(PetDetailsUiEvent.LoadPetDetails(pet.id)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.fromHex("#00b942")
                                )
                            ) {
                                Text("Tentar Novamente")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Pet Information
                        item {
                            PetInfoCard(pet = pet)
                        }

                        // Health Records Section
                        item {
                            Text(
                                text = "Histórico de Saúde",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.fromHex(theme.palette.textPrimary)
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Consultations
                        if (uiState.consultations.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Consultas (${uiState.consultations.size})",
                                    icon = Icons.Default.Event
                                )
                            }
                            items(uiState.consultations.take(3)) { consultation ->
                                HealthRecordCard(
                                    title = consultation.consultaType.displayName,
                                    description = if (consultation.symptoms.isEmpty()) "Consulta veterinária" else consultation.symptoms,
                                    date = "${consultation.consultaDate.dayOfMonth.toString().padStart(2, '0')}/" +
                                           "${consultation.consultaDate.monthNumber.toString().padStart(2, '0')}/" +
                                           "${consultation.consultaDate.year}",
                                    status = consultation.status.displayName,
                                    type = "consulta"
                                )
                            }
                        }

                        // Vaccines
                        if (uiState.vaccinations.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Vacinas (${uiState.vaccinations.size})",
                                    icon = Icons.Default.Vaccines
                                )
                            }
                            items(uiState.vaccinations.take(3)) { vaccination ->
                                HealthRecordCard(
                                    title = vaccination.vaccineType.getDisplayName(),
                                    description = if (vaccination.observations.isEmpty()) "Vacinação realizada" else vaccination.observations,
                                    date = "${vaccination.vaccinationDate.dayOfMonth.toString().padStart(2, '0')}/${vaccination.vaccinationDate.monthNumber.toString().padStart(2, '0')}/${vaccination.vaccinationDate.year}",
                                    status = vaccination.status.getDisplayName(),
                                    type = "vacina"
                                )
                            }
                        }

                        // Medications
                        if (uiState.medications.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Medicações (${uiState.medications.size})",
                                    icon = Icons.Default.Medication
                                )
                            }
                            items(uiState.medications.take(3)) { medication ->
                                HealthRecordCard(
                                    title = medication.medicationName,
                                    description = medication.dosage,
                                    date = medication.startDate.toString().substringBefore('T'),
                                    status = medication.status.displayName,
                                    type = "medicacao"
                                )
                            }
                        }

                        // Exams
                        if (uiState.exams.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Exames (${uiState.exams.size})",
                                    icon = Icons.Default.Science
                                )
                            }
                            items(uiState.exams.take(3)) { exam ->
                                HealthRecordCard(
                                    title = exam.examType,
                                    description = exam.results ?: "Exame realizado",
                                    date = exam.examDate.toString().substringBefore('T'),
                                    status = exam.status,
                                    type = "exame"
                                )
                            }
                        }

                        // Prescriptions
                        if (uiState.prescriptions.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Prescrições (${uiState.prescriptions.size})",
                                    icon = Icons.Default.Receipt
                                )
                            }
                            items(uiState.prescriptions.take(3)) { prescription ->
                                HealthRecordCard(
                                    title = prescription.medications,
                                    description = prescription.medications,
                                    date = prescription.prescriptionDate,
                                    status = prescription.status,
                                    type = "prescricao"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val theme = PetWiseTheme.Light

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.fromHex("#00b942"),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.fromHex(theme.palette.textPrimary)
            )
        )
    }
}