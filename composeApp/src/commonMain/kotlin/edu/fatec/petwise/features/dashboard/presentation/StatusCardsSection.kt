package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import edu.fatec.petwise.features.dashboard.presentation.components.StatusCard
import edu.fatec.petwise.features.dashboard.domain.models.StatusCardData

@Composable
fun StatusCardsSection(
    userType: String,
    petCount: Int,
    consultasCount: Int,
    vacinasCount: Int,
    prescriptionsCount: Int,
    examsCount: Int,
    labsCount: Int,
    foodCount: Int,
    hygieneCount: Int,
    toysCount: Int,
    onCardClick: (String) -> Unit
) {
    val theme = PetWiseTheme.Light

    // Create status cards with real data
    val statusCards = when (userType.uppercase()) {
        "OWNER" -> listOf(
            StatusCardData(
                title = "Pets",
                value = petCount.toString(),
                icon = "pets",
                route = "pets",
                color = "#00b942"
            ),
            StatusCardData(
                title = "Consultas",
                value = consultasCount.toString(),
                icon = "event",
                route = "appointments",
                color = "#2196F3"
            ),
            StatusCardData(
                title = "Vacinas",
                value = vacinasCount.toString(),
                icon = "vaccines",
                route = "vaccines",
                color = "#FF9800"
            ),
            StatusCardData(
                title = "Prescrições",
                value = prescriptionsCount.toString(),
                icon = "medication",
                route = "prescriptions",
                color = "#9C27B0"
            ),
            StatusCardData(
                title = "Exames",
                value = examsCount.toString(),
                icon = "lab",
                route = "exams",
                color = "#607D8B"
            )
        )
        "VETERINARY" -> listOf(
            StatusCardData(
                title = "Consultas Hoje",
                value = consultasCount.toString(),
                icon = "event",
                route = "appointments",
                color = "#2196F3"
            ),
            StatusCardData(
                title = "Vacinas Agendadas",
                value = vacinasCount.toString(),
                icon = "vaccines",
                route = "vaccines",
                color = "#FF9800"
            ),
            StatusCardData(
                title = "Prescrições",
                value = "0", // TODO: Add prescriptions count
                icon = "medication",
                route = "prescriptions",
                color = "#9C27B0"
            ),
            StatusCardData(
                title = "Exames",
                value = "0", // TODO: Add exams count
                icon = "lab",
                route = "exams",
                color = "#607D8B"
            )
        )
        "PETSHOP" -> listOf(
            StatusCardData(
                title = "Ração em Estoque",
                value = "0", // TODO: Add food count
                icon = "food",
                route = "food",
                color = "#00b942"
            ),
            StatusCardData(
                title = "Produtos Higiene",
                value = "0", // TODO: Add hygiene count
                icon = "hygiene",
                route = "hygiene",
                color = "#2196F3"
            ),
            StatusCardData(
                title = "Brinquedos",
                value = "0", // TODO: Add toys count
                icon = "toys",
                route = "toys",
                color = "#FF9800"
            )
        )
        "PHARMACY" -> listOf(
            StatusCardData(
                title = "Medicações",
                value = vacinasCount.toString(), // TODO: Use medicationsCount when available
                icon = "medication",
                route = "medications",
                color = "#9C27B0"
            ),
            StatusCardData(
                title = "Prescrições",
                value = prescriptionsCount.toString(),
                icon = "prescription",
                route = "prescriptions",
                color = "#2196F3"
            )
        )
        else -> listOf(
            StatusCardData(
                title = "Pets",
                value = petCount.toString(),
                icon = "pets",
                route = "pets",
                color = "#00b942"
            ),
        )
    }

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

        // Display cards in rows of 2
        val rows = statusCards.chunked(2)
        rows.forEachIndexed { index, rowCards ->
            if (index > 0) {
                Column(modifier = Modifier.height(12.dp)) {}
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCards.forEach { cardData ->
                    StatusCard(
                        statusCardData = cardData,
                        onClick = onCardClick,
                        modifier = Modifier.weight(1f),
                        isClickable = userType.uppercase() != "OWNER" || cardData.route == "pets"
                    )
                }
                // Fill remaining space if odd number
                if (rowCards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}