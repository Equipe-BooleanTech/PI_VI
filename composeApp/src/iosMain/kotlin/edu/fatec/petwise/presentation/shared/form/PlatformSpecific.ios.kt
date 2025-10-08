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
import platform.Foundation.*
import platform.UIKit.*

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

        val window = UIApplication.sharedApplication.keyWindow
        val safeAreaInsets = window?.safeAreaInsets
        return PaddingValues(
            top = (safeAreaInsets?.top ?: 0.0).dp,
            bottom = (safeAreaInsets?.bottom ?: 0.0).dp,
            start = (safeAreaInsets?.left ?: 0.0).dp,
            end = (safeAreaInsets?.right ?: 0.0).dp
        )
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

        return formatter.stringFromNumber(NSNumber.numberWithDouble(amount)) ?: "$amount"
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
                val matches = detector.matchesInString(value, NSMatchingOptions.MIN_VALUE, range)

                return matches.size > 0
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
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {

    androidx.compose.material3.Text(
        text = "iOS Date Picker - ${fieldState.displayValue}",
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
        text = "iOS Time Picker - ${fieldState.displayValue}",
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
        androidx.compose.material3.Text("Choose File")
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

        UIAccessibilityPostNotification(UIAccessibilityAnnouncementNotification, message)
    }

    actual fun setContentDescription(elementId: String, description: String) {

    }

    actual fun shouldUseHighContrast(): Boolean {
        return UIAccessibilityIsReduceTransparencyEnabled()
    }

    actual fun shouldUseLargeText(): Boolean {
        return UIApplication.sharedApplication.preferredContentSizeCategory >= UIContentSizeCategoryAccessibilityMedium
    }

    actual fun shouldReduceMotion(): Boolean {
        return UIAccessibilityIsReduceMotionEnabled()
    }
}

actual object PlatformHaptics {
    actual fun performLightImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleLight)
        generator.impactOccurred()
    }

    actual fun performMediumImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleMedium)
        generator.impactOccurred()
    }

    actual fun performHeavyImpact() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleHeavy)
        generator.impactOccurred()
    }

    actual fun performSelectionChanged() {
        val generator = UISelectionFeedbackGenerator()
        generator.selectionChanged()
    }

    actual fun performNotificationSuccess() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackTypeSuccess)
    }

    actual fun performNotificationWarning() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackTypeWarning)
    }

    actual fun performNotificationError() {
        val generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(UINotificationFeedbackTypeError)
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

        val cepRegex = "^\\d{5}-?\\d{3}$"
        return NSPredicate.predicateWithFormat("SELF MATCHES %@", cepRegex)
            .evaluateWithObject(code)
    }

    actual fun validateCreditCard(number: String): Boolean {

        let cleanNumber = number.replacingOccurrences(of: "[^0-9]", with: "", options: .regularExpression)

        guard cleanNumber.count >= 13 && cleanNumber.count <= 19 else { return false }

        var sum = 0
        let reversedDigits = Array(cleanNumber.reversed())

        for (index, digitChar) in reversedDigits.enumerated() {
            guard let digit = Int(String(digitChar)) else { return false }

            if index % 2 == 1 {
                let doubled = digit * 2
                sum += doubled > 9 ? doubled - 9 : doubled
            } else {
                sum += digit
            }
        }

        return sum % 10 == 0
    }
}