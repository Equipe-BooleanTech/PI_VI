package edu.fatec.petwise.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.DefaultDashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.components.BottomNavigation.BottomNavigationBar
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun DashboardScreen(
    navigationManager: NavigationManager,
    userName: String = "João",
    userType: UserType = UserType.OWNER,
    dataProvider: DashboardDataProvider = DefaultDashboardDataProvider()
) {
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    val scrollState = rememberScrollState()
    var selectedRoute by remember { mutableStateOf("home") }
    
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBar(
                onItemSelected = { route -> 
                    selectedRoute = route
                    // Handle navigation in a real app
                },
                selectedRoute = selectedRoute
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7)) // Light gray background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 0.dp)
            ) {
                // Header - Greeting in a green card as shown in design
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.fromHex("#00b942") // Green card
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = dataProvider.getGreeting(userType, userName),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        
                        Text(
                            text = dataProvider.getSubGreeting(userType),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
                
                // Status Cards Section
                StatusCardsSection(
                    userType = userType,
                    dataProvider = dataProvider,
                    onCardClick = { route ->
                        // Handle navigation
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quick Actions Section
                QuickActionsSection(
                    userType = userType,
                    dataProvider = dataProvider,
                    onActionClick = { route ->
                        // Handle navigation
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recent Activities Section
                RecentActivitiesSection(
                    userType = userType,
                    dataProvider = dataProvider,
                    onActivityClick = { route ->
                        // Handle navigation
                    },
                    onViewAllClick = {
                        // Handle view all navigation
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Reminders Section
                RemindersSection(
                    userType = userType,
                    dataProvider = dataProvider,
                    onReminderClick = { route ->
                        // Handle navigation
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}