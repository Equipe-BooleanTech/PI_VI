package edu.fatec.petwise.features.pettags.presentation.view

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.features.pettags.domain.models.TagScanStatus
import edu.fatec.petwise.features.pettags.presentation.viewmodel.*
import edu.fatec.petwise.features.pettags.di.PetTagDependencyContainer
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetTagScreen(
    navigationKey: Any? = null
) {
    val viewModel = remember { PetTagDependencyContainer.providePetTagViewModel() }
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navigationKey) {
        println("PetTagScreen: Carregando dados - navigationKey: $navigationKey")
        viewModel.onEvent(PetTagUiEvent.LoadPets)
    }

    
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.onEvent(PetTagUiEvent.ClearSuccess)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.fromHex("#F7F7F7"))
    ) {
        
        PetTagHeader(
            isPairingMode = uiState.isPairingMode,
            selectedPet = uiState.selectedPetForPairing,
            onSelectPetClick = { viewModel.onEvent(PetTagUiEvent.ShowPetSelectionDialog) },
            onStartPairingClick = { viewModel.onEvent(PetTagUiEvent.StartPairing) },
            onCancelPairingClick = { viewModel.onEvent(PetTagUiEvent.CancelPairing) },
            onManualInputClick = { viewModel.onEvent(PetTagUiEvent.ToggleManualInput) }
        )

        
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.fromHex(primaryColor)
                    )
                }
                uiState.isPairingMode -> {
                    PairingModeContent(
                        selectedPet = uiState.selectedPetForPairing,
                        onCancelClick = { viewModel.onEvent(PetTagUiEvent.CancelPairing) }
                    )
                }
                uiState.lastCheckInResult != null -> {
                    PetFoundContent(
                        result = uiState.lastCheckInResult!!,
                        scanStatus = uiState.scanStatus,
                        onScanAgainClick = { viewModel.onEvent(PetTagUiEvent.ResetState) }
                    )
                }
                else -> {
                    IdleContent(
                        showManualInput = uiState.showManualInput,
                        manualTagUid = uiState.manualTagUid,
                        onManualTagUidChange = { viewModel.onEvent(PetTagUiEvent.UpdateManualTagUid(it)) },
                        onSubmitManualTag = { viewModel.onEvent(PetTagUiEvent.SubmitManualTag) },
                        onToggleManualInput = { viewModel.onEvent(PetTagUiEvent.ToggleManualInput) }
                    )
                }
            }

            
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                uiState.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#FFE0E0")
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.fromHex("#D32F2F")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                color = Color.fromHex("#D32F2F"),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.onEvent(PetTagUiEvent.ClearError) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = Color.fromHex("#D32F2F")
                                )
                            }
                        }
                    }
                }
            }

            
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.successMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                uiState.successMessage?.let { success ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.fromHex("#E8F5E9")
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.fromHex("#388E3C")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = success,
                                color = Color.fromHex("#388E3C"),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.onEvent(PetTagUiEvent.ClearSuccess) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = Color.fromHex("#388E3C")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    
    if (uiState.showPetSelectionDialog) {
        PetSelectionDialog(
            pets = uiState.pets,
            isLoading = uiState.isLoading,
            onPetSelected = { pet -> viewModel.onEvent(PetTagUiEvent.SelectPetForPairing(pet)) },
            onDismiss = { viewModel.onEvent(PetTagUiEvent.HidePetSelectionDialog) }
        )
    }
}

@Composable
private fun PetTagHeader(
    isPairingMode: Boolean,
    selectedPet: Pet?,
    onSelectPetClick: () -> Unit,
    onStartPairingClick: () -> Unit,
    onCancelPairingClick: () -> Unit,
    onManualInputClick: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.fromHex(primaryColor),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tag NFC / RFID",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isPairingMode) "Modo de pareamento ativo" else "Escaneie ou parear tags",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                
                IconButton(
                    onClick = onManualInputClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.Keyboard,
                        contentDescription = "Entrada manual"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isPairingMode) {
                
                OutlinedButton(
                    onClick = onSelectPetClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedPet?.name ?: "Selecionar pet para parear",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                if (selectedPet != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onStartPairingClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.fromHex(primaryColor)
                        )
                    ) {
                        Icon(Icons.Default.Nfc, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Pareamento")
                    }
                }
            } else {
                
                OutlinedButton(
                    onClick = onCancelPairingClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar Pareamento")
                }
            }
        }
    }
}

