package edu.fatec.petwise.features.consultas.presentation.components

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
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun ConsultaCard(
    consulta: Consulta,
    onClick: (Consulta) -> Unit,
    onEditClick: ((Consulta) -> Unit)? = null,
    onStatusChange: ((String) -> Unit)? = null,
    onMarkAsPaid: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    canEdit: Boolean = true,
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
            ) { onClick(consulta) }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
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
        } else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
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
                        onCheckedChange = { onClick(consulta) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.fromHex("#2196F3")
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(getConsultaTypeColor(consulta.consultaType)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getConsultaTypeIcon(consulta.consultaType),
                        contentDescription = consulta.consultaType.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = consulta.petName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(theme.palette.textPrimary)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = consulta.consultaType.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex(theme.palette.textSecondary),
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Veterinário",
                            tint = Color.fromHex(theme.palette.textSecondary),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = consulta.veterinarianName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.fromHex(theme.palette.textSecondary)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.fromHex(consulta.status.color).copy(alpha = 0.1f),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text(
                                text = consulta.status.displayName,
                                color = Color.fromHex(consulta.status.color),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (!consulta.isPaid && consulta.price > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.fromHex("#FF9800").copy(alpha = 0.1f),
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = "Pendente",
                                        tint = Color.fromHex("#FF9800"),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Pendente",
                                        color = Color.fromHex("#FF9800"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                if (!selectionMode) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (canEdit) {
                                onEditClick?.let { editClick ->
                                    IconButton(
                                        onClick = { editClick(consulta) },
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
                        
                        Text(
                            text = formatDate(consulta.consultaDate),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.fromHex(theme.palette.textPrimary)
                            )
                        )
                        Text(
                            text = consulta.consultaTime,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.fromHex(theme.palette.textSecondary)
                            )
                        )
                        if (consulta.price > 0) {
                            Text(
                                text = "R$${consulta.price}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (consulta.isPaid) 
                                        Color.fromHex("#4CAF50") 
                                    else 
                                        Color.fromHex("#FF9800")
                                )
                            )
                        }
                    }
                }
            }

            if (consulta.symptoms.isNotBlank()) {
                Divider(
                    color = Color.fromHex(theme.palette.textSecondary).copy(alpha = 0.1f),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Sintomas",
                        tint = Color.fromHex(theme.palette.textSecondary),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = consulta.symptoms,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.fromHex(theme.palette.textSecondary)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun getConsultaTypeColor(type: ConsultaType): Color {
    return when (type) {
        ConsultaType.ROUTINE -> Color.fromHex("#2196F3")
        ConsultaType.EMERGENCY -> Color.fromHex("#F44336")
        ConsultaType.FOLLOW_UP -> Color.fromHex("#9C27B0")
        ConsultaType.VACCINATION -> Color.fromHex("#4CAF50")
        ConsultaType.SURGERY -> Color.fromHex("#FF5722")
        ConsultaType.EXAM -> Color.fromHex("#00BCD4")
        ConsultaType.OTHER -> Color.fromHex("#607D8B")
    }
}

@Composable
private fun getConsultaTypeIcon(type: ConsultaType) = when (type) {
    ConsultaType.ROUTINE -> Icons.Default.HealthAndSafety
    ConsultaType.EMERGENCY -> Icons.Default.LocalHospital
    ConsultaType.FOLLOW_UP -> Icons.Default.EventRepeat
    ConsultaType.VACCINATION -> Icons.Default.Vaccines
    ConsultaType.SURGERY -> Icons.Default.MedicalServices
    ConsultaType.EXAM -> Icons.Default.Science
    ConsultaType.OTHER -> Icons.Default.MoreHoriz
}

private fun formatDate(dateString: String): String {
    val parts = dateString.split("-")
    return if (parts.size == 3) {
        "${parts[2]}/${parts[1]}"
    } else {
        dateString
    }
}
