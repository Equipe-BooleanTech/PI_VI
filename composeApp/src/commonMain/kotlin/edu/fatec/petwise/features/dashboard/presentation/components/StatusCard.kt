package edu.fatec.petwise.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.dashboard.domain.models.StatusCardData
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun StatusCard(
    statusCardData: StatusCardData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(statusCardData.route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.fromHex(theme.palette.cardBackground)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusCardData.icon,
                    contentDescription = statusCardData.title,
                    tint = Color.fromHex(statusCardData.iconBackground),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Text(
                text = statusCardData.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex(theme.palette.textPrimary),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = statusCardData.count.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.fromHex(theme.palette.textPrimary),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}