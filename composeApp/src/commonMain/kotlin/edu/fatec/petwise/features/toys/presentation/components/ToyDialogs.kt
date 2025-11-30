package edu.fatec.petwise.features.toys.presentation.components

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
import edu.fatec.petwise.features.toys.domain.models.Toy
import edu.fatec.petwise.features.toys.presentation.forms.addToyFormConfiguration
import edu.fatec.petwise.features.toys.presentation.forms.createEditToyFormConfiguration
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun AddToyDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light

    val formConfiguration = addToyFormConfiguration

    val formViewModel = viewModel<DynamicFormViewModel>(key = "add_toy_form") {
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
                        color = Color.fromHex("#FF9800"),
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
                                text = "Adicionar Brinquedo",
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
                                        color = Color.fromHex("#FF9800")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Adicionando brinquedo...",
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
                                    
                                    formViewModel.resetForm()
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
fun EditToyDialog(
    toy: Toy,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSuccess: (Map<String, Any>) -> Unit = {}
) {
    val theme = PetWiseTheme.Light

    val formConfiguration = createEditToyFormConfiguration(toy)

    val formViewModel = viewModel<DynamicFormViewModel>(key = "edit_toy_form_${toy.id}") {
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
                        color = Color.fromHex("#FF9800"),
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
                                text = "Editar Brinquedo",
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
                                        color = Color.fromHex("#FF9800")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Atualizando brinquedo...",
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
                                    
                                    formViewModel.resetForm()
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
fun DeleteToyConfirmationDialog(
    toyId: String,
    toyName: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val theme = PetWiseTheme.Light

    AlertDialog(
        onDismissRequest = onCancel,
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
            Text(
                text = "Tem certeza que deseja excluir o brinquedo \"$toyName\"?\n\nEsta ação não pode ser desfeita.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.fromHex(theme.palette.textSecondary)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onSuccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex("#F44336"),
                    contentColor = Color.White
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
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