package edu.fatec.petwise.features.dashboard.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserType {
    OWNER,
    VETERINARY,
    PHARMACY,
    PETSHOP
}

enum class PriorityLevel(val displayName: String, val color: String) {
    CRITICAL("Crítica", "#FF0000"),
    HIGH("Alta", "#FF9800"),
    MEDIUM("Média", "#FFEB3B"),
    LOW("Baixa", "#4CAF50");

    companion object {
        fun fromString(value: String): PriorityLevel {
            return when (value.lowercase()) {
                "critical", "crítica" -> CRITICAL
                "high", "alta" -> HIGH
                "medium", "média" -> MEDIUM
                "low", "baixa" -> LOW
                else -> LOW
            }
        }
    }
}

data class StatusCardData(
    val id: String = "",
    val title: String,
    val value: String = "0",
    val count: Int = 0, 
    val icon: Any,
    val iconBackground: String = "",
    val color: String = "", 
    val route: String
) {
    fun getCountValue(): Int = value.toIntOrNull() ?: count
}

data class QuickActionData(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val route: String,
    val background: String
)

data class RecentActivityData(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val icon: ImageVector,
    val iconBackground: String,
    val route: String? = null
)

data class ReminderData(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val priority: PriorityLevel,
    val icon: ImageVector,
    val iconBackground: String,
    val route: String? = null
)

interface DashboardDataProvider {
    fun getStatusCards(userType: UserType): List<StatusCardData>
    fun getQuickActions(userType: UserType): List<QuickActionData>
    fun getRecentActivities(userType: UserType): List<RecentActivityData>
    fun getReminders(userType: UserType): List<ReminderData>
    fun getGreeting(userType: UserType, userName: String): String
    fun getSubGreeting(userType: UserType, petCount: Int = 0): String
}