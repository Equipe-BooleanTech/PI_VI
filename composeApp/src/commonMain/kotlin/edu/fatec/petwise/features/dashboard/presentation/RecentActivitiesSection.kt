package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.dashboard.domain.models.RecentActivityData
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.dashboard.presentation.components.PetActivityCard
import edu.fatec.petwise.features.dashboard.presentation.components.PetActivityData
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import androidx.compose.foundation.background

@Composable
fun RecentActivitiesSection(
    userType: UserType,
    upcomingConsultas: List<Consulta>,
    onActivityClick: (String?) -> Unit,
    onCancelActivity: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    val theme = PetWiseTheme.Light

    val activities = when (userType) {
        UserType.OWNER -> {
            // For OWNER, show detailed pet activities with cancel option
            upcomingConsultas.take(3).map { consulta ->
                PetActivityData(
                    id = consulta.id,
                    petName = consulta.petName,
                    petId = consulta.petId,
                    activityType = "consulta",
                    title = consulta.consultaType.displayName,
                    description = consulta.symptoms.ifEmpty { "Consulta veterinária" },
                    date = consulta.consultaDate,
                    time = consulta.consultaTime,
                    price = if (consulta.price > 0) consulta.price.toString() else null,
                    status = consulta.status.displayName.lowercase(),
                    canCancel = consulta.status == edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus.SCHEDULED
                )
            }
        }
        UserType.VETERINARY -> {
            // For VETERINARY, show medical activities (consultations, vaccines, prescriptions, exams, labs)
            upcomingConsultas.take(3).map { consulta ->
                val formattedDate = "${consulta.consultaDate} ${consulta.consultaTime}"

                RecentActivityData(
                    id = consulta.id,
                    title = "Próximo atendimento",
                    description = "${consulta.petName} - ${consulta.symptoms.ifEmpty { "Consulta veterinária" }}",
                    date = formattedDate,
                    icon = Icons.Default.CalendarMonth,
                    iconBackground = "#2196F3",
                    route = "appointments/${consulta.id}"
                )
            }
        }
        UserType.PHARMACY -> {
            // For PHARMACY, show medication-related activities
            // TODO: Replace with actual medication activities when available
            upcomingConsultas.take(3).map { consulta ->
                val formattedDate = "${consulta.consultaDate} ${consulta.consultaTime}"

                RecentActivityData(
                    id = consulta.id,
                    title = "Prescrição pendente",
                    description = "${consulta.petName} - Medicação prescrita",
                    date = formattedDate,
                    icon = Icons.Default.Add,
                    iconBackground = "#9C27B0",
                    route = "prescriptions/${consulta.id}"
                )
            }
        }
        UserType.PETSHOP -> {
            // For PETSHOP, show product-related activities
            // TODO: Replace with actual product activities when available
            upcomingConsultas.take(3).map { consulta ->
                val formattedDate = "${consulta.consultaDate} ${consulta.consultaTime}"

                RecentActivityData(
                    id = consulta.id,
                    title = "Produto vendido",
                    description = "${consulta.petName} - Compra realizada",
                    date = formattedDate,
                    icon = Icons.Default.FlashOn,
                    iconBackground = "#00b942",
                    route = "suprimentos/${consulta.id}"
                )
            }
        }
    }

    if (activities.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Atividades Recentes",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = when (userType) {
                            UserType.OWNER -> "Consultas Agendadas"
                            UserType.VETERINARY -> "Atendimentos"
                            UserType.PHARMACY -> "Prescrições"
                            UserType.PETSHOP -> "Vendas Recentes"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )
                }
            }

            Text(
                text = when (userType) {
                    UserType.OWNER -> "Nenhuma consulta agendada"
                    UserType.VETERINARY -> "Nenhum atendimento agendado"
                    UserType.PHARMACY -> "Nenhuma prescrição pendente"
                    UserType.PETSHOP -> "Nenhuma venda recente"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Atividades Recentes",
                    tint = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
                    Text(
                        text = when (userType) {
                            UserType.OWNER -> "Consultas Agendadas"
                            UserType.VETERINARY -> "Atendimentos"
                            UserType.PHARMACY -> "Prescrições"
                            UserType.PETSHOP -> "Vendas Recentes"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.fromHex(theme.palette.textPrimary)
                        )
                    )
            }

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "Ver tudo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex("#00b942"),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (userType) {
                UserType.OWNER -> {
                    (activities as List<PetActivityData>).forEach { activity ->
                        PetActivityCard(
                            activity = activity,
                            onCancel = onCancelActivity
                        )
                    }
                }
                else -> {
                    (activities as List<RecentActivityData>).forEach { activityData ->
                        RecentActivityItem(
                            recentActivityData = activityData,
                            onClick = onActivityClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivityItem(
    recentActivityData: RecentActivityData,
    onClick: (String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recentActivityData.route) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = recentActivityData.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.fromHex(recentActivityData.iconBackground)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recentActivityData.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = recentActivityData.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = recentActivityData.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}