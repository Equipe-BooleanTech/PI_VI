package edu.fatec.petwise.features.dashboard.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

class DefaultDashboardDataProvider : DashboardDataProvider {
    override fun getStatusCards(userType: UserType): List<StatusCardData> {
        return when(userType) {
            UserType.OWNER -> listOf(
                StatusCardData(
                    id = "pets",
                    title = "Meus Pets",
                    count = 3,
                    icon = Icons.Default.Pets,
                    iconBackground = "#00b942",
                    route = "pets"
                ),
                StatusCardData(
                    id = "appointments",
                    title = "Consultas Agendadas",
                    count = 2,
                    icon = Icons.Default.EventNote,
                    iconBackground = "#4169FF",
                    route = "appointments"
                ),
                StatusCardData(
                    id = "vaccines",
                    title = "Vacinas Pendentes",
                    count = 1,
                    icon = Icons.Default.HealthAndSafety,
                    iconBackground = "#FFC107",
                    route = "vaccines"
                ),
                StatusCardData(
                    id = "reminders",
                    title = "Lembretes",
                    count = 4,
                    icon = Icons.Default.Notifications,
                    iconBackground = "#9C27B0",
                    route = "reminders"
                )
            )
            UserType.VET -> listOf(
                StatusCardData(
                    id = "appointments",
                    title = "Consultas Hoje",
                    count = 5,
                    icon = Icons.Default.EventNote,
                    iconBackground = "#2196F3",
                    route = "appointments"
                ),
                StatusCardData(
                    id = "patients",
                    title = "Pacientes",
                    count = 12,
                    icon = Icons.Default.Pets,
                    iconBackground = "#00b942",
                    route = "patients"
                ),
                StatusCardData(
                    id = "tasks",
                    title = "Tarefas",
                    count = 3,
                    icon = Icons.Default.CheckCircle,
                    iconBackground = "#FFC107",
                    route = "tasks"
                ),
                StatusCardData(
                    id = "messages",
                    title = "Mensagens",
                    count = 2,
                    icon = Icons.Default.Message,
                    iconBackground = "#9C27B0",
                    route = "messages"
                )
            )
            UserType.ADMIN -> listOf(
                StatusCardData(
                    id = "users",
                    title = "Usuários",
                    count = 45,
                    icon = Icons.Default.People,
                    iconBackground = "#00b942",
                    route = "users"
                ),
                StatusCardData(
                    id = "analytics",
                    title = "Análises",
                    count = 4,
                    icon = Icons.Default.BarChart,
                    iconBackground = "#2196F3",
                    route = "analytics"
                ),
                StatusCardData(
                    id = "reports",
                    title = "Relatórios",
                    count = 12,
                    icon = Icons.Default.Description,
                    iconBackground = "#FFC107",
                    route = "reports"
                ),
                StatusCardData(
                    id = "settings",
                    title = "Configurações",
                    count = 8,
                    icon = Icons.Default.Settings,
                    iconBackground = "#9C27B0",
                    route = "settings"
                )
            )
        }
    }

