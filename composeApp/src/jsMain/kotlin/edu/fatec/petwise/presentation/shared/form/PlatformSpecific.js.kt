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

import kotlinx.datetime.LocalDate

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
    onValueChange: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier
) {
    var showDialog by remember { mutableStateOf(true) }

    val parts = fieldState.displayValue.split("-")
    var year by remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 2000) }
    var month by remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 1) }
    var day by remember { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                // Dismiss without changing value
            },
            confirmButton = {
                TextButton(onClick = {
                    val y = year
                    val m = month.coerceIn(1, 12)
                    val d = day.coerceIn(1, 31)
                    val localDate = kotlinx.datetime.LocalDate(y, m, d)
                    showDialog = false
                    onValueChange(localDate)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    // Dismiss without changing value
                }) { Text("Cancelar") }
            },
            title = { Text(text = fieldDefinition.label ?: "Selecione a Data") },
            text = {
                Column {
                    OutlinedTextField(
                        value = year.toString(),
                        onValueChange = { year = it.toIntOrNull() ?: year },
                        label = { Text("Ano") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = month.toString(),
                        onValueChange = { month = it.toIntOrNull() ?: month },
                        label = { Text("Mês") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = day.toString(),
                        onValueChange = { day = it.toIntOrNull() ?: day },
                        label = { Text("Dia") },
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
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onValueChange(fieldState.displayValue)
            },
            confirmButton = {
                TextButton(onClick = {
                    val h = hour.coerceIn(0, 23)
                    val m = minute.coerceIn(0, 59)
                    // build formatted time without using JVM String.format
                    val formatted = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                    showDialog = false
                    onValueChange(formatted)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    onValueChange(fieldState.displayValue)
                }) { Text("Cancelar") }
            },
            title = { Text(text = fieldDefinition.label ?: "Selecione o Horário") },
            text = {
                Column {
                    OutlinedTextField(
                        value = hour.toString(),
                        onValueChange = { hour = it.toIntOrNull() ?: hour },
                        label = { Text("Hora") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = minute.toString(),
                        onValueChange = { minute = it.toIntOrNull() ?: minute },
                        label = { Text("Minuto") },
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