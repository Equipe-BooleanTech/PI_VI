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
import platform.Foundation.*
import platform.UIKit.*
import kotlinx.datetime.LocalDate
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class PlatformFormBehavior {
    actual fun shouldShowKeyboardSpacer(): Boolean = true

    actual fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean {
        return when (fieldType) {
            FormFieldType.DATE, FormFieldType.TIME, FormFieldType.DATETIME -> true
            FormFieldType.SELECT -> true
            else -> false
        }
    }

    actual fun getSafeAreaInsets(): PaddingValues {
        return PaddingValues(0.dp)
    }

    actual fun getOptimalFieldHeight(): androidx.compose.ui.unit.Dp = 44.dp

    actual fun supportsHapticFeedback(): Boolean = true

    actual fun supportsSystemDarkMode(): Boolean = true
}

actual object PlatformFormStyling {
    actual fun getFieldShape(): Shape = RoundedCornerShape(10.dp)

    actual fun getFieldColors(colorScheme: ColorScheme): FieldColors {
        return FieldColors(
            background = colorScheme.surface,
            border = colorScheme.outline.copy(alpha = 0.3f),
            focusedBorder = colorScheme.primary,
            errorBorder = colorScheme.error,
            text = colorScheme.onSurface,
            label = colorScheme.onSurfaceVariant,
            placeholder = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }

    actual fun getTypography(): PlatformTypography {
        return PlatformTypography(
            fieldLabel = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            ),
            fieldText = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            ),
            errorText = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            ),
            helperText = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }

    actual fun getSpacing(): PlatformSpacing {
        return PlatformSpacing(
            fieldPadding = PaddingValues(16.dp),
            fieldSpacing = 12.dp,
            sectionSpacing = 20.dp
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class PlatformInputHandling {
    actual fun formatPhoneNumber(input: String, country: String): String {
        val digits = input.replace(Regex("[^0-9]"), "")
        return when (country) {
            "BR" -> {
                when (digits.length) {
                    11 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
                    10 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}"
                    else -> digits
                }
            }
            else -> digits
        }
    }

    actual fun formatCurrency(amount: Double, currency: String): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterCurrencyStyle

        when (currency) {
            "BRL" -> formatter.currencyCode = "BRL"
            "USD" -> formatter.currencyCode = "USD"
            else -> formatter.currencyCode = currency
        }

        val result = formatter.stringFromNumber(NSNumber.numberWithDouble(amount)) as? String
        return result ?: "$amount"
    }

    actual fun formatDate(timestamp: Long, format: String): String {
        val formatter = NSDateFormatter()
        formatter.dateFormat = format

        val date = NSDate.dateWithTimeIntervalSince1970(timestamp / 1000.0)
        return formatter.stringFromDate(date)
    }

    actual fun validatePlatformSpecific(fieldType: FormFieldType, value: String): Boolean {
        return when (fieldType) {
            FormFieldType.PHONE -> {
                val detector = try {
                    NSDataDetector.dataDetectorWithTypes(NSTextCheckingTypePhoneNumber, null)
                } catch (e: Exception) {
                    return false
                }

                val range = NSMakeRange(0u, value.length.toULong())
                val matches = detector?.matchesInString(value, NSMatchingOptions.MIN_VALUE, range)

                return (matches?.size ?: 0) > 0
            }
            FormFieldType.EMAIL -> {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                return NSPredicate.predicateWithFormat("SELF MATCHES %@", emailRegex)
                    .evaluateWithObject(value)
            }
            else -> true
        }
    }
}

actual class PlatformFileHandling {
    actual suspend fun pickFile(mimeTypes: List<String>): PlatformFile? {
        return null
    }

    actual suspend fun pickImage(): PlatformFile? {
        return null
    }

    actual suspend fun uploadFile(file: PlatformFile, endpoint: String): Result<String> {
        return Result.failure(Exception("Not implemented"))
    }
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
                    val h = hour.coerceIn(0,23)
                    val m = minute.coerceIn(0,59)
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

    Button(
        onClick = {  },
        modifier = modifier
    ) {
        Text("Choose File")
    }
}

@Composable
actual fun PlatformCameraCapturer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {

    Button(
        onClick = {  },
        modifier = modifier
    ) {
        Text("Take Photo")
    }
}

@OptIn(ExperimentalForeignApi::class)
actual object PlatformAccessibility {
    actual fun announceForAccessibility(message: String) {

        UIAccessibilityPostNotification(UIAccessibilityAnnouncementNotification, message)
    }

    actual fun setContentDescription(elementId: String, description: String) {

    }

    actual fun shouldUseHighContrast(): Boolean {
        return UIAccessibilityIsReduceTransparencyEnabled()
    }

    actual fun shouldUseLargeText(): Boolean {
        val currentCategory = UIApplication.sharedApplication.preferredContentSizeCategory
        val accessibilityMedium = UIContentSizeCategoryAccessibilityMedium
        return currentCategory != null && accessibilityMedium != null && currentCategory >= accessibilityMedium
    }

    actual fun shouldReduceMotion(): Boolean {
        return UIAccessibilityIsReduceMotionEnabled()
    }
}

@OptIn(ExperimentalForeignApi::class)
actual object PlatformHaptics {
    actual fun performLightImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        generator.impactOccurred()
    }

    actual fun performMediumImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        generator.impactOccurred()
    }

    actual fun performHeavyImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
        generator.impactOccurred()
    }

    actual fun performSelectionChanged() {
        val generator = UISelectionFeedbackGenerator()
        generator.selectionChanged()
    }

    actual fun performNotificationSuccess() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
    }

    actual fun performNotificationWarning() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
    }

    actual fun performNotificationError() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual object PlatformValidation {
    actual fun validateBankAccount(accountNumber: String, bankCode: String): Boolean {

        return true
    }

    actual fun validateGovernmentId(id: String, type: String): Boolean {

        return true
    }

    actual fun validatePostalCode(code: String): Boolean {

        val cepRegex = "^\\d{5}-?\\d{3}$"
        return NSPredicate.predicateWithFormat("SELF MATCHES %@", cepRegex)
            ?.evaluateWithObject(code) ?: false
    }

    actual fun validateCreditCard(number: String): Boolean {
        val cleanNumber = number.replace(Regex("[^0-9]"), "")

        if (cleanNumber.length !in 13..19) return false

        var sum = 0
        val reversedDigits = cleanNumber.reversed()

        for ((index, digitChar) in reversedDigits.withIndex()) {
            val digit = digitChar.toString().toIntOrNull() ?: return false

            if (index % 2 == 1) {
                val doubled = digit * 2
                sum += if (doubled > 9) doubled - 9 else doubled
            } else {
                sum += digit
            }
        }

        return sum % 10 == 0
    }
}