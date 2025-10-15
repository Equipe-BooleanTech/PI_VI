package edu.fatec.petwise.features.pets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetSpecies
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun PetCard(
    pet: Pet,
    onClick: (Pet) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onEditClick: (Pet) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    isSelected: Boolean = false
) {
    val theme = PetWiseTheme.Light
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(pet) }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color.fromHex("#00b942").copy(alpha = 0.1f)
                isHovered -> Color.White.copy(alpha = 0.9f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 3.dp else 1.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color.fromHex("#00b942"))
        } else null
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
                    onCheckedChange = { onClick(pet) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.fromHex("#00b942")
                    ),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            PetProfileImage(
                pet = pet,
                size = 56.dp,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${pet.breed} • ${pet.species.displayName} • ${pet.gender.displayName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Tutor",
                        tint = Color.fromHex(theme.palette.textSecondary),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pet.ownerName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.fromHex(theme.palette.textSecondary)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.fromHex(pet.healthStatus.color).copy(alpha = 0.1f),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = pet.healthStatus.displayName,
                        color = Color.fromHex(pet.healthStatus.color),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (!selectionMode) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onFavoriteClick(pet.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (pet.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (pet.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (pet.isFavorite) Color.fromHex("#FFC107") else Color.fromHex(theme.palette.textSecondary),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { onEditClick(pet) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.fromHex(theme.palette.primary),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        pet.nextAppointment?.let { appointment ->
            Divider(
                color = Color.fromHex(theme.palette.textSecondary).copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Próxima consulta",
                    tint = Color.fromHex(theme.palette.primary),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Próxima consulta: $appointment",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.fromHex(theme.palette.primary),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun getSpeciesColor(species: PetSpecies): Color {
    return when (species) {
        PetSpecies.DOG -> Color.fromHex("#00b942")
        PetSpecies.CAT -> Color.fromHex("#FF9800")
        PetSpecies.BIRD -> Color.fromHex("#2196F3")
        PetSpecies.RABBIT -> Color.fromHex("#9C27B0")
        PetSpecies.OTHER -> Color.fromHex("#607D8B")
    }
}

@Composable
fun PetProfileImage(
    pet: Pet,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    edu.fatec.petwise.presentation.shared.components.AsyncImage(
        imageUrl = pet.profileImageUrl,
        contentDescription = "Imagem de ${pet.name}",
        fallbackIcon = getSpeciesIcon(pet.species),
        fallbackColor = getSpeciesColor(pet.species),
        size = size,
        modifier = modifier
    )
}

@Composable
private fun getSpeciesIcon(species: PetSpecies) = when (species) {
    PetSpecies.DOG -> Icons.Default.Pets
    PetSpecies.CAT -> Icons.Default.Pets
    PetSpecies.BIRD -> Icons.Default.Pets
    PetSpecies.RABBIT -> Icons.Default.Pets
    PetSpecies.OTHER -> Icons.Default.Pets
}
