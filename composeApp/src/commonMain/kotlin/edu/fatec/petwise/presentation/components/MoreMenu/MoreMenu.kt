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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    currentEmail: String = "cliente@petwise.com"
) {
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
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp)
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
                            text = "Usuário PetWise",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = currentEmail,
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
                    onClick = { navigationManager.navigateToTab(NavigationManager.TabScreen.Settings) }
                )

                MoreMenuOption(
                    text = "Configurações",
                    onClick = { navigationManager.navigateToTab(NavigationManager.TabScreen.Settings) }
                )

                MoreMenuOption(
                    text = "Notificações",
                    onClick = { navigationManager.navigateToTab(NavigationManager.TabScreen.Settings) }
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                val menuItems = listOf(
                    MoreMenuItem(
                        title = "Vacinas",
                        icon = Icons.Default.HealthAndSafety,
                        tabScreen = NavigationManager.TabScreen.Vaccines
                    ),
                    MoreMenuItem(
                        title = "Veterinários",
                        icon = Icons.Default.MedicalInformation,
                        tabScreen = NavigationManager.TabScreen.Veterinarians
                    ),
                    MoreMenuItem(
                        title = "Suprimentos",
                        icon = Icons.Default.ShoppingCart,
                        tabScreen = NavigationManager.TabScreen.Supplies
                    ),
                    MoreMenuItem(
                        title = "Farmácias",
                        icon = Icons.Default.LocalPharmacy,
                        tabScreen = NavigationManager.TabScreen.Pharmacy
                    ),
                    MoreMenuItem(
                        title = "Exames",
                        icon = Icons.Default.MedicalInformation,
                        tabScreen = NavigationManager.TabScreen.Labs
                    )
                )

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

                Text(
                    text = "Suporte",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                MoreMenuOption(
                    text = "Ajuda",
                    onClick = { navigationManager.navigateToTab(NavigationManager.TabScreen.Help) }
                )

                MoreMenuOption(
                    text = "Contato",
                    onClick = { navigationManager.navigateToTab(NavigationManager.TabScreen.Help) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
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