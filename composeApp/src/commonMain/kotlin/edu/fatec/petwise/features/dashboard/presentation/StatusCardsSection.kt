package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.StatusCard

@Composable
fun StatusCardsSection(
    userType: UserType,
    dataProvider: DashboardDataProvider,
    onCardClick: (String) -> Unit
) {
    val statusCards = dataProvider.getStatusCards(userType).take(4) // Only take 4 cards as shown in the design
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // First row with 2 status cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            statusCards.take(2).forEach { cardData ->
                StatusCard(
                    statusCardData = cardData,
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Add some spacing between rows
        Column(modifier = Modifier.height(12.dp)) {}
        
        // Second row with the next 2 status cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            statusCards.drop(2).take(2).forEach { cardData ->
                StatusCard(
                    statusCardData = cardData,
                    onClick = onCardClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}