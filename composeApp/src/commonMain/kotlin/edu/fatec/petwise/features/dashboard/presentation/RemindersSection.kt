package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.PriorityLevel
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.ReminderItem
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun RemindersSection(
    userType: UserType,
    dataProvider: DashboardDataProvider,
    onReminderClick: (String?) -> Unit
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    // Sort by priority (critical first) and take only 2 as shown in design
    val reminders = dataProvider.getReminders(userType)
        .sortedWith(compareBy<edu.fatec.petwise.features.dashboard.domain.models.ReminderData> { 
            when (it.priority) {
                PriorityLevel.CRITICAL -> 0
                PriorityLevel.HIGH -> 1
                PriorityLevel.MEDIUM -> 2
                PriorityLevel.LOW -> 3
            }
        })
        .take(2)
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header with bell icon
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Lembretes",
                    tint = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Próximos Lembretes",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.fromHex(theme.palette.textPrimary)
                    )
                )
            }
        }
        
        // Reminders list
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reminders.forEach { reminderData ->
                ReminderItem(
                    reminderData = reminderData,
                    onClick = onReminderClick
                )
            }
        }
    }
}