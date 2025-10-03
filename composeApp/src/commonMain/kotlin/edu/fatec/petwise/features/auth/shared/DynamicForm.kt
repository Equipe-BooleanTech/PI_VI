package edu.fatec.petwise.features.auth.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicAuthFormScreen(
    formStore: FormStore,
    modifier: Modifier = Modifier
) {
    val uiState by formStore.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.fields.forEach { fs ->
            if (fs.visible) {
                val fieldDef = formStore.schema.fields.first { it.id == fs.id }

                when (fieldDef.type) {
                    "text", "email" -> {
                        OutlinedTextField(
                            value = fs.value,
                            onValueChange = { formStore.updateValue(fs.id, it) },
                            label = { Text(fieldDef.label ?: "") },
                            placeholder = { Text(fieldDef.placeholder ?: "") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.fromHex(theme.palette.primary),
                                unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                focusedLabelColor = Color.fromHex(theme.palette.primary),
                                unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                errorBorderColor = Color.fromHex("#ff0000")
                            ),
                            isError = fs.errors.isNotEmpty(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    "password" -> {
                        var passwordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = fs.value,
                            onValueChange = { formStore.updateValue(fs.id, it) },
                            label = { Text(fieldDef.label ?: "") },
                            placeholder = { Text(fieldDef.placeholder ?: "") },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Filled.Visibility
                                        else
                                            Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            "Ocultar senha"
                                        else
                                            "Mostrar senha",
                                        tint = Color.fromHex(theme.palette.textSecondary)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.fromHex(theme.palette.primary),
                                unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                focusedLabelColor = Color.fromHex(theme.palette.primary),
                                unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                errorBorderColor = Color.fromHex("#ff0000")
                            ),
                            isError = fs.errors.isNotEmpty(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    "segmented" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            fieldDef.options?.forEach { opt ->
                                val selected = fs.value.ifBlank {
                                    fieldDef.default?.jsonPrimitive?.content ?: ""
                                } == opt
                                ElevatedButton(
                                    onClick = { formStore.updateValue(fs.id, opt) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selected)
                                            Color.fromHex(theme.palette.primary)
                                        else
                                            Color.White,
                                        contentColor = if (selected)
                                            Color.White
                                        else
                                            Color.fromHex(theme.palette.textPrimary)
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(opt)
                                }
                            }
                        }
                    }

                    "select" -> {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = fs.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(fieldDef.label ?: "") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.fromHex(theme.palette.primary),
                                    unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedLabelColor = Color.fromHex(theme.palette.primary),
                                    unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                fieldDef.options?.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            formStore.updateValue(fs.id, option)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    "buttonGroup" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            fieldDef.options?.forEach { opt ->
                                OutlinedButton(
                                    onClick = { formStore.updateValue("demo_roles", opt) },
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.fromHex(theme.palette.primary)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            Color.fromHex(theme.palette.primary)
                                        )
                                    )
                                ) {
                                    Text(opt)
                                }
                            }
                        }
                    }
                }

                if (fs.errors.isNotEmpty()) {
                    Text(
                        text = fs.errors.first(),
                        color = Color.fromHex("#d32f2f"),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    formStore.submit { success, errors ->
                        // handle success/failure
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.fromHex(theme.palette.primary),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Entrar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}