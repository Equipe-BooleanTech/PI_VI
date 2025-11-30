package edu.fatec.petwise.presentation.components.MoreMenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.features.auth.presentation.viewmodel.AuthViewModel
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.core.network.getPlatformName

data class MoreMenuItem(
    val title: String,
    val icon: ImageVector,
    val tabScreen: NavigationManager.TabScreen
)

@Composable
fun MoreMenu(
    isVisible: Boolean,
    navigationManager: NavigationManager,
    onClose: () -> Unit,
    authViewModel: AuthViewModel,
    getUserProfileUseCase: GetUserProfileUseCase
) {
    var userProfile by remember(isVisible) { mutableStateOf<edu.fatec.petwise.core.network.dto.UserProfileDto?>(null) }
    var isLoadingProfile by remember(isVisible) { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            userProfile = null
            isLoadingProfile = true
            getUserProfileUseCase.execute().fold(
                onSuccess = { profile ->
                    userProfile = profile
                    isLoadingProfile = false
                    println("MoreMenu - userProfile loaded: ${profile.fullName}, userType: ${profile.userType}")
                },
                onFailure = {
                    isLoadingProfile = false
                    println("MoreMenu - failed to load userProfile: ${it.message}")
                }
            )
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.fromHex("#00b942")),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuário",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userProfile?.fullName ?: "Carregando...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = userProfile?.email ?: "carregando...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }

                Text(
                    text = "Minha Conta",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                MoreMenuOption(
                    text = "Editar Perfil",
                    onClick = {
                        navigationManager.navigateToTab(NavigationManager.TabScreen.EditProfile)
                        onClose()
                    }
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                val menuItems = when (userProfile?.let { 
                    when (it.userType.uppercase()) {
                        "VETERINARY", "VETERINARIAN", "VET" -> "VETERINARY"
                        "PETSHOP" -> "PETSHOP"
                        "PHARMACY" -> "PHARMACY"
                        else -> "OWNER"
                    }
                }) {
                    "VETERINARY" -> listOf(
                        MoreMenuItem(
                            title = "Consultas",
                            icon = Icons.Default.MedicalInformation,
                            tabScreen = NavigationManager.TabScreen.Appointments
                        ),
                        MoreMenuItem(
                            title = "Prescrições",
                            icon = Icons.Default.LocalPharmacy,
                            tabScreen = NavigationManager.TabScreen.Prescriptions
                        ),
                        MoreMenuItem(
                            title = "Exames",
                            icon = Icons.Default.MedicalInformation,
                            tabScreen = NavigationManager.TabScreen.Exams
                        ),
                        MoreMenuItem(
                            title = "Laboratório",
                            icon = Icons.Default.MedicalInformation,
                            tabScreen = NavigationManager.TabScreen.Labs
                        ),
                    )
                    "PHARMACY" -> listOf(
                        MoreMenuItem(
                            title = "Medicamentos",
                            icon = Icons.Default.LocalPharmacy,
                            tabScreen = NavigationManager.TabScreen.Medication
                        )
                    )
                    "PETSHOP" -> listOf(
                        MoreMenuItem(
                            title = "Ração",
                            icon = Icons.Default.ShoppingCart,
                            tabScreen = NavigationManager.TabScreen.Food
                        ),
                        MoreMenuItem(
                            title = "Higiene",
                            icon = Icons.Default.MedicalInformation,
                            tabScreen = NavigationManager.TabScreen.Hygiene
                        ),
                        MoreMenuItem(
                            title = "Brinquedos",
                            icon = Icons.Default.ShoppingCart,
                            tabScreen = NavigationManager.TabScreen.Toys
                        )
                    )
                    else -> listOf(
                        MoreMenuItem(
                            title = "Pets",
                            icon = Icons.Default.Person,
                            tabScreen = NavigationManager.TabScreen.Pets
                        )
                    ).let { items ->
                        // Tag NFC is only available on Android and iOS (native platforms with NFC support)
                        val platform = getPlatformName()
                        if (platform == "Android" || platform == "iOS") {
                            items + MoreMenuItem(
                                title = "Tag NFC",
                                icon = Icons.Default.Nfc,
                                tabScreen = NavigationManager.TabScreen.PetTags
                            )
                        } else {
                            items
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    menuItems.forEach { item ->
                        MoreMenuItemRow(
                            item = item,
                            onClick = {
                                navigationManager.navigateToTab(item.tabScreen)
                                onClose()
                            }
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )


                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            authViewModel.logout()
                            navigationManager.navigateTo(NavigationManager.Screen.Auth)
                            onClose()
                        }
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Sair",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
}
            }
        }
    }

@Composable
fun MoreMenuItemRow(
    item: MoreMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.fromHex("#f2f2f2")),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun MoreMenuOption(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}