package edu.fatec.petwise.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.PriorityLevel
import edu.fatec.petwise.features.dashboard.domain.models.ReminderData
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun ReminderItem(
    reminderData: ReminderData,
    onClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    
    val priorityColor = when (reminderData.priority) {
        PriorityLevel.CRITICAL -> Color.Red
        PriorityLevel.HIGH -> Color.fromHex("#FF9800")
        PriorityLevel.MEDIUM -> Color.fromHex("#2196F3")
        PriorityLevel.LOW -> Color.fromHex("#4CAF50")
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(reminderData.route) }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left indicator strip with priority color
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = priorityColor
                )
            }
            
            // Main content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminderData.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.fromHex(theme.palette.textPrimary)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = reminderData.description.split(" ").first(), // Just show pet name as in design
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Date chip
            Surface(
                color = Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = reminderData.date.split(" - ").first(), // Just show day as in design
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}