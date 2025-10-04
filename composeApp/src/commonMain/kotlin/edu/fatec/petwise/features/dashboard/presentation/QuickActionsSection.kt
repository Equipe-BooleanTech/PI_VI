package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.QuickActionItem
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun QuickActionsSection(
    userType: UserType,
    dataProvider: DashboardDataProvider,
    onActionClick: (String) -> Unit
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    val quickActions = dataProvider.getQuickActions(userType).take(4) // Only take 4 actions as shown in the design
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Ações Rápidas",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color.fromHex(theme.palette.textPrimary)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // First row with 2 actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickActions.take(2).forEach { actionData ->
                QuickActionItem(
                    quickActionData = actionData,
                    onClick = onActionClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Add some spacing between rows
        Column(modifier = Modifier.height(12.dp)) {}
        
        // Second row with the next 2 actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickActions.drop(2).take(2).forEach { actionData ->
                QuickActionItem(
                    quickActionData = actionData,
                    onClick = onActionClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}