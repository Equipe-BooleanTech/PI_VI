package edu.fatec.petwise.features.pets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetGender
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun PetInfoCard(
    pet: Pet
) {
    val theme = PetWiseTheme.Light

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with pet icon and name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Pet",
                    tint = Color.fromHex("#00b942"),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )
                    Text(
                        text = "${pet.species} • ${pet.breed}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex(theme.palette.textSecondary)
                        )
                    )
                }
                // Health status indicator
                val healthColor = when (pet.healthStatus) {
                    HealthStatus.EXCELLENT -> "#00b942"
                    HealthStatus.GOOD -> "#4CAF50"
                    HealthStatus.REGULAR -> "#FFC107"
                    HealthStatus.ATTENTION -> "#FF9800"
                    HealthStatus.CRITICAL -> "#F44336"
                    else -> "#607D8B" // Default fallback
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex(healthColor)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = pet.healthStatus.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pet details grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left column
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(
                        icon = Icons.Default.Cake,
                        label = "Idade",
                        value = "${pet.age} anos"
                    )
                    DetailItem(
                        icon = Icons.Default.MonitorWeight,
                        label = "Peso",
                        value = "${pet.weight} kg"
                    )
                    DetailItem(
                        icon = if (pet.gender == PetGender.MALE) Icons.Default.Male else Icons.Default.Female,
                        label = "Sexo",
                        value = pet.gender.displayName
                    )
                }

                // Right column
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(
                        icon = Icons.Default.Person,
                        label = "Tutor",
                        value = pet.ownerName
                    )
                    DetailItem(
                        icon = Icons.Default.Palette,
                        label = "Raça",
                        value = pet.breed
                    )
                    DetailItem(
                        icon = if (pet.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        label = "Favorito",
                        value = if (pet.isFavorite) "Sim" else "Não"
                    )
                }
            }

            // Additional info if available
            if (pet.healthHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Histórico de Saúde",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pet.healthHistory,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.fromHex("#00b942"),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.fromHex("#666666"),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex("#333333")
                )
            )
        }
    }
}