package edu.fatec.petwise.features.dashboard.presentation

import edu.fatec.petwise.features.dashboard.di.DashboardDepedencyContainer
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardUiEvent
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardUiState
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import edu.fatec.petwise.features.exams.di.ExamDependencyContainer
import edu.fatec.petwise.features.medications.presentation.view.MedicationsScreen
import edu.fatec.petwise.features.pets.presentation.view.PetsScreen
import edu.fatec.petwise.features.suprimentos.presentation.view.SuprimentosPetSelectionScreen
import edu.fatec.petwise.features.suprimentos.presentation.view.SuprimentosScreen
import edu.fatec.petwise.features.vaccinations.di.VaccinationDependencyContainer
import edu.fatec.petwise.features.vaccinations.presentation.view.VaccinationsScreen
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.components.BottomNavigation.BottomNavigationBar
import edu.fatec.petwise.presentation.components.MoreMenu.MoreMenu
import edu.fatec.petwise.presentation.components.NavBar.NavBar
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import edu.fatec.petwise.features.dashboard.presentation.StatusCardsSection
import edu.fatec.petwise.features.dashboard.presentation.QuickActionsSection
import edu.fatec.petwise.features.dashboard.presentation.RecentActivitiesSection
import edu.fatec.petwise.features.dashboard.presentation.UnauthorizedScreen


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
    
    var selectedPetIdForSuprimentos by remember { mutableStateOf<String?>(null) }
    var currentUserType by remember { mutableStateOf(userType) }
    var currentUserName by remember { mutableStateOf(userName) }

    LaunchedEffect(Unit) {
        println("DashboardScreen: Fetching user profile directly")
        getUserProfileUseCase.execute().fold(
            onSuccess = { userProfile ->
                currentUserName = userProfile.fullName
                currentUserType = when (userProfile.userType.uppercase()) {
                    "VETERINARY", "VETERINARIAN", "VET" -> UserType.VETERINARY
                    "PETSHOP" -> UserType.PETSHOP
                    "PHARMACY" -> UserType.PHARMACY
                    else -> UserType.OWNER
                }
                println("DashboardScreen: UserType fetched successfully: ${userProfile.userType} -> $currentUserType")
            },
            onFailure = { error ->
                println("DashboardScreen: Failed to fetch user profile: ${error.message}")
                currentUserType = UserType.OWNER
                currentUserName = userName
            }
        )
        
        println("DashboardScreen: Iniciando carregamento de dados")
        dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
    }

    LaunchedEffect(currentTabScreen) {
        if (currentTabScreen == NavigationManager.TabScreen.Home) {
            println("DashboardScreen: Retornando para Home, atualizando contagens")
            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
        }
        if (currentTabScreen != NavigationManager.TabScreen.Suprimentos) {
            selectedPetIdForSuprimentos = null
        }
    }

    LaunchedEffect(dashboardUiState.errorMessage) {
        val errorMsg = dashboardUiState.errorMessage
        if (errorMsg != null) {
            println("DashboardScreen: Analisando mensagem de erro: '$errorMsg'")
            
            
            val isSessionExpired = errorMsg.contains("sessão expirou", ignoreCase = true) ||
                    errorMsg.contains("Faça login novamente", ignoreCase = true) ||
                    errorMsg.contains("Token expirado - faça login novamente", ignoreCase = true) ||
                    errorMsg.contains("Sessão inválida", ignoreCase = true)
            
            
            val isGenericAuthError = (errorMsg.contains("unauthorized", ignoreCase = true) ||
                    errorMsg.contains("401", ignoreCase = true)) &&
                    !errorMsg.contains("Erro temporário", ignoreCase = true)
            
            when {
                isSessionExpired -> {
                    println("DashboardScreen: Sessão expirada detectada, fazendo logout - $errorMsg")
                    authViewModel.handleSessionExpired(errorMsg)
                    navigationManager.navigateTo(NavigationManager.Screen.Auth)
                }
                isGenericAuthError -> {
                    println("DashboardScreen: Erro de autenticação genérico detectado - aguardando antes de logout automático - $errorMsg")
                    
                    
                    if (dashboardUiState.errorMessage == errorMsg) {
                        println("DashboardScreen: Erro de autenticação persistiu após delay, fazendo logout - $errorMsg")
                        authViewModel.handleSessionExpired("Problema de autenticação persistente. Faça login novamente.")
                        navigationManager.navigateTo(NavigationManager.Screen.Auth)
                    } else {
                        println("DashboardScreen: Erro de autenticação foi resolvido durante delay - não fazendo logout")
                    }
                }
                else -> {
                    println("DashboardScreen: Erro não relacionado à autenticação ignorado - $errorMsg")
                }
            }
        }
    }

    println("DashboardScreen: Renderizando UI - currentTabScreen=$currentTabScreen, userType=$currentUserType, isLoading=${dashboardUiState.isLoading}, errorMessage=${dashboardUiState.errorMessage}")
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
                BottomNavigationBar(
                    navigationManager = navigationManager
                )
            }
        ) { paddingValues ->
            when (currentTabScreen) {
                NavigationManager.TabScreen.Home -> {
                    val effectiveUserType = currentUserType
                    
                    HomeTabContent(
                        paddingValues = paddingValues,
                        scrollState = scrollState,
                        userType = effectiveUserType,
                        userName = currentUserName.ifEmpty { userName },
                        dataProvider = dataProvider,
                        navigationManager = navigationManager,
                        dashboardUiState = dashboardUiState,
                        onRefresh = {
                            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
                        },
                        onCancelActivity = { activityId ->
                            dashboardViewModel.onEvent(DashboardUiEvent.CancelConsulta(activityId))
                        }
                    )
                }
                NavigationManager.TabScreen.Pets -> {
                    if (currentUserType == UserType.OWNER) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            PetsScreen(
                                navigationKey = currentTabScreen
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Pets")
                    }
                }
                NavigationManager.TabScreen.Appointments -> {
                    if (currentUserType in listOf(UserType.OWNER, UserType.VETERINARY)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            ConsultasScreen(
                                navigationKey = currentTabScreen
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Consultas")
                    }
                }
                NavigationManager.TabScreen.Medication -> {
                    if (currentUserType == UserType.PHARMACY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            MedicationsScreen(
                                navigationKey = currentTabScreen
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Medicamentos")
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
                    if (currentUserType == UserType.VETERINARY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            VaccinationsScreen(
                                viewModel = remember { VaccinationDependencyContainer.provideVaccinationsViewModel() },
                                navigationKey = currentTabScreen
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Vacinas")
                    }
                }
                NavigationManager.TabScreen.Suprimentos -> {
                    if (selectedPetIdForSuprimentos != null) {
                        SuprimentosScreen(
                            petId = selectedPetIdForSuprimentos!!,
                            navigationKey = currentTabScreen
                        )
                    } else {
                        SuprimentosPetSelectionScreen(
                            onPetSelected = { petId ->
                                selectedPetIdForSuprimentos = petId
                            },
                            onBackClick = {
                                navigationManager.navigateToTab(NavigationManager.TabScreen.Home)
                            }
                        )
                    }
                }
                NavigationManager.TabScreen.Labs -> {
                    if (currentUserType == UserType.VETERINARY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.labs.presentation.LabsScreen(
                                viewModel = edu.fatec.petwise.features.labs.di.LabDependencyContainer.labsViewModel
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Laboratório")
                    }
                }
                NavigationManager.TabScreen.Prescriptions -> {
                    if (currentUserType == UserType.VETERINARY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.prescriptions.presentation.PrescriptionsScreen()
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Prescrições")
                    }
                }
                NavigationManager.TabScreen.Exams -> {
                    if (currentUserType == UserType.VETERINARY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.exams.presentation.ExamsScreen(
                                viewModel = ExamDependencyContainer.examsViewModel
                            )
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Exames")
                    }
                }
                NavigationManager.TabScreen.Food -> {
                    if (currentUserType == UserType.PETSHOP) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.food.presentation.FoodScreen()
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Ração")
                    }
                }
                NavigationManager.TabScreen.Hygiene -> {
                    if (currentUserType == UserType.PETSHOP) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.hygiene.presentation.HygieneScreen()
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Higiene")
                    }
                }
                NavigationManager.TabScreen.Toys -> {
                    if (currentUserType == UserType.PETSHOP) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            edu.fatec.petwise.features.toys.presentation.ToysScreen()
                        }
                    } else {
                        UnauthorizedScreen(paddingValues, "Você não tem permissão para acessar Brinquedos")
                    }
                }
                NavigationManager.TabScreen.PetTags -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        edu.fatec.petwise.features.pettags.presentation.view.PetTagScreen(
                            navigationKey = currentTabScreen
                        )
                    }
                }
                NavigationManager.TabScreen.EditProfile -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        edu.fatec.petwise.features.profile.presentation.view.EditProfileScreen(
                            navigationManager = navigationManager
                        )
                    }
                }
                else -> {
                    val effectiveUserType = currentUserType
                    
                    HomeTabContent(
                        paddingValues = paddingValues,
                        scrollState = scrollState,
                        userType = effectiveUserType,
                        userName = currentUserName.ifEmpty { userName },
                        dataProvider = dataProvider,
                        navigationManager = navigationManager,
                        dashboardUiState = dashboardUiState,
                        onRefresh = {
                            dashboardViewModel.onEvent(DashboardUiEvent.RefreshDashboard)
                        },
                        onCancelActivity = { activityId ->
                            dashboardViewModel.onEvent(DashboardUiEvent.CancelConsulta(activityId))
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
    onRefresh: () -> Unit,
    onCancelActivity: (String) -> Unit
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
                    userType = userType.name,
                    petCount = dashboardUiState.petCount,
                    consultasCount = dashboardUiState.consultasCount,
                    vacinasCount = dashboardUiState.vacinasCount,
                    medicamentosCount = dashboardUiState.medicamentosCount,
                    prescriptionsCount = dashboardUiState.prescriptionsCount,
                    examsCount = dashboardUiState.examsCount,
                    labsCount = dashboardUiState.labsCount,
                    foodCount = dashboardUiState.foodCount,
                    hygieneCount = dashboardUiState.hygieneCount,
                    toysCount = dashboardUiState.toysCount,
                    onCardClick = { route ->
                        when(route) {
                            "pets" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Pets)
                            "appointments" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Appointments)
                            "vaccines" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Vaccines)
                            "medications" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Medication)
                            "prescriptions" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Prescriptions)
                            "exams" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Exams)
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
                            "prescriptions" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Prescriptions)
                            "exams" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Exams)
                            "labs" -> navigationManager.navigateToTab(NavigationManager.TabScreen.Labs)
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
                    onCancelActivity = onCancelActivity,
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