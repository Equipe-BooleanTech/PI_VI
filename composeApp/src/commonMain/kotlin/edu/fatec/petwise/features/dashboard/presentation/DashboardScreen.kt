package edu.fatec.petwise.features.dashboard.presentation

import edu.fatec.petwise.features.dashboard.di.DashboardDepedencyContainer
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardUiEvent
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardUiState
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.consultas.presentation.view.ConsultasScreen
import edu.fatec.petwise.features.dashboard.domain.models.DashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.DefaultDashboardDataProvider
import edu.fatec.petwise.features.dashboard.domain.models.UserType
import edu.fatec.petwise.features.medications.presentation.view.MedicationsScreen
import edu.fatec.petwise.features.pets.presentation.view.PetsScreen
import edu.fatec.petwise.features.vaccinations.di.VaccinationDependencyContainer
import edu.fatec.petwise.features.vaccinations.presentation.view.VaccinationsScreen
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.components.BottomNavigation.BottomNavigationBar
import edu.fatec.petwise.presentation.components.MoreMenu.MoreMenu
import edu.fatec.petwise.presentation.components.NavBar.NavBar
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun DashboardScreen(
    navigationManager: NavigationManager,
    userName: String = "",
    userType: UserType = UserType.OWNER,
    dataProvider: DashboardDataProvider = DefaultDashboardDataProvider()
) {
    val authViewModel = remember { AuthDependencyContainer.provideAuthViewModel() }
    val getUserProfileUseCase = remember { AuthDependencyContainer.provideGetUserProfileUseCase() }
    val dashboardViewModel = remember { DashboardDepedencyContainer.provideDashboardViewModel() }
    
    val theme = PetWiseTheme.Light
    val scrollState = rememberScrollState()

    val currentTabScreen by navigationManager.currentTabScreen.collectAsState()
    val showMoreMenu by navigationManager.showMoreMenu.collectAsState()
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        println("DashboardScreen: Iniciando carregamento de dados")
        dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
    }

    LaunchedEffect(currentTabScreen) {
        if (currentTabScreen == NavigationManager.TabScreen.Home) {
            println("DashboardScreen: Retornando para Home, atualizando contagens")
            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
        }
    }

    LaunchedEffect(dashboardUiState.errorMessage) {
        val errorMsg = dashboardUiState.errorMessage
        if (errorMsg != null) {
            val isAuthError = errorMsg.contains("sessão expirou", ignoreCase = true) ||
                    errorMsg.contains("Faça login", ignoreCase = true) ||
                    errorMsg.contains("unauthorized", ignoreCase = true) ||
                    errorMsg.contains("401", ignoreCase = true) ||
                    errorMsg.contains("Token expirado", ignoreCase = true)
            
            if (isAuthError) {
                println("DashboardScreen: Erro de autenticação detectado, redirecionando para login - $errorMsg")
                authViewModel.handleSessionExpired(errorMsg)
                navigationManager.navigateTo(NavigationManager.Screen.Auth)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
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
                        userName = dashboardUiState.userName.ifEmpty { userName },
                        dataProvider = dataProvider,
                        navigationManager = navigationManager,
                        dashboardUiState = dashboardUiState,
                        onRefresh = {
                            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
                        }
                    )
                }
                NavigationManager.TabScreen.Pets -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        PetsScreen(navigationKey = currentTabScreen)
                    }
                }
                NavigationManager.TabScreen.Appointments -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        ConsultasScreen(navigationKey = currentTabScreen)
                    }
                }
                NavigationManager.TabScreen.Medication -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        MedicationsScreen(
                            navigationKey = currentTabScreen,
                            canAddMedications = userType != UserType.OWNER,
                            canEditMedications = userType != UserType.OWNER
                        )
                    }
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        VaccinationsScreen(
                            viewModel = remember { VaccinationDependencyContainer.provideVaccinationsViewModel() },
                            navigationKey = currentTabScreen,
                            canAddVaccinations = userType != UserType.OWNER
                        )
                    }
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
                        navigationManager = navigationManager,
                        dashboardUiState = dashboardUiState,
                        onRefresh = {
                            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
                        }
                    )
                }
            }
        }

        MoreMenu(
            isVisible = showMoreMenu,
            navigationManager = navigationManager,
            onClose = { navigationManager.hideMoreMenu() },
            authViewModel = authViewModel,
            getUserProfileUseCase = getUserProfileUseCase
        )
    }
}

@Composable
fun HomeTabContent(
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    userType: UserType,
    userName: String,
    dataProvider: DashboardDataProvider,
    navigationManager: NavigationManager,
    dashboardUiState: DashboardUiState,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF7F7F7))
    ) {
        if (dashboardUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.fromHex("#00b942")
                )
            }
        } else if (dashboardUiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Erro ao carregar dados",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dashboardUiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.fromHex("#00b942")
                        )
                    ) {
                        Text("Tentar novamente")
                    }
                }
            }
        } else {
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
                            text = dataProvider.getSubGreeting(userType, dashboardUiState.petCount),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                StatusCardsSection(
                    userType = userType,
                    petCount = dashboardUiState.petCount,
                    consultasCount = dashboardUiState.consultasCount,
                    vacinasCount = dashboardUiState.vacinasCount,
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
                    upcomingConsultas = dashboardUiState.upcomingConsultas,
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
            }
        }
    }
}

@Composable
fun PlaceholderContent(
    paddingValues: PaddingValues,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF7F7F7)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
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