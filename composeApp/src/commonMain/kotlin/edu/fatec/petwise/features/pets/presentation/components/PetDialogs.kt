package edu.fatec.petwise.features.pets.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import edu.fatec.petwise.features.auth.shared.DynamicAuthFormScreen
import edu.fatec.petwise.features.auth.shared.FormStore
import edu.fatec.petwise.features.pets.domain.models.*
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetUiEvent
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun AddPetDialog(
    formStore: FormStore,
    addPetViewModel: AddPetViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
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
                    Text(
                        text = "Adicionar Novo Pet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.fromHex(theme.palette.textSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Preencha as informações do pet para adicionar ao sistema.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#FFEBEE")
                        )
                    ) {
                        Text(
                            text = message,
                            color = Color.fromHex("#C62828"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                DynamicAuthFormScreen(
                    formStore = formStore,
                    onLoginSuccess = {
                        val values = formStore.getCurrentValues()
                        
                        val species = when (values["species"]) {
                            "Cão" -> PetSpecies.DOG
                            "Gato" -> PetSpecies.CAT
                            "Ave" -> PetSpecies.BIRD
                            "Coelho" -> PetSpecies.RABBIT
                            else -> PetSpecies.OTHER
                        }
                        
                        val gender = when (values["gender"]) {
                            "Macho" -> PetGender.MALE
                            "Fêmea" -> PetGender.FEMALE
                            else -> PetGender.MALE
                        }
                        
                        val healthStatus = when (values["healthStatus"]) {
                            "Excelente" -> HealthStatus.EXCELLENT
                            "Bom" -> HealthStatus.GOOD
                            "Regular" -> HealthStatus.REGULAR
                            "Atenção" -> HealthStatus.ATTENTION
                            "Crítico" -> HealthStatus.CRITICAL
                            else -> HealthStatus.GOOD
                        }

                        addPetViewModel.onEvent(
                            AddPetUiEvent.AddPet(
                                name = values["name"] ?: "",
                                breed = values["breed"] ?: "",
                                species = species,
                                gender = gender,
                                age = values["age"] ?: "",
                                weight = values["weight"] ?: "",
                                healthStatus = healthStatus,
                                ownerName = values["ownerName"] ?: "",
                                ownerPhone = values["ownerPhone"] ?: "",
                                healthHistory = values["healthHistory"] ?: ""
                            )
                        )
                    }
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.fromHex(theme.palette.primary)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: PetFilterOptions,
    onFilterApply: (PetFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    var selectedSpecies by remember { mutableStateOf(currentFilter.species) }
    var selectedHealthStatus by remember { mutableStateOf(currentFilter.healthStatus) }
    var favoritesOnly by remember { mutableStateOf(currentFilter.favoritesOnly) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filtrar Pets",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Espécie",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PetSpecies.values().forEach { species ->
                    FilterChip(
                        onClick = { 
                            selectedSpecies = if (selectedSpecies == species) null else species
                        },
                        label = { Text(species.displayName) },
                        selected = selectedSpecies == species,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = "Status de Saúde",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                HealthStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedHealthStatus == status,
                            onClick = { 
                                selectedHealthStatus = if (selectedHealthStatus == status) null else status
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = favoritesOnly,
                    onCheckedChange = { favoritesOnly = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apenas favoritos",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedSpecies = null
                        selectedHealthStatus = null
                        favoritesOnly = false
                        onFilterApply(PetFilterOptions())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar")
                }
                
                Button(
                    onClick = {
                        onFilterApply(
                            PetFilterOptions(
                                species = selectedSpecies,
                                healthStatus = selectedHealthStatus,
                                favoritesOnly = favoritesOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex(theme.palette.primary)
                    )
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}