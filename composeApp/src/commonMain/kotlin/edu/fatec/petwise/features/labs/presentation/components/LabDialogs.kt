package edu.fatec.petwise.features.labs.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.presentation.forms.addLabFormConfiguration
import edu.fatec.petwise.features.labs.presentation.forms.createEditLabFormConfiguration
import edu.fatec.petwise.features.labs.di.LabDependencyContainer
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.coroutines.launch

@Composable
fun AddLabDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light

    val formConfiguration = addLabFormConfiguration

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_lab_form") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dialogHeight = with(LocalDensity.current) {
                (screenHeight.toPx() * 0.9f).toDp()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        color = Color.fromHex("#009688"),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Adicionar Laboratório",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            if (!isLoading) {
                                IconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fechar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.fromHex("#009688")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Adicionando laboratório...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.fromHex(theme.palette.textSecondary)
                                        )
                                    )
                                }
                            }
                        } else {
                            DynamicForm(
                                viewModel = formViewModel,
                                modifier = Modifier.fillMaxSize(),
                                onSubmitSuccess = { formData ->
                                    onSuccess(formData)
                                },
                                onSubmitError = { error ->
                                    println("Erro no formulário: ${error.message}")
                                }
                            )
                        }

                        errorMessage?.let { message ->
                            LaunchedEffect(message) {
                                kotlinx.coroutines.delay(3000)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditLabDialog(
    lab: Lab,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light

    val formConfiguration = createEditLabFormConfiguration(lab)

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_lab_form_${lab.id}") {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dialogHeight = with(LocalDensity.current) {
                (screenHeight.toPx() * 0.9f).toDp()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        color = Color.fromHex("#009688"),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Editar Laboratório",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            if (!isLoading) {
                                IconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fechar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.fromHex("#009688")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Atualizando laboratório...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.fromHex(theme.palette.textSecondary)
                                        )
                                    )
                                }
                            }
                        } else {
                            DynamicForm(
                                viewModel = formViewModel,
                                modifier = Modifier.fillMaxSize(),
                                onSubmitSuccess = { formData ->
                                    onSuccess(formData)
                                },
                                onSubmitError = { error ->
                                    println("Erro no formulário: ${error.message}")
                                }
                            )
                        }

                        errorMessage?.let { message ->
                            LaunchedEffect(message) {
                                kotlinx.coroutines.delay(3000)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteLabConfirmationDialog(
    labId: String,
    labName: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val theme = PetWiseTheme.Light

    AlertDialog(
        onDismissRequest = { if (!isLoading) onCancel() },
        title = {
            Text(
                text = "Confirmar Exclusão",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.fromHex(theme.palette.textPrimary)
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Tem certeza que deseja excluir o laboratório \"$labName\"?\n\nEsta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.fromHex(theme.palette.textSecondary)
                    )
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = LabDependencyContainer.deleteLabUseCase(labId)
                        result.fold(
                            onSuccess = {
                                onSuccess()
                            },
                            onFailure = { error ->
                                errorMessage = error.message ?: "Erro ao excluir laboratório"
                            }
                        )
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#F44336"),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Excluir")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isLoading
            ) {
                Text(
                    "Cancelar",
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}