@Composable
private fun PairingModeContent(
    selectedPet: Pet?,
    onCancelClick: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary
    
    
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.fromHex(primaryColor).copy(alpha = alpha * 0.2f))
                .border(3.dp, Color.fromHex(primaryColor).copy(alpha = alpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Nfc,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.fromHex(primaryColor)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Modo de Pareamento Ativo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.fromHex(primaryColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aproxime a tag NFC do dispositivo",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        selectedPet?.let { pet ->
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex("#E3F2FD")
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color.fromHex(primaryColor),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Pareando tag para:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = pet.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.fromHex(primaryColor)
                        )
                        Text(
                            text = "${pet.species.displayName} • ${pet.breed}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "A tag será associada automaticamente quando detectada",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PetFoundContent(
    result: edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult,
    scanStatus: TagScanStatus,
    onScanAgainClick: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary
    val isNewRegistration = scanStatus == TagScanStatus.TAG_REGISTERED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    if (isNewRegistration) Color.fromHex("#E8F5E9") else Color.fromHex("#E3F2FD")
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isNewRegistration) Icons.Default.CheckCircle else Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = if (isNewRegistration) Color.fromHex("#388E3C") else Color.fromHex(primaryColor)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isNewRegistration) "Tag Registrada!" else "Pet Identificado",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNewRegistration) Color.fromHex("#388E3C") else Color.fromHex(primaryColor)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                InfoRow(label = "Nome", value = result.petName)
                result.species?.let { InfoRow(label = "Espécie", value = it) }
                result.ownerName?.let { InfoRow(label = "Dono", value = it) }
                result.ownerPhone?.let { InfoRow(label = "Telefone", value = it) }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = result.message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onScanAgainClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.fromHex(primaryColor)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Escanear Outra Tag")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun IdleContent(
    showManualInput: Boolean,
    manualTagUid: String,
    onManualTagUidChange: (String) -> Unit,
    onSubmitManualTag: () -> Unit,
    onToggleManualInput: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Nfc,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nenhuma tag detectada",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aproxime uma tag NFC ou selecione um pet para parear uma nova tag",
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        if (showManualInput) {
            Column(
                modifier = Modifier.padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = manualTagUid,
                    onValueChange = onManualTagUidChange,
                    label = { Text("UID da Tag") },
                    placeholder = { Text("Ex: 04:A3:B2:C1:D0:E9") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.fromHex(primaryColor),
                        focusedLabelColor = Color.fromHex(primaryColor)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSubmitManualTag,
                    enabled = manualTagUid.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.fromHex(primaryColor)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar Tag")
                }
            }
        }

        if (!showManualInput) {
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = onToggleManualInput) {
                Icon(Icons.Default.Keyboard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inserir manualmente")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetSelectionDialog(
    pets: List<Pet>,
    isLoading: Boolean,
    onPetSelected: (Pet) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Selecionar Pet",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.fromHex(primaryColor)
                        )
                    }
                    pets.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Pets,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhum pet encontrado",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        LazyColumn {
                            items(pets) { pet ->
                                PetSelectionItem(
                                    pet = pet,
                                    onClick = { onPetSelected(pet) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PetSelectionItem(
    pet: Pet,
    onClick: () -> Unit
) {
    val theme = PetWiseTheme.Light
    val primaryColor = theme.palette.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.fromHex(primaryColor).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.fromHex(primaryColor)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = "${pet.species.displayName} • ${pet.breed}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
