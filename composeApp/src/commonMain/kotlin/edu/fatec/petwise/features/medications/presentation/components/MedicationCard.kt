package edu.fatec.petwise.features.medications.presentation.components

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
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun MedicationCard(
    medication: Medication,
    onClick: (Medication) -> Unit,
    onEditClick: (Medication) -> Unit,
    onDeleteClick: (Medication) -> Unit,
    onMarkAsCompletedClick: (String) -> Unit,
    onPauseResumeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    isSelected: Boolean = false
) {
    val theme = PetWiseTheme.Light
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Calculate medication status based on dates
    val currentDate = remember { kotlinx.datetime.Clock.System.now() }
    val startDate = remember(medication.startDate) {
        try {
            kotlinx.datetime.Instant.parse("${medication.startDate}T00:00:00Z")
        } catch (e: Exception) {
            kotlinx.datetime.Clock.System.now()
        }
    }
    val endDate = remember(medication.endDate) {
        try {
            kotlinx.datetime.Instant.parse("${medication.endDate}T00:00:00Z")
        } catch (e: Exception) {
            kotlinx.datetime.Clock.System.now()
        }
    }

    val medicationStatus = when {
        currentDate < startDate -> "Agendado"
        currentDate > endDate -> "Concluído"
        else -> "Ativo"
    }

    val statusColor = when (medicationStatus) {
        "Ativo" -> Color.fromHex("#4CAF50")
        "Agendado" -> Color.fromHex("#2196F3")
        "Concluído" -> Color.fromHex("#9E9E9E")
        else -> Color.fromHex("#FF9800")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(medication) }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick(medication) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.fromHex("#00b942")
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                // Medication icon
                Surface(
                    shape = CircleShape,
                    color = Color.fromHex("#2196F3").copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Medicamento",
                        tint = Color.fromHex("#2196F3"),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = medication.medicationName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${medication.dosage} • ${medication.frequency}",
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
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Duração",
                            tint = Color.fromHex(theme.palette.textSecondary),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${medication.durationDays} dias (${medication.startDate} - ${medication.endDate})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.fromHex(theme.palette.textSecondary)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (!selectionMode) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onEditClick(medication) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = Color.fromHex(theme.palette.primary),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteClick(medication) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir",
                                tint = Color.fromHex("#F44336"),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (medicationStatus == "Ativo") {
                            IconButton(
                                onClick = { onMarkAsCompletedClick(medication.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Marcar como concluído",
                                    tint = Color.fromHex("#4CAF50"),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = medicationStatus,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Side effects (if any)
            if (medication.sideEffects.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Efeitos colaterais",
                        tint = Color.fromHex("#FF9800"),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Efeitos observados: ${medication.sideEffects}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.fromHex("#FF9800")
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}