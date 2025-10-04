package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.ActivityItem
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun RecentActivitiesSection(
    userType: UserType,
    dataProvider: DashboardDataProvider,
    onActivityClick: (String?) -> Unit,
    onViewAllClick: () -> Unit
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    val activities = dataProvider.getRecentActivities(userType).take(3) // Take only 3 as shown in design
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Atividades Recentes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
            
            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.padding(end = 0.dp)
            ) {
                Text(
                    text = "Ver todas",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.primary)
                    )
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ver todas",
                    tint = Color.fromHex(theme.palette.primary)
                )
            }
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            activities.forEach { activityData ->
                ActivityItem(
                    activityData = activityData,
                    onClick = onActivityClick
                )
            }
        }
    }
}