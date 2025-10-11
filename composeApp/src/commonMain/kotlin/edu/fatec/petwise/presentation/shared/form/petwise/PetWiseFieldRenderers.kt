package edu.fatec.petwise.presentation.shared.form.petwise

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.presentation.shared.form.theme.LocalPetWiseTheme
import edu.fatec.petwise.presentation.shared.form.theme.PetWiseFormColors
import edu.fatec.petwise.presentation.shared.form.theme.PetWiseFormSpacing
import edu.fatec.petwise.presentation.shared.form.masking.InputMaskBridge
import edu.fatec.petwise.presentation.theme.fromHex
import edu.fatec.petwise.features.auth.shared.InputMasks

@Composable
fun PetWiseCustomFieldRenderer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
): Boolean {
    return when (fieldDefinition.type) {
        FormFieldType.TEXT, FormFieldType.EMAIL, FormFieldType.PHONE, FormFieldType.NUMBER -> {
            PetWiseMaskedTextField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange
            )
            true
        }
        FormFieldType.PASSWORD -> {
            PetWisePasswordField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange
            )
            true
        }
        FormFieldType.SEGMENTED_CONTROL -> {
            PetWiseSegmentedControl(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange
            )
            true
        }
        FormFieldType.SELECT -> {
            PetWiseSelectField(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange
            )
            true
        }
        FormFieldType.RADIO -> {
            PetWiseButtonGroup(
                fieldDefinition = fieldDefinition,
                fieldState = fieldState,
                onValueChange = onValueChange
            )
            true
        }
        else -> false
    }
}

@Composable
private fun PetWiseMaskedTextField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
) {
    val theme = LocalPetWiseTheme.current

    val keyboardType = when {
        fieldDefinition.type == FormFieldType.EMAIL -> KeyboardType.Email
        fieldDefinition.type == FormFieldType.PHONE -> KeyboardType.Phone
        fieldDefinition.id in setOf("cpf", "cnpj", "cep", "crmv") -> KeyboardType.Number
        fieldDefinition.type == FormFieldType.NUMBER -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    val shouldUseMask = InputMaskBridge.shouldUseMask(fieldDefinition.id, fieldDefinition.formatting)
    val maskPattern = fieldDefinition.formatting?.mask ?: InputMaskBridge.getMaskForFieldId(fieldDefinition.id)

    BoxWithConstraints {
        val fieldHeight = PetWiseFormSpacing.getFieldHeight(maxWidth)

        if (shouldUseMask && maskPattern != null) {
            var textFieldValue by remember {
                mutableStateOf(TextFieldValue(fieldState.displayValue))
            }

            LaunchedEffect(fieldState.displayValue) {
                if (textFieldValue.text != fieldState.displayValue) {
                    textFieldValue = TextFieldValue(fieldState.displayValue)
                }
            }

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue: TextFieldValue ->
                    val processedValue = InputMasks.processTextWithMask(
                        textFieldValue,
                        newValue.text,
                        maskPattern
                    )
                    textFieldValue = processedValue
                    onValueChange(InputMasks.removeMask(processedValue.text))
                },
                label = fieldDefinition.label?.let { { Text(it) } },
                placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = fieldHeight),
                colors = PetWiseFormColors.getFieldColors(
                    theme,
                    isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                enabled = fieldState.isEnabled,
                isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
            )
        } else {

            OutlinedTextField(
                value = fieldState.displayValue,
                onValueChange = { onValueChange(it) },
                label = fieldDefinition.label?.let { { Text(it) } },
                placeholder = fieldDefinition.placeholder?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = fieldHeight),
                colors = PetWiseFormColors.getFieldColors(
                    theme,
                    isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                enabled = fieldState.isEnabled,
                isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
            )
        }
    }
}

@Composable
private fun PetWisePasswordField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
) {
    val theme = LocalPetWiseTheme.current
    var passwordVisible by remember { mutableStateOf(false) }

    BoxWithConstraints {
        val fieldHeight = PetWiseFormSpacing.getFieldHeight(maxWidth)

        OutlinedTextField(
            value = fieldState.displayValue,
            onValueChange = { onValueChange(it) },
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
            colors = PetWiseFormColors.getFieldColors(
                theme,
                isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            enabled = fieldState.isEnabled,
            isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
        )
    }
}

@Composable
private fun PetWiseSegmentedControl(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
) {
    val theme = LocalPetWiseTheme.current
    val options = fieldDefinition.options ?: emptyList()

    val buttonsInRow = options.size
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
        options.forEach { option ->
            val isSelected = fieldState.displayValue == option

            ElevatedButton(
                onClick = { onValueChange(option) },
                colors = PetWiseFormColors.getSegmentedControlColors(theme, isSelected),
                modifier = Modifier.weight(1f, fill = false),
                enabled = fieldState.isEnabled
            ) {
                Text(
                    text = option,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetWiseSelectField(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
) {
    val theme = LocalPetWiseTheme.current
    var expanded by remember { mutableStateOf(false) }

    BoxWithConstraints {
        val fieldHeight = PetWiseFormSpacing.getFieldHeight(maxWidth)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && fieldState.isEnabled }
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
                colors = PetWiseFormColors.getFieldColors(
                    theme,
                    isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = fieldState.isEnabled,
                isError = fieldState.errors.isNotEmpty() && fieldState.isTouched
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
}

@Composable
private fun PetWiseButtonGroup(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (Any?) -> Unit
) {
    val theme = LocalPetWiseTheme.current
    val options = fieldDefinition.options ?: emptyList()

    val buttonsInRow = options.size
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
        options.forEach { option ->
            OutlinedButton(
                onClick = { onValueChange(option) },
                modifier = Modifier.weight(1f),
                colors = PetWiseFormColors.getOutlinedButtonColors(theme),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        Color.fromHex(theme.palette.primary)
                    )
                ),
                enabled = fieldState.isEnabled
            ) {
                Text(
                    text = option,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}