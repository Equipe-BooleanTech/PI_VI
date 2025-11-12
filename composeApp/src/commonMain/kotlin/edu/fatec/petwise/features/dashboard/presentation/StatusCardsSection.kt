package edu.fatec.petwise.features.dashboard.presentation

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
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.StatusCard
import edu.fatec.petwise.features.dashboard.domain.models.StatusCardData

@Composable
fun StatusCardsSection(
    userType: UserType,
    petCount: Int,
    consultasCount: Int,
    vacinasCount: Int,
    onCardClick: (String) -> Unit
) {
    val theme = PetWiseTheme.Light

    // Create status cards with real data
    val statusCards = when (userType) {
        UserType.OWNER -> listOf(
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
                title = "Medicações",
                value = "0", // TODO: Add medication count when available
                icon = "notifications",
                route = "reminders",
                color = "#9C27B0"
            )
        )
        UserType.VETERINARY -> listOf(
            StatusCardData(
                title = "Pacientes",
                value = petCount.toString(),
                icon = "pets",
                route = "pets",
                color = "#00b942"
            ),
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
                title = "Pendências",
                value = "0", // TODO: Add pending tasks when available
                icon = "pending",
                route = "reminders",
                color = "#F44336"
            )
        )
        UserType.PETSHOP -> listOf(
            StatusCardData(
                title = "Total Pets",
                value = petCount.toString(),
                icon = "pets",
                route = "pets",
                color = "#00b942"
            ),
            StatusCardData(
                title = "Consultas Ativas",
                value = consultasCount.toString(),
                icon = "event",
                route = "appointments",
                color = "#2196F3"
            ),
            StatusCardData(
                title = "Vacinas Aplicadas",
                value = vacinasCount.toString(),
                icon = "vaccines",
                route = "vaccines",
                color = "#FF9800"
            ),
            StatusCardData(
                title = "Usuários Ativos",
                value = "0", // TODO: Add user count when available
                icon = "people",
                route = "users",
                color = "#607D8B"
            )
        )
        UserType.PHARMACY -> listOf(
            StatusCardData(
                title = "Pedidos Pendentes",
                value = "0",
                icon = "shopping_cart",
                route = "orders",
                color = "#2196F3"
            ),
            StatusCardData(
                title = "Produtos",
                value = "0",
                icon = "inventory",
                route = "products",
                color = "#00b942"
            ),
            StatusCardData(
                title = "Clientes",
                value = "0",
                icon = "people",
                route = "clients",
                color = "#FF9800"
            ),
            StatusCardData(
                title = "Receitas",
                value = "0",
                icon = "receipt",
                route = "prescriptions",
                color = "#9C27B0"
            )
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