@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicForm(
    viewModel: DynamicFormViewModel,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    customFieldRenderer: @Composable ((FormFieldDefinition, FieldState, (Any?) -> Unit) -> Boolean)? = null,
    onSubmitSuccess: ((Map<String, Any>) -> Unit)? = null,
    onSubmitError: ((FormError) -> Unit)? = null,
    onFieldChanged: ((String, Any?, Any?) -> Unit)? = null
) {
    val formState by viewModel.state.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = null)
    
    LaunchedEffect(events) {
        events?.let { event ->
            when (event) {
                is FormEvent.FormSubmitted -> {
                    if (event.isValid) {
                        onSubmitSuccess?.invoke(event.values)
                    }
                }
                is FormEvent.ErrorOccurred -> {
                    onSubmitError?.invoke(event.error)
                }
                is FormEvent.FieldValueChanged -> {
                    onFieldChanged?.invoke(event.fieldId, event.oldValue, event.newValue)
                }
                else -> {}
            }
        }
    }
    
    DynamicFormContent(
        state = formState,
        onFieldValueChange = { fieldId, value -> viewModel.updateFieldValue(fieldId, value) },
        onFieldFocus = { fieldId -> viewModel.focusField(fieldId) },
        onFieldBlur = { fieldId -> viewModel.blurField(fieldId) },
        onSubmit = { viewModel.submitForm() },
        modifier = modifier,
        colorScheme = colorScheme,
        customFieldRenderer = customFieldRenderer
    )
}

@Composable
private fun DynamicFormContent(
    state: FormState,
    onFieldValueChange: (String, Any?) -> Unit,
    onFieldFocus: (String) -> Unit,
    onFieldBlur: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    customFieldRenderer: @Composable ((FormFieldDefinition, FieldState, (Any?) -> Unit) -> Boolean)? = null
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        val spacing = calculateSpacing(screenWidth)
        val fieldHeight = calculateFieldHeight(screenWidth)
        
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.configuration.title?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground
                )
            }
            
            state.configuration.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            state.configuration.fields.forEach { fieldDef ->
                val fieldState = state.fieldStates[fieldDef.id]
                
                if (fieldState != null && fieldState.isVisible) {
                    val wasHandled = customFieldRenderer?.invoke(
                        fieldDef, 
                        fieldState
                    ) { newValue ->
                        onFieldValueChange(fieldDef.id, newValue)
                    } ?: false
                    
                    if (!wasHandled) {
                        RenderFormField(
                            fieldDefinition = fieldDef,
                            fieldState = fieldState,
                            onValueChange = { onFieldValueChange(fieldDef.id, it) },
                            onFocus = { onFieldFocus(fieldDef.id) },
                            onBlur = { onFieldBlur(fieldDef.id) },
                            fieldHeight = fieldHeight,
                            colorScheme = colorScheme
                        )
                    }
                    
                    if (fieldState.errors.isNotEmpty() && fieldState.isTouched) {
                        fieldState.errors.forEach { error ->
                            Text(
                                text = error.message,
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            if (state.errors.globalErrors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                state.errors.globalErrors.forEach { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error.message,
                            color = colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            
            val submitField = state.configuration.fields.find { it.type == FormFieldType.SUBMIT }
            if (submitField != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !state.isSubmitting && state.fieldStates.values.any { it.isTouched },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = submitField.label ?: "Submit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderFormField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    when (fieldDefinition.type) {
        FormFieldType.TEXT, FormFieldType.EMAIL, FormFieldType.NUMBER, FormFieldType.DECIMAL -> {
            RenderTextField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.PASSWORD -> {
            RenderPasswordField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.SELECT -> {
            RenderSelectField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.RADIO, FormFieldType.SEGMENTED_CONTROL -> {
            RenderSegmentedControl(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.CHECKBOX -> {
            RenderCheckboxField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.SWITCH -> {
            RenderSwitchField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.TEXTAREA -> {
            RenderTextAreaField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                colorScheme = colorScheme
            )
        }
        
        FormFieldType.DIVIDER -> {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.outline
            )
        }
        
        FormFieldType.SPACER -> {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        else -> {
            Text(
                text = "Unsupported field type: ${fieldDefinition.type}",
                color = colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun RenderTextField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    val keyboardType = when (fieldDefinition.type) {
        FormFieldType.EMAIL -> KeyboardType.Email
        FormFieldType.NUMBER -> KeyboardType.Number
        FormFieldType.DECIMAL -> KeyboardType.Decimal
        FormFieldType.PHONE -> KeyboardType.Phone
        else -> KeyboardType.Text
    }
    
    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = onValueChange,
        label = fieldDefinition.label?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = fieldHeight),
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            errorBorderColor = colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
private fun RenderPasswordField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = onValueChange,
        label = fieldDefinition.label?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
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
                        "Hide password" 
                    else 
                        "Show password",
                    tint = colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = fieldHeight),
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            errorBorderColor = colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
private fun RenderSelectField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = fieldState.displayValue,
            onValueChange = {},
            readOnly = true,
            label = fieldDefinition.label?.let { { Text(it) } },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = fieldHeight),
            enabled = fieldState.isEnabled,
            isError = fieldState.errors.isNotEmpty() && fieldState.isTouched,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant,
                errorBorderColor = colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fieldDefinition.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RenderSegmentedControl(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    colorScheme: ColorScheme
) {
    fieldDefinition.options?.let { options ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = fieldState.displayValue == option
                
                FilterChip(
                    onClick = { onValueChange(option) },
                    label = { Text(option) },
                    selected = isSelected,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun RenderCheckboxField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Boolean) -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = fieldState.value as? Boolean ?: false,
            onCheckedChange = onValueChange,
            enabled = fieldState.isEnabled,
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.primary
            )
        )
        
        fieldDefinition.label?.let { label ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RenderSwitchField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Boolean) -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        fieldDefinition.label?.let { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        
        Switch(
            checked = fieldState.value as? Boolean ?: false,
            onCheckedChange = onValueChange,
            enabled = fieldState.isEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.primary
            )
        )
    }
}

@Composable
private fun RenderTextAreaField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    colorScheme: ColorScheme
) {
    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = onValueChange,
        label = fieldDefinition.label?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            errorBorderColor = colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp),
        maxLines = 5
    )
}

@Composable
private fun calculateSpacing(screenWidth: Dp): Dp {
    return with(LocalDensity.current) {
        when {
            screenWidth < 400.dp -> 12.dp
            screenWidth < 600.dp -> 16.dp
            else -> 20.dp
        }
    }
}

@Composable
private fun calculateFieldHeight(screenWidth: Dp): Dp {
    return with(LocalDensity.current) {
        when {
            screenWidth < 400.dp -> 52.dp
            else -> 56.dp
        }
    }
}