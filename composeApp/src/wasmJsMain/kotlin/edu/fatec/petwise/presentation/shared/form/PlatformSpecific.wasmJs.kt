package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual class PlatformFormBehavior {
    actual fun shouldShowKeyboardSpacer(): Boolean = false
    actual fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean = false
    actual fun getSafeAreaInsets(): PaddingValues = PaddingValues(0.dp)
    actual fun getOptimalFieldHeight(): androidx.compose.ui.unit.Dp = 48.dp
    actual fun supportsHapticFeedback(): Boolean = false
    actual fun supportsSystemDarkMode(): Boolean = true
}

actual object PlatformFormStyling {
    actual fun getFieldShape(): Shape = RoundedCornerShape(4.dp)
    actual fun getFieldColors(colorScheme: ColorScheme): FieldColors = FieldColors(
        background = colorScheme.surface,
        border = colorScheme.outline,
        focusedBorder = colorScheme.primary,
        errorBorder = colorScheme.error,
        text = colorScheme.onSurface,
        label = colorScheme.onSurfaceVariant,
        placeholder = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
    actual fun getTypography(): PlatformTypography = PlatformTypography(
        fieldLabel = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
        fieldText = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
        errorText = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
        helperText = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    )
    actual fun getSpacing(): PlatformSpacing = PlatformSpacing(
        fieldPadding = PaddingValues(12.dp),
        fieldSpacing = 12.dp,
        sectionSpacing = 20.dp
    )
}

actual class PlatformInputHandling {
    actual fun formatPhoneNumber(input: String, country: String): String = input
    actual fun formatCurrency(amount: Double, currency: String): String = "$currency $amount"
    actual fun formatDate(timestamp: Long, format: String): String = timestamp.toString()
    actual fun validatePlatformSpecific(fieldType: FormFieldType, value: String): Boolean = true
}

actual class PlatformFileHandling {
    actual suspend fun pickFile(mimeTypes: List<String>): PlatformFile? = null
    actual suspend fun pickImage(): PlatformFile? = null
    actual suspend fun uploadFile(file: PlatformFile, endpoint: String): Result<String> = Result.failure(Exception("Not implemented"))
}

@Composable
actual fun PlatformDatePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    var showDialog by remember { mutableStateOf(true) }

    val parts = fieldState.displayValue.split("-")
    var year by remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 2000) }
    var month by remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 1) }
    var day by remember { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 1) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showDialog = false
                onValueChange(fieldState.displayValue)
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val y = year
                    val m = month.coerceIn(1, 12)
                    val d = day.coerceIn(1, 31)
                    val formatted = "%04d-%02d-%02d".format(y, m, d)
                    showDialog = false
                    onValueChange(formatted)
                }) { androidx.compose.material3.Text("OK") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog = false
                    onValueChange(fieldState.displayValue)
                }) { androidx.compose.material3.Text("Cancelar") }
            },
            title = { androidx.compose.material3.Text(text = fieldDefinition.label ?: "Selecione a Data") },
            text = {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.OutlinedTextField(
                        value = year.toString(),
                        onValueChange = { year = it.toIntOrNull() ?: year },
                        label = { androidx.compose.material3.Text("Ano") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = month.toString(),
                        onValueChange = { month = it.toIntOrNull() ?: month },
                        label = { androidx.compose.material3.Text("Mês") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = day.toString(),
                        onValueChange = { day = it.toIntOrNull() ?: day },
                        label = { androidx.compose.material3.Text("Dia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
actual fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
     var showDialog by remember { mutableStateOf(true) }

    val parts = fieldState.displayValue.split(":")
    var hour by remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 12) }
    var minute by remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showDialog = false
                onValueChange(fieldState.displayValue)
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val h = hour.coerceIn(0,23)
                    val m = minute.coerceIn(0,59)
                    val formatted = "%02d:%02d".format(h,m)
                    showDialog = false
                    onValueChange(formatted)
                }) { androidx.compose.material3.Text("OK") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog = false
                    onValueChange(fieldState.displayValue)
                }) { androidx.compose.material3.Text("Cancelar") }
            },
            title = { androidx.compose.material3.Text(text = fieldDefinition.label ?: "Selecione o Horário") },
            text = {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.OutlinedTextField(
                        value = hour.toString(),
                        onValueChange = { hour = it.toIntOrNull() ?: hour },
                        label = { androidx.compose.material3.Text("Hora") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = minute.toString(),
                        onValueChange = { minute = it.toIntOrNull() ?: minute },
                        label = { androidx.compose.material3.Text("Minuto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
actual fun PlatformFilePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Button(onClick = {}, modifier = modifier) {
        androidx.compose.material3.Text("Pick File")
    }
}

@Composable
actual fun PlatformCameraCapturer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Button(onClick = {}, modifier = modifier) {
        androidx.compose.material3.Text("Take Photo")
    }
}

actual object PlatformAccessibility {
    actual fun announceForAccessibility(message: String) {}
    actual fun setContentDescription(elementId: String, description: String) {}
    actual fun shouldUseHighContrast(): Boolean = false
    actual fun shouldUseLargeText(): Boolean = false
    actual fun shouldReduceMotion(): Boolean = false
}

actual object PlatformHaptics {
    actual fun performLightImpact() {}
    actual fun performMediumImpact() {}
    actual fun performHeavyImpact() {}
    actual fun performSelectionChanged() {}
    actual fun performNotificationSuccess() {}
    actual fun performNotificationWarning() {}
    actual fun performNotificationError() {}
}

actual object PlatformValidation {
    actual fun validateBankAccount(accountNumber: String, bankCode: String): Boolean = true
    actual fun validateGovernmentId(id: String, type: String): Boolean = true
    actual fun validatePostalCode(code: String): Boolean = code.matches(Regex("^\\d{5}-?\\d{3}$"))
    actual fun validateCreditCard(number: String): Boolean = true
}