    override fun getQuickActions(userType: UserType): List<QuickActionData> {
        return when(userType) {
            UserType.OWNER -> listOf(
                QuickActionData(
                    id = "pets",
                    title = "Meus Pets",
                    icon = Icons.Default.Pets,
                    route = "pets",
                    background = "#00b942"
                ),
                QuickActionData(
                    id = "appointments",
                    title = "Consultas",
                    icon = Icons.Default.EventNote,
                    route = "appointments",
                    background = "#4169FF"
                ),
                QuickActionData(
                    id = "medications",
                    title = "Medicamentos",
                    icon = Icons.Default.Medication,
                    route = "medications",
                    background = "#9C27B0"
                ),
                QuickActionData(
                    id = "vaccines",
                    title = "Vacinas",
                    icon = Icons.Default.HealthAndSafety,
                    route = "vaccines",
                    background = "#FFC107"
                )
            )
            UserType.VET -> listOf(
                QuickActionData(
                    id = "schedule",
                    title = "Agenda",
                    icon = Icons.Default.EventNote,
                    route = "schedule",
                    background = "#2196F3"
                ),
                QuickActionData(
                    id = "patients",
                    title = "Pacientes",
                    icon = Icons.Default.Pets,
                    route = "patients",
                    background = "#00b942"
                ),
                QuickActionData(
                    id = "prescriptions",
                    title = "Prescrições",
                    icon = Icons.Default.Medication,
                    route = "prescriptions",
                    background = "#9C27B0"
                ),
                QuickActionData(
                    id = "records",
                    title = "Registros",
                    icon = Icons.Default.Description,
                    route = "records",
                    background = "#FFC107"
                )
            )
            UserType.ADMIN -> listOf(
                QuickActionData(
                    id = "users",
                    title = "Usuários",
                    icon = Icons.Default.People,
                    route = "users",
                    background = "#00b942"
                ),
                QuickActionData(
                    id = "clinics",
                    title = "Clínicas",
                    icon = Icons.Default.LocalHospital,
                    route = "clinics",
                    background = "#2196F3"
                ),
                QuickActionData(
                    id = "reports",
                    title = "Relatórios",
                    icon = Icons.Default.BarChart,
                    route = "reports",
                    background = "#9C27B0"
                ),
                QuickActionData(
                    id = "settings",
                    title = "Configurações",
                    icon = Icons.Default.Settings,
                    route = "settings",
                    background = "#FFC107"
                )
            )
        }
    }

    override fun getRecentActivities(userType: UserType): List<RecentActivityData> {
        return when(userType) {
            UserType.OWNER -> listOf(
                RecentActivityData(
                    id = "appointment1",
                    title = "Consulta Veterinária",
                    description = "Max foi ao veterinário para checkup",
                    date = "03/10/2025",
                    icon = Icons.Default.EventNote,
                    iconBackground = "#2196F3",
                    route = "appointments/detail/1"
                ),
                RecentActivityData(
                    id = "vaccine1",
                    title = "Vacina Aplicada",
                    description = "Bella recebeu vacina antirrábica",
                    date = "01/10/2025",
                    icon = Icons.Default.HealthAndSafety,
                    iconBackground = "#00b942",
                    route = "vaccines/detail/1"
                ),
                RecentActivityData(
                    id = "weight1",
                    title = "Atualização de Peso",
                    description = "Luna foi pesada: 12,5kg",
                    date = "28/09/2025",
                    icon = Icons.Default.FitnessCenter,
                    iconBackground = "#FFC107",
                    route = "pets/detail/1/health"
                )
            )
            UserType.VET -> listOf(
                RecentActivityData(
                    id = "appointment1",
                    title = "Consulta Realizada",
                    description = "Checkup anual do Max",
                    date = "03/10/2025",
                    icon = Icons.Default.EventNote,
                    iconBackground = "#2196F3",
                    route = "appointments/detail/1"
                ),
                RecentActivityData(
                    id = "prescription1",
                    title = "Prescrição Emitida",
                    description = "Antibiótico para Bella",
                    date = "03/10/2025",
                    icon = Icons.Default.Medication,
                    iconBackground = "#9C27B0",
                    route = "prescriptions/detail/1"
                ),
                RecentActivityData(
                    id = "vaccine1",
                    title = "Vacina Aplicada",
                    description = "Antirrábica para Luna",
                    date = "01/10/2025",
                    icon = Icons.Default.HealthAndSafety,
                    iconBackground = "#00b942",
                    route = "vaccines/detail/1"
                )
            )
            UserType.ADMIN -> listOf(
                RecentActivityData(
                    id = "user1",
                    title = "Novo Usuário",
                    description = "Maria Silva registrou-se no sistema",
                    date = "03/10/2025",
                    icon = Icons.Default.PersonAdd,
                    iconBackground = "#00b942",
                    route = "users/detail/1"
                ),
                RecentActivityData(
                    id = "clinic1",
                    title = "Clínica Atualizada",
                    description = "PetCare atualizou informações",
                    date = "02/10/2025",
                    icon = Icons.Default.LocalHospital,
                    iconBackground = "#2196F3",
                    route = "clinics/detail/1"
                ),
                RecentActivityData(
                    id = "report1",
                    title = "Relatório Gerado",
                    description = "Relatório mensal de consultas",
                    date = "01/10/2025",
                    icon = Icons.Default.Description,
                    iconBackground = "#FFC107",
                    route = "reports/detail/1"
                )
            )
        }
    }

