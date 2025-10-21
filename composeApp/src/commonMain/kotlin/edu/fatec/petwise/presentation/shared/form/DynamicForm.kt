@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.fatec.petwise.features.auth.shared.InputMasks

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
            val formHasBeenSubmitted = state.metadata.submitCount > 0
            
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
                            colorScheme = colorScheme,
                            formHasBeenSubmitted = formHasBeenSubmitted
                        )
                    }

                    val shouldShowErrors = fieldState.errors.isNotEmpty() && 
                        (fieldState.isTouched || (formHasBeenSubmitted && fieldState.errors.isNotEmpty()))
                    
                    if (shouldShowErrors) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            fieldState.errors.forEach { error ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = extractCleanErrorMessage(error.message),
                                        color = colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    
                    if (fieldState.isValidating) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = colorScheme.primary,
                                strokeWidth = 1.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Validando...",
                                color = colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else if (fieldState.isTouched && fieldState.errors.isEmpty() && fieldState.value?.toString()?.isNotEmpty() == true) {

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
                            text = extractCleanErrorMessage(error.message),
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

                val visibleRequiredFields = state.configuration.fields.filter { field ->
                    field.validators.any { it.type == ValidationType.REQUIRED } && 
                    field.type != FormFieldType.SUBMIT &&
                    state.fieldStates[field.id]?.isVisible == true
                }
            
                
                val allRequiredFieldsValid = visibleRequiredFields.all { field ->
                    val fieldState = state.fieldStates[field.id]
                    if (fieldState != null && fieldState.isVisible) {
                        val hasValue = !fieldState.value?.toString().isNullOrBlank()
                        val hasNoErrors = fieldState.errors.isEmpty()
                        hasValue && hasNoErrors
                    } else {
                        false
                    }
                }
                
                val hasFieldErrors = state.fieldStates.values.any { fieldState ->
                    fieldState.isVisible && fieldState.errors.isNotEmpty()
                }
                
                val allVisibleFieldsValidated = state.configuration.fields
                    .filter { field -> 
                        field.type != FormFieldType.SUBMIT &&
                        state.fieldStates[field.id]?.isVisible == true 
                    }
                    .all { field ->
                        val fieldState = state.fieldStates[field.id]
                        val isRequired = field.validators.any { it.type == ValidationType.REQUIRED }
                        
                        if (fieldState != null) {
                            if (isRequired) {
                                !fieldState.value?.toString().isNullOrBlank() && fieldState.errors.isEmpty()
                            } else {
                                fieldState.errors.isEmpty()
                            }
                        } else {
                            !isRequired
                        }
                    }
                
                val isFormValid = allRequiredFieldsValid && !hasFieldErrors && allVisibleFieldsValidated
                
                val fieldsWithErrors = state.fieldStates.values.filter { 
                    it.isVisible && it.errors.isNotEmpty() && it.isTouched
                }

                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !state.isSubmitting && isFormValid,
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
    colorScheme: ColorScheme,
    formHasBeenSubmitted: Boolean
) {
    when (fieldDefinition.type) {
        FormFieldType.TEXT, FormFieldType.EMAIL, FormFieldType.NUMBER, FormFieldType.DECIMAL, FormFieldType.PHONE -> {
            RenderTextField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme,
                formHasBeenSubmitted = formHasBeenSubmitted
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
                colorScheme = colorScheme,
                formHasBeenSubmitted = formHasBeenSubmitted
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
                colorScheme = colorScheme,
                formHasBeenSubmitted = formHasBeenSubmitted
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

        FormFieldType.DATE -> {
            RenderDateField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme
            )
        }

        FormFieldType.TIME -> {
            RenderTimeField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
                colorScheme = colorScheme
            )
        }

        FormFieldType.DATETIME -> {
            RenderDateTimeField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange,
                onFocus = onFocus,
                onBlur = onBlur,
                fieldHeight = fieldHeight,
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
                text = "Tipo de campo não suportado: ${fieldDefinition.type}",
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
    colorScheme: ColorScheme,
    formHasBeenSubmitted: Boolean
) {
    val keyboardType = when (fieldDefinition.type) {
        FormFieldType.EMAIL -> KeyboardType.Email
        FormFieldType.NUMBER -> KeyboardType.Number
        FormFieldType.DECIMAL -> KeyboardType.Decimal
        FormFieldType.PHONE -> KeyboardType.Phone
        else -> KeyboardType.Text
    }

    val shouldUseMask = fieldDefinition.formatting?.mask != null ||
        InputMasks.getMaskForFieldType(fieldDefinition.id, fieldDefinition.type.name) != null

    val maskPattern = fieldDefinition.formatting?.mask 
        ?: InputMasks.getMaskForFieldType(fieldDefinition.id, fieldDefinition.type.name)
    
    val isRequired = fieldDefinition.validators.any { it.type == ValidationType.REQUIRED }
    val labelText = if (isRequired && fieldDefinition.label != null) {
        "${fieldDefinition.label} *"
    } else {
        fieldDefinition.label
    }

    if (shouldUseMask && maskPattern != null) {
        var textFieldValue by remember(fieldState.displayValue) {
            mutableStateOf(TextFieldValue(fieldState.displayValue, TextRange(fieldState.displayValue.length)))
        }
        
        LaunchedEffect(fieldState.displayValue) {
            if (textFieldValue.text != fieldState.displayValue) {
                textFieldValue = TextFieldValue(
                    text = fieldState.displayValue,
                    selection = TextRange(fieldState.displayValue.length)
                )
            }
        }

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val processedValue = InputMasks.processTextWithMask(
                    currentValue = textFieldValue,
                    newText = newValue.text,
                    mask = maskPattern
                )
                textFieldValue = processedValue
                onValueChange(InputMasks.removeMask(processedValue.text))
            },
            label = labelText?.let { { Text(it) } },
            placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = fieldHeight)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocus()
                    } else {
                        onBlur()
                    }
                },
            enabled = fieldState.isEnabled,
            isError = shouldShowFieldError(fieldState, formHasBeenSubmitted),
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
    } else {
        OutlinedTextField(
            value = fieldState.displayValue,
            onValueChange = onValueChange,
            label = labelText?.let { { Text(it) } },
            placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = fieldHeight)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocus()
                    } else {
                        onBlur()
                    }
                },
            enabled = fieldState.isEnabled,
            isError = shouldShowFieldError(fieldState, formHasBeenSubmitted),
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
}

