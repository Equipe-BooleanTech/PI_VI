package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.features.dashboard.domain.models.QuickActionData
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.QuickActionItem
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun QuickActionsSection(
    userType: UserType,
    onActionClick: (String) -> Unit
) {
    val theme = PetWiseTheme.Light

    val quickActions = when (userType) {
        UserType.OWNER -> listOf(
            QuickActionData(
                id = "add_pet",
                title = "Adicionar Pet",
                icon = Icons.Default.Add,
                route = "pets",
                background = "#00b942"
            ),
            QuickActionData(
                id = "manage_pets",
                title = "Gerenciar Pets",
                icon = Icons.Default.Pets,
                route = "pets",
                background = "#2196F3"
            )
        )
        UserType.VETERINARY -> listOf(
            QuickActionData(
                id = "new_appointment",
                title = "Nova Consulta",
                icon = Icons.Default.CalendarMonth,
                route = "appointments",
                background = "#2196F3"
            ),
            QuickActionData(
                id = "apply_vaccine",
                title = "Aplicar Vacina",
                icon = Icons.Default.Vaccines,
                route = "vaccines",
                background = "#FF9800"
            ),
            QuickActionData(
                id = "new_prescription",
                title = "Nova Prescrição",
                icon = Icons.Default.Medication,
                route = "prescriptions",
                background = "#9C27B0"
            ),
            QuickActionData(
                id = "new_exam",
                title = "Novo Exame",
                icon = Icons.Default.Add,
                route = "exams",
                background = "#607D8B"
            ),
            QuickActionData(
                id = "new_lab",
                title = "Novo Laboratório",
                icon = Icons.Default.FlashOn,
                route = "labs",
                background = "#FF5722"
            )
        )
        UserType.PHARMACY -> listOf(
            QuickActionData(
                id = "manage_medications",
                title = "Gerenciar Medicações",
                icon = Icons.Default.Medication,
                route = "medications",
                background = "#9C27B0"
            ),
            QuickActionData(
                id = "view_prescriptions",
                title = "Ver Prescrições",
                icon = Icons.Default.CalendarMonth,
                route = "prescriptions",
                background = "#2196F3"
            )
        )
        UserType.PETSHOP -> listOf(
            QuickActionData(
                id = "manage_food",
                title = "Gerenciar Ração",
                icon = Icons.Default.Add,
                route = "food",
                background = "#00b942"
            ),
            QuickActionData(
                id = "manage_hygiene",
                title = "Gerenciar Higiene",
                icon = Icons.Default.FlashOn,
                route = "hygiene",
                background = "#2196F3"
            ),
            QuickActionData(
                id = "manage_toys",
                title = "Gerenciar Brinquedos",
                icon = Icons.Default.Pets,
                route = "toys",
                background = "#FF9800"
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
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Ações Rápidas",
                tint = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Ações Rápidas",
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
            quickActions.take(2).forEach { actionData ->
                QuickActionItem(
                    quickActionData = actionData,
                    onClick = onActionClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(modifier = Modifier.height(12.dp)) {}

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