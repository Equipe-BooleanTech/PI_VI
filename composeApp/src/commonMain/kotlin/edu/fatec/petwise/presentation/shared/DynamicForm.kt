package edu.fatec.petwise.features.auth.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicAuthFormScreen(
    formStore: FormStore,
    modifier: Modifier = Modifier,
    onLoginSuccess: (() -> Unit)? = null
) {
    val uiState by formStore.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val theme = if (isSystemInDarkTheme()) PetWiseTheme.Dark else PetWiseTheme.Light
    
    BoxWithConstraints {
        val screenWidth = maxWidth
        val fieldSpacing = when {
            screenWidth < 400.dp -> 12.dp
            screenWidth < 600.dp -> 16.dp
            else -> 20.dp
        }
        
        val fieldHeight = when {
            screenWidth < 400.dp -> 52.dp
            else -> 56.dp
        }
        
        val buttonHeight = when {
            screenWidth < 400.dp -> 44.dp
            screenWidth < 600.dp -> 48.dp
            else -> 52.dp
        }
        
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(fieldSpacing)
        ) {
            uiState.fields.forEach { fs ->
                val fieldDef = formStore.schema.fields.find { it.id == fs.id } ?: return@forEach

                if (fs.visible) {
                    when (fieldDef.type) {
                        "text", "email" -> {
                            val keyboardType = when {
                                fieldDef.type == "email" -> KeyboardType.Email
                                fieldDef.id == "phone" -> KeyboardType.Phone
                                fieldDef.id == "cpf" || fieldDef.id == "cnpj" || fieldDef.id == "cep" || 
                                fieldDef.id == "crmv" -> KeyboardType.Number
                                else -> KeyboardType.Text
                            }
                            
                            val mask = fieldDef.mask?.let { maskName -> 
                                InputMasks.MASK_MAP[maskName]
                            }
                            
                            if (mask != null) {
                                var textFieldValue by remember { mutableStateOf(TextFieldValue(fs.value)) }
                                
                                OutlinedTextField(
                                    value = textFieldValue,
                                    onValueChange = { newValue: TextFieldValue -> 
                                        val processedValue = InputMasks.processTextWithMask(
                                            textFieldValue, 
                                            newValue.text,
                                            mask
                                        )
                                        textFieldValue = processedValue
                                        formStore.updateValue(fs.id, InputMasks.removeMask(processedValue.text))
                                    },
                                label = { Text(fieldDef.label ?: "") },
                                placeholder = { Text(fieldDef.placeholder ?: "") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = fieldHeight),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.fromHex(theme.palette.primary),
                                    unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedLabelColor = Color.fromHex(theme.palette.primary),
                                    unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    errorBorderColor = Color.fromHex("#ff0000")
                                ),
                                isError = fs.errors.isNotEmpty() && (fs.touched || fs.submitted),
                                shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                                )
                            } else {
                                OutlinedTextField(
                                    value = fs.value,
                                    onValueChange = { newValue: String -> 
                                        formStore.updateValue(fs.id, newValue)
                                    },
                                    label = { Text(fieldDef.label ?: "") },
                                    placeholder = { Text(fieldDef.placeholder ?: "") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = fieldHeight),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.fromHex(theme.palette.primary),
                                        unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                        focusedLabelColor = Color.fromHex(theme.palette.primary),
                                        unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        errorBorderColor = Color.fromHex("#ff0000")
                                    ),
                                    isError = fs.errors.isNotEmpty() && (fs.touched || fs.submitted),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                                )
                            }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = fieldHeight),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.fromHex(theme.palette.primary),
                                    unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedLabelColor = Color.fromHex(theme.palette.primary),
                                    unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    errorBorderColor = Color.fromHex("#ff0000")
                                ),
                                isError = fs.errors.isNotEmpty() && (fs.touched || fs.submitted),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }

                        "segmented" -> {
                            val buttonsInRow = fieldDef.options?.size ?: 0
                            val buttonSpacing = when {
                                buttonsInRow <= 2 -> 8.dp
                                buttonsInRow <= 3 -> 6.dp
                                else -> 4.dp
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
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
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Text(
                                            text = opt,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
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
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .heightIn(min = fieldHeight),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.fromHex(theme.palette.primary),
                                        unfocusedBorderColor = Color.fromHex(theme.palette.textSecondary),
                                        focusedLabelColor = Color.fromHex(theme.palette.primary),
                                        unfocusedLabelColor = Color.fromHex(theme.palette.textSecondary),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
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
                            val buttonsInRow = fieldDef.options?.size ?: 0
                            val buttonSpacing = when {
                                buttonsInRow <= 2 -> 8.dp
                                buttonsInRow <= 3 -> 6.dp
                                else -> 4.dp
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                fieldDef.options?.forEach { opt ->
                                    OutlinedButton(
                                        onClick = { formStore.updateValue(fieldDef.id, opt) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.fromHex(theme.palette.primary)
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(
                                                Color.fromHex(theme.palette.primary)
                                            )
                                        )
                                    ) {
                                        Text(
                                            text = opt,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (fs.errors.isNotEmpty() && (fs.touched || fs.submitted)) {
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
            
            val errorMessage = uiState.errorMessage
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.fromHex("#d32f2f"),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(8.dp))
            }

            val submitField = formStore.schema.fields.find { it.type == "submit" }
            
            Button(
                onClick = {
                    coroutineScope.launch {
                        formStore.submit { success, errors ->
                            if (success && formStore.schema.id == "login_form") {
                                onLoginSuccess?.invoke()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.fromHex(theme.palette.primary),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = (!uiState.isSubmitting && uiState.fields.any { it.touched }) && 
                          uiState.fields.all { it.errors.isEmpty() || !it.visible }
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = submitField?.label ?: "Enviar",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}