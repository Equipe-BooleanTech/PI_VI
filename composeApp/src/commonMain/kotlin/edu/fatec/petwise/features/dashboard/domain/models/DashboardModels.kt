package edu.fatec.petwise.features.dashboard.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserType {
    OWNER,
    VET,
    ADMIN
}

enum class PriorityLevel {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

data class StatusCardData(
    val id: String,
    val title: String,
    val count: Int,
    val icon: ImageVector,
    val iconBackground: String,
    val route: String
)

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
    fun getSubGreeting(userType: UserType): String
}