@Composable
private fun RenderPasswordField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme,
    formHasBeenSubmitted: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    val isRequired = fieldDefinition.validators.any { it.type == ValidationType.REQUIRED }
    val labelText = if (isRequired && fieldDefinition.label != null) {
        "${fieldDefinition.label} *"
    } else {
        fieldDefinition.label
    }

    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = { newValue ->
            val processedValue = when {
                fieldDefinition.formatting?.uppercase == true -> newValue.uppercase()
                fieldDefinition.formatting?.lowercase == true -> newValue.lowercase()
                fieldDefinition.formatting?.capitalize == true -> newValue.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
                else -> newValue
            }
            onValueChange(processedValue)
        },
        label = labelText?.let { { Text(it) } },
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
            .heightIn(min = fieldHeight)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus()
                } else {
                    onBlur()
                }
            },
        enabled = fieldState.isEnabled,
        isError = shouldShowFieldError(fieldState, formHasBeenSubmitted),
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
    colorScheme: ColorScheme,
    formHasBeenSubmitted: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { newExpanded ->
            expanded = newExpanded
            if (newExpanded) {
                onFocus()
            }
        }
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
            isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
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
            onDismissRequest = { 
                expanded = false
                onBlur()
            }
        ) {
            // Handle new selectOptions
            fieldDefinition.selectOptions?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    onClick = {
                        onValueChange(option.key)
                        expanded = false
                        onBlur() 
                    }
                )
            }
            
            // Handle legacy string options
            fieldDefinition.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                        onBlur() 
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
    val allOptions = (fieldDefinition.selectOptions?.map { it.key to it.value } ?: emptyList()) +
                     (fieldDefinition.options?.map { it to it } ?: emptyList())
    
    if (allOptions.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allOptions.forEach { (key, value) ->
                val isSelected = fieldState.value == key

                FilterChip(
                    onClick = { onValueChange(key) },
                    label = { Text(value) },
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
        onValueChange = { newValue ->
            val processedValue = when {
                fieldDefinition.formatting?.uppercase == true -> newValue.uppercase()
                fieldDefinition.formatting?.lowercase == true -> newValue.lowercase()
                fieldDefinition.formatting?.capitalize == true -> newValue.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
                else -> newValue
            }
            onValueChange(processedValue)
        },
        label = fieldDefinition.label?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus()
                } else {
                    onBlur()
                }
            },
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
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
private fun RenderDateField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    val isRequired = fieldDefinition.validators.any { it.type == ValidationType.REQUIRED }
    val labelText = if (isRequired && fieldDefinition.label != null) {
        "${fieldDefinition.label} *"
    } else {
        fieldDefinition.label
    }

    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = {},
        readOnly = true,
        label = labelText?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = fieldHeight)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus()
                    showDatePicker = true
                }
            },
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            errorBorderColor = colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true; onFocus() }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date",
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    )

    if (showDatePicker) {
        PlatformDatePicker(
            fieldDefinition = fieldDefinition,
            fieldState = fieldState,
            onValueChange = { newValue ->
                onValueChange(newValue)
                showDatePicker = false
                onBlur()
            }
        )
    }
}

