package edu.fatec.petwise.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

data class PetActivityData(
    val id: String,
    val petName: String,
    val petId: String,
    val activityType: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String? = null,
    val price: String? = null,
    val status: String = "agendado",
    val canCancel: Boolean = true
)

@Composable
fun PetActivityCard(
    activity: PetActivityData,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    val activityColor = when (activity.activityType.lowercase()) {
        "consulta" -> "#2196F3"
        "vacina" -> "#FF9800"
        "exame" -> "#607D8B"
        "prescricao" -> "#9C27B0"
        else -> "#00b942"
    }

    val statusColor = when (activity.status.lowercase()) {
        "agendado", "scheduled" -> Color(0xFF4CAF50)
        "cancelado", "cancelled" -> Color(0xFFDC3545)
        "concluido", "completed" -> Color(0xFF2196F3)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.fromHex(activityColor).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = activity.petName,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 13.sp,
                            color = Color.fromHex(activityColor),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = activity.status.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (activity.canCancel && activity.status.lowercase() == "agendado") {
                    IconButton(
                        onClick = { onCancel(activity.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancelar",
                            tint = Color(0xFFDC3545),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex(theme.palette.textSecondary),
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color.fromHex(theme.palette.textSecondary),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${activity.date}${activity.time?.let { " às $it" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.fromHex(theme.palette.textSecondary),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                activity.price?.let { price ->
                    Text(
                        text = "R$ $price",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.fromHex("#00b942"),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}