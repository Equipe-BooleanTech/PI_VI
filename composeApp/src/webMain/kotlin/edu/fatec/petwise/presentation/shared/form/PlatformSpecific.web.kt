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
    androidx.compose.material3.Text(text = "Web Date Picker - ${fieldState.displayValue}", modifier = modifier)
}

@Composable
actual fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Text(text = "Web Time Picker - ${fieldState.displayValue}", modifier = modifier)
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