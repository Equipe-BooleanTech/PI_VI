package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual class PlatformFormBehavior {
    actual fun shouldShowKeyboardSpacer(): Boolean = true

    actual fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean {
        return when (fieldType) {
            FormFieldType.DATE, FormFieldType.TIME, FormFieldType.DATETIME -> true
            else -> false
        }
    }

    actual fun getSafeAreaInsets(): PaddingValues {

        return PaddingValues(0.dp)
    }

    actual fun getOptimalFieldHeight(): androidx.compose.ui.unit.Dp = 56.dp

    actual fun supportsHapticFeedback(): Boolean = true

    actual fun supportsSystemDarkMode(): Boolean = true
}

actual object PlatformFormStyling {
    actual fun getFieldShape(): Shape = RoundedCornerShape(8.dp)

    actual fun getFieldColors(colorScheme: ColorScheme): FieldColors {
        return FieldColors(
            background = colorScheme.surface,
            border = colorScheme.outline,
            focusedBorder = colorScheme.primary,
            errorBorder = colorScheme.error,
            text = colorScheme.onSurface,
            label = colorScheme.onSurfaceVariant,
            placeholder = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }

    actual fun getTypography(): PlatformTypography {
        return PlatformTypography(
            fieldLabel = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            fieldText = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            errorText = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            helperText = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }

    actual fun getSpacing(): PlatformSpacing {
        return PlatformSpacing(
            fieldPadding = PaddingValues(16.dp),
            fieldSpacing = 16.dp,
            sectionSpacing = 24.dp
        )
    }
}

actual class PlatformInputHandling {
    actual fun formatPhoneNumber(input: String, country: String): String {
        val digits = input.replace(Regex("[^0-9]"), "")
        return when (country) {
            "BR" -> {
                when (digits.length) {
                    11 -> "${digits.substring(0, 2)} ${digits.substring(2, 7)}-${digits.substring(7)}"
                    10 -> "${digits.substring(0, 2)} ${digits.substring(2, 6)}-${digits.substring(6)}"
                    else -> digits
                }
            }
            else -> digits
        }
    }

    actual fun formatCurrency(amount: Double, currency: String): String {
        return when (currency) {
            "BRL" -> "R$ %.2f".format(amount)
            "USD" -> "$%.2f".format(amount)
            else -> "%.2f %s".format(amount, currency)
        }
    }

    actual fun formatDate(timestamp: Long, format: String): String {
        return java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }

    actual fun validatePlatformSpecific(fieldType: FormFieldType, value: String): Boolean {
        return when (fieldType) {
            FormFieldType.PHONE -> {
                android.util.Patterns.PHONE.matcher(value).matches()
            }
            FormFieldType.EMAIL -> {
                android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()
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
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {

    androidx.compose.material3.Text(
        text = "Android Date Picker - ${fieldState.displayValue}",
        modifier = modifier
    )
}

@Composable
actual fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Text(
        text = "Android Time Picker - ${fieldState.displayValue}",
        modifier = modifier
    )
}

@Composable
actual fun PlatformFilePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Button(
        onClick = {  },
        modifier = modifier
    ) {
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
    androidx.compose.material3.Button(
        onClick = {  },
        modifier = modifier
    ) {
        androidx.compose.material3.Text("Take Photo")
    }
}

actual object PlatformAccessibility {
    actual fun announceForAccessibility(message: String) {
    }

    actual fun setContentDescription(elementId: String, description: String) {
    }

    actual fun shouldUseHighContrast(): Boolean {
        return false
    }

    actual fun shouldUseLargeText(): Boolean {
        return false
    }

    actual fun shouldReduceMotion(): Boolean {
        return false
    }
}

actual object PlatformHaptics {
    actual fun performLightImpact() {
    }

    actual fun performMediumImpact() {
    }

    actual fun performHeavyImpact() {
    }

    actual fun performSelectionChanged() {
    }

    actual fun performNotificationSuccess() {
    }

    actual fun performNotificationWarning() {
    }

    actual fun performNotificationError() {
    }
}

actual object PlatformValidation {
    actual fun validateBankAccount(accountNumber: String, bankCode: String): Boolean {
        return true
    }

    actual fun validateGovernmentId(id: String, type: String): Boolean {
        return true
    }

    actual fun validatePostalCode(code: String): Boolean {
        return code.matches(Regex("^\\d{5}-?\\d{3}$"))
    }

    actual fun validateCreditCard(number: String): Boolean {
        val cleanNumber = number.replace(Regex("[^0-9]"), "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false

        var sum = 0
        var alternate = false

        for (i in cleanNumber.length - 1 downTo 0) {
            var n = cleanNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = n % 10 + 1
                }
            }
            sum += n
            alternate = !alternate
        }

        return sum % 10 == 0
    }
}