    override fun getReminders(userType: UserType): List<ReminderData> {
        return when(userType) {
            UserType.OWNER -> listOf(
                ReminderData(
                    id = "vaccine1",
                    title = "Vacina Antirrábica",
                    description = "Max precisa tomar a vacina antirrábica",
                    date = "15/10/2025",
                    priority = PriorityLevel.CRITICAL,
                    icon = Icons.Default.HealthAndSafety,
                    iconBackground = "#F44336",
                    route = "reminders/detail/1"
                ),
                ReminderData(
                    id = "medication1",
                    title = "Medicação Diária",
                    description = "Dar remédio para Bella após o almoço",
                    date = "Diariamente às 14:00",
                    priority = PriorityLevel.HIGH,
                    icon = Icons.Default.Medication,
                    iconBackground = "#FF9800",
                    route = "reminders/detail/2"
                ),
                ReminderData(
                    id = "appointment1",
                    title = "Consulta de Rotina",
                    description = "Luna tem consulta agendada com Dr. Silva",
                    date = "20/10/2025 - 15:00",
                    priority = PriorityLevel.MEDIUM,
                    icon = Icons.Default.EventNote,
                    iconBackground = "#2196F3",
                    route = "reminders/detail/3"
                ),
                ReminderData(
                    id = "grooming1",
                    title = "Banho e Tosa",
                    description = "Agendar banho e tosa para Max",
                    date = "25/10/2025",
                    priority = PriorityLevel.LOW,
                    icon = Icons.Default.ContentCut,
                    iconBackground = "#4CAF50",
                    route = "reminders/detail/4"
                )
            )
            UserType.VET -> listOf(
                ReminderData(
                    id = "appointment1",
                    title = "Consulta - Max",
                    description = "Checkup pós-operatório com João Silva",
                    date = "10/10/2025 - 10:00",
                    priority = PriorityLevel.CRITICAL,
                    icon = Icons.Default.EventNote,
                    iconBackground = "#F44336",
                    route = "appointments/detail/1"
                ),
                ReminderData(
                    id = "lab1",
                    title = "Resultados de Exames",
                    description = "Revisar exames de sangue da Bella",
                    date = "12/10/2025",
                    priority = PriorityLevel.HIGH,
                    icon = Icons.Default.Science,
                    iconBackground = "#FF9800",
                    route = "lab/detail/1"
                ),
                ReminderData(
                    id = "meeting1",
                    title = "Reunião de Equipe",
                    description = "Discussão de casos clínicos",
                    date = "15/10/2025 - 14:00",
                    priority = PriorityLevel.MEDIUM,
                    icon = Icons.Default.People,
                    iconBackground = "#2196F3",
                    route = "meetings/detail/1"
                )
            )
            UserType.ADMIN -> listOf(
                ReminderData(
                    id = "report1",
                    title = "Fechamento Mensal",
                    description = "Gerar relatórios financeiros",
                    date = "31/10/2025",
                    priority = PriorityLevel.HIGH,
                    icon = Icons.Default.Description,
                    iconBackground = "#FF9800",
                    route = "reports/finance"
                ),
                ReminderData(
                    id = "maintenance1",
                    title = "Manutenção do Sistema",
                    description = "Atualização de segurança programada",
                    date = "12/10/2025 - 22:00",
                    priority = PriorityLevel.MEDIUM,
                    icon = Icons.Default.Build,
                    iconBackground = "#2196F3",
                    route = "maintenance/detail/1"
                ),
                ReminderData(
                    id = "license1",
                    title = "Renovação de Licenças",
                    description = "Licenças de software vencem em breve",
                    date = "25/10/2025",
                    priority = PriorityLevel.LOW,
                    icon = Icons.Default.FactCheck,
                    iconBackground = "#4CAF50",
                    route = "licenses"
                )
            )
        }
    }

    override fun getGreeting(userType: UserType, userName: String): String {
        return "Olá, $userName! 👋"
    }

    override fun getSubGreeting(userType: UserType): String {
        return when(userType) {
            UserType.OWNER -> "Cuidando de 3 pets com carinho"
            UserType.VET -> "5 consultas agendadas hoje"
            UserType.ADMIN -> "Gerenciando 3 clínicas ativas"
        }
    }
}