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
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun HealthRecordCard(
    title: String,
    description: String,
    date: String,
    status: String,
    type: String
) {
    val theme = PetWiseTheme.Light

    val (icon: androidx.compose.ui.graphics.vector.ImageVector, color: String) = when (type) {
        "consulta" -> Icons.Default.Event to "#2196F3"
        "vacina" -> Icons.Default.Vaccines to "#FF9800"
        "medicacao" -> Icons.Default.Medication to "#9C27B0"
        "exame" -> Icons.Default.Science to "#607D8B"
        "prescricao" -> Icons.Default.Receipt to "#4CAF50"
        else -> Icons.Default.Info to "#666666"
    }

    val statusColor = when (status.lowercase()) {
        "concluído", "concluida", "aplicada", "applied", "completed" -> "#00b942"
        "agendado", "agendada", "scheduled", "pendente", "pending" -> "#FF9800"
        "cancelado", "cancelada", "cancelled" -> "#d32f2f"
        else -> "#666666"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex(color).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.fromHex(color),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.fromHex("#666666")
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex(statusColor).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.fromHex(statusColor),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}