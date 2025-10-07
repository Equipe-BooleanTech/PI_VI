package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
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
    val statusCards = dataProvider.getStatusCards(userType).take(4)
    
    val theme =PetWiseTheme.Light
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = "Status",
                tint = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Status Geral",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
        }
        
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
        
        Column(modifier = Modifier.height(12.dp)) {}
        
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