@Composable
private fun RenderTimeField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    val isRequired = fieldDefinition.validators.any { it.type == ValidationType.REQUIRED }
    val labelText = if (isRequired && fieldDefinition.label != null) {
        "${fieldDefinition.label} *"
    } else {
        fieldDefinition.label
    }

    OutlinedTextField(
        value = fieldState.displayValue,
        onValueChange = {},
        readOnly = true,
        label = labelText?.let { { Text(it) } },
        placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = fieldHeight)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus()
                    showTimePicker = true
                }
            },
        enabled = fieldState.isEnabled,
        isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            errorBorderColor = colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true; onFocus() }) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Select Time",
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    )

    if (showTimePicker) {
        PlatformTimePicker(
            fieldDefinition = fieldDefinition,
            fieldState = fieldState,
            onValueChange = { newValue ->
                onValueChange(newValue)
                showTimePicker = false
                onBlur()
            }
        )
    }
}

@Composable
private fun RenderDateTimeField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onBlur: () -> Unit,
    fieldHeight: Dp,
    colorScheme: ColorScheme
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var dateValue by remember { mutableStateOf("") }
    var timeValue by remember { mutableStateOf("") }
    
    LaunchedEffect(fieldState.displayValue) {
        val parts = fieldState.displayValue.split(" ")
        if (parts.size >= 2) {
            dateValue = parts[0]
            timeValue = parts[1]
        }
    }
    
    val isRequired = fieldDefinition.validators.any { it.type == ValidationType.REQUIRED }
    val labelText = if (isRequired && fieldDefinition.label != null) {
        "${fieldDefinition.label} *"
    } else {
        fieldDefinition.label
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = dateValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("${labelText ?: "Date/Time"} - Date") },
            placeholder = { Text("Select Date") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = fieldHeight),
            enabled = fieldState.isEnabled,
            isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant,
                errorBorderColor = colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true; onFocus() }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        OutlinedTextField(
            value = timeValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("${labelText ?: "Date/Time"} - Time") },
            placeholder = { Text("Select Time") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = fieldHeight),
            enabled = fieldState.isEnabled,
            isError = fieldState.errors.isNotEmpty() && fieldState.isTouched && fieldState.isDirty,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant,
                errorBorderColor = colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true; onFocus() }) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Select Time",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (showDatePicker) {
        PlatformDatePicker(
            fieldDefinition = fieldDefinition,
            fieldState = fieldState.copy(displayValue = dateValue),
            onValueChange = { newDate ->
                dateValue = newDate
                onValueChange("$newDate $timeValue")
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        PlatformTimePicker(
            fieldDefinition = fieldDefinition,
            fieldState = fieldState.copy(displayValue = timeValue),
            onValueChange = { newTime ->
                timeValue = newTime
                onValueChange("$dateValue $newTime")
                showTimePicker = false
                onBlur()
            }
        )
    }
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

private fun shouldShowFieldError(fieldState: FieldState, formHasBeenSubmitted: Boolean): Boolean {
    return fieldState.errors.isNotEmpty() && 
        (fieldState.isTouched || formHasBeenSubmitted)
}

private fun extractCleanErrorMessage(rawMessage: String): String {
    return try {
        when {
            rawMessage.contains("\"message\":") -> {
                val patterns = listOf("\"message\":\"", "\"message\": \"")
                for (pattern in patterns) {
                    val messageStart = rawMessage.indexOf(pattern)
                    if (messageStart != -1) {
                        val start = messageStart + pattern.length
                        val end = rawMessage.indexOf("\"", start)
                        if (end != -1) {
                            return rawMessage.substring(start, end)
                        }
                    }
                }
                rawMessage
            }
            rawMessage.contains("\"error\":") -> {
                val patterns = listOf("\"error\":\"", "\"error\": \"")
                for (pattern in patterns) {
                    val errorStart = rawMessage.indexOf(pattern)
                    if (errorStart != -1) {
                        val start = errorStart + pattern.length
                        val end = rawMessage.indexOf("\"", start)
                        if (end != -1) {
                            return rawMessage.substring(start, end)
                        }
                    }
                }
                rawMessage
            }
            rawMessage.contains("HTTP") && rawMessage.contains("Regra de negócio violada") -> {
                "Email ou senha incorretos"
            }
            rawMessage.startsWith("HTTP") && rawMessage.contains(":") -> {
                val colonIndex = rawMessage.indexOf(":", rawMessage.indexOf(" ") + 1)
                if (colonIndex != -1) {
                    val cleaned = rawMessage.substring(colonIndex + 1).trim()
                    if (cleaned.contains("validationErrors")) {
                        "Dados inválidos. Verifique as informações inseridas."
                    } else {
                        cleaned
                    }
                } else rawMessage
            }
            rawMessage.length > 100 -> {
                "Erro no servidor. Tente novamente."
            }
            else -> rawMessage
        }
    } catch (e: Exception) {
        "Erro inesperado. Tente novamente."
    }
}