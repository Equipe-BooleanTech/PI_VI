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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.DefaultDashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.pets.presentation.view.PetsScreen
import edu.fatec.petwise.features.consultas.presentation.view.ConsultasScreen
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.components.BottomNavigation.BottomNavigationBar
import edu.fatec.petwise.presentation.components.MoreMenu.MoreMenu
import edu.fatec.petwise.presentation.components.NavBar.NavBar
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun DashboardScreen(
    navigationManager: NavigationManager,
    userName: String = "João",
    userType: UserType = UserType.OWNER,
    dataProvider: DashboardDataProvider = DefaultDashboardDataProvider()
) {
    val theme = PetWiseTheme.Light
    val scrollState = rememberScrollState()

    val currentTabScreen by navigationManager.currentTabScreen.collectAsState()
    val showMoreMenu by navigationManager.showMoreMenu.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                NavBar(
                    title = "PetWise",
                    navigationManager = navigationManager
                )
            },
            bottomBar = {
                BottomNavigationBar(navigationManager = navigationManager)
            }
        ) { paddingValues ->
            when (currentTabScreen) {
                NavigationManager.TabScreen.Home -> {
                    HomeTabContent(
                        paddingValues = paddingValues,
                        scrollState = scrollState,
                        userType = userType,
                        userName = userName,
                        dataProvider = dataProvider,
                        navigationManager = navigationManager
                    )
                }
                NavigationManager.TabScreen.Pets -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        PetsScreen()
                    }
                }
                NavigationManager.TabScreen.Appointments -> {
                   Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        ConsultasScreen()
                    }
                }
                NavigationManager.TabScreen.Medication -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Medicações"
                    )
                }
                NavigationManager.TabScreen.Settings -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Configurações"
                    )
                }
                NavigationManager.TabScreen.Help -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Ajuda"
                    )
                }
                NavigationManager.TabScreen.Vaccines -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Vacinas"
                    )
                }
                NavigationManager.TabScreen.Veterinarians -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Veterinários"
                    )
                }
                NavigationManager.TabScreen.Supplies -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Suprimentos"
                    )
                }
                NavigationManager.TabScreen.Pharmacy -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Farmácias"
                    )
                }
                NavigationManager.TabScreen.Labs -> {
                    PlaceholderContent(
                        paddingValues = paddingValues,
                        title = "Exames"
                    )
                }
                else -> {
                    HomeTabContent(
                        paddingValues = paddingValues,
                        scrollState = scrollState,
                        userType = userType,
                        userName = userName,
                        dataProvider = dataProvider,
                        navigationManager = navigationManager
                    )
                }
            }
        }

        MoreMenu(
            isVisible = showMoreMenu,
            navigationManager = navigationManager,
            onClose = { navigationManager.hideMoreMenu() }
        )
    }
}

@Composable
fun HomeTabContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    scrollState: androidx.compose.foundation.ScrollState,
    userType: UserType,
    userName: String,
    dataProvider: DashboardDataProvider,
    navigationManager: NavigationManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 0.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex("#00b942")
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

            StatusCardsSection(
                userType = userType,
                dataProvider = dataProvider,
                onCardClick = { route ->
                    when(route) {
                        "pets" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Pets)
                        "appointments" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Appointments)
                        "vaccines" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Vaccines)
                        "reminders" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Medication)
                        else -> { }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuickActionsSection(
                userType = userType,
                dataProvider = dataProvider,
                onActionClick = { route ->
                    when(route) {
                        "pets" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Pets)
                        "appointments" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Appointments)
                        "medications" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Medication)
                        "vaccines" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Vaccines)
                        else -> { }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecentActivitiesSection(
                userType = userType,
                dataProvider = dataProvider,
                onActivityClick = { route ->
                    if (route?.contains("appointments") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Appointments)
                    } else if (route?.contains("vaccines") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Vaccines)
                    } else if (route?.contains("pets") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Pets)
                    }
                },
                onViewAllClick = {
                    navigationManager.navigateToTab(NavigationManager.TabScreen.Home)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            RemindersSection(
                userType = userType,
                dataProvider = dataProvider,
                onReminderClick = { route ->
                    if (route?.contains("appointments") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Appointments)
                    } else if (route?.contains("vaccines") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Vaccines)
                    } else if (route?.contains("medications") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Medication)
                    } else if (route?.contains("reminders") == true) {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.Medication)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlaceholderContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF7F7F7)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Em desenvolvimento",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}