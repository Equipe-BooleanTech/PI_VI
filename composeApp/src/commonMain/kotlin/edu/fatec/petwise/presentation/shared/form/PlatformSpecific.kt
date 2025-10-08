package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

expect class PlatformFormBehavior {
    fun shouldShowKeyboardSpacer(): Boolean
    fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean
    fun getSafeAreaInsets(): PaddingValues
    fun getOptimalFieldHeight(): Dp
    fun supportsHapticFeedback(): Boolean
    fun supportsSystemDarkMode(): Boolean
}

expect object PlatformFormStyling {
    fun getFieldShape(): Shape
    fun getFieldColors(colorScheme: ColorScheme): FieldColors
    fun getTypography(): PlatformTypography
    fun getSpacing(): PlatformSpacing
}

expect class PlatformInputHandling {
    fun formatPhoneNumber(input: String, country: String = "BR"): String
    fun formatCurrency(amount: Double, currency: String = "BRL"): String
    fun formatDate(timestamp: Long, format: String = "dd/MM/yyyy"): String
    fun validatePlatformSpecific(fieldType: FormFieldType, value: String): Boolean
}

expect class PlatformFileHandling {
    suspend fun pickFile(mimeTypes: List<String>): PlatformFile?
    suspend fun pickImage(): PlatformFile?
    suspend fun uploadFile(file: PlatformFile, endpoint: String): Result<String>
}

data class FieldColors(
    val background: androidx.compose.ui.graphics.Color,
    val border: androidx.compose.ui.graphics.Color,
    val focusedBorder: androidx.compose.ui.graphics.Color,
    val errorBorder: androidx.compose.ui.graphics.Color,
    val text: androidx.compose.ui.graphics.Color,
    val label: androidx.compose.ui.graphics.Color,
    val placeholder: androidx.compose.ui.graphics.Color
)

data class PlatformTypography(
    val fieldLabel: TextStyle,
    val fieldText: TextStyle,
    val errorText: TextStyle,
    val helperText: TextStyle
)

data class PlatformSpacing(
    val fieldPadding: PaddingValues,
    val fieldSpacing: Dp,
    val sectionSpacing: Dp
)

data class PlatformFile(
    val name: String,
    val path: String,
    val size: Long,
    val mimeType: String,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatformFile) return false

        if (name != other.name) return false
        if (path != other.path) return false
        if (size != other.size) return false
        if (mimeType != other.mimeType) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

class PlatformFormConfigurationBuilder {

    companion object {
        fun createOptimizedConfiguration(
            baseConfiguration: FormConfiguration,
            platform: PlatformFormBehavior
        ): FormConfiguration {
            val optimizedFields = baseConfiguration.fields.map { field ->
                when {
                    platform.shouldUseNativePickerFor(field.type) -> {
                        field.copy(
                            customProperties = field.customProperties +
                            mapOf("useNativePicker" to kotlinx.serialization.json.JsonPrimitive(true))
                        )
                    }
                    else -> field
                }
            }

            val optimizedStyling = baseConfiguration.styling.copy(
                fieldHeight = platform.getOptimalFieldHeight().value.toInt()
            )

            return baseConfiguration.copy(
                fields = optimizedFields,
                styling = optimizedStyling
            )
        }
    }
}

@Composable
expect fun PlatformDatePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)

@Composable
expect fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)

@Composable
expect fun PlatformFilePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)

@Composable
expect fun PlatformCameraCapturer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)

expect object PlatformAccessibility {
    fun announceForAccessibility(message: String)
    fun setContentDescription(elementId: String, description: String)
    fun shouldUseHighContrast(): Boolean
    fun shouldUseLargeText(): Boolean
    fun shouldReduceMotion(): Boolean
}

expect object PlatformHaptics {
    fun performLightImpact()
    fun performMediumImpact()
    fun performHeavyImpact()
    fun performSelectionChanged()
    fun performNotificationSuccess()
    fun performNotificationWarning()
    fun performNotificationError()
}

expect object PlatformValidation {
    fun validateBankAccount(accountNumber: String, bankCode: String): Boolean
    fun validateGovernmentId(id: String, type: String): Boolean
    fun validatePostalCode(code: String): Boolean
    fun validateCreditCard(number: String): Boolean
}

@Composable
fun PlatformOptimizedDynamicForm(
    viewModel: DynamicFormViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    platformBehavior: PlatformFormBehavior,
    colorScheme: ColorScheme = androidx.compose.material3.MaterialTheme.colorScheme,
    onSubmitSuccess: ((Map<String, Any>) -> Unit)? = null,
    onSubmitError: ((FormError) -> Unit)? = null,
    onFieldChanged: ((String, Any?, Any?) -> Unit)? = null
) {
    val customFieldRenderer: @Composable (FormFieldDefinition, FieldState, (Any?) -> Unit) -> Boolean = { fieldDef, fieldState, onValueChange ->
        when (fieldDef.type) {
            FormFieldType.DATE -> {
                if (platformBehavior.shouldUseNativePickerFor(FormFieldType.DATE)) {
                    PlatformDatePicker(
                        fieldDefinition = fieldDef,
                        fieldState = fieldState,
                        onValueChange = { onValueChange(it) }
                    )
                    true
                } else {
                    false
                }
            }
            FormFieldType.TIME -> {
                if (platformBehavior.shouldUseNativePickerFor(FormFieldType.TIME)) {
                    PlatformTimePicker(
                        fieldDefinition = fieldDef,
                        fieldState = fieldState,
                        onValueChange = { onValueChange(it) }
                    )
                    true
                } else {
                    false
                }
            }
            FormFieldType.FILE_UPLOAD -> {
                PlatformFilePicker(
                    fieldDefinition = fieldDef,
                    fieldState = fieldState,
                    onValueChange = { onValueChange(it) }
                )
                true
            }
            else -> false
        }
    }

    DynamicForm(
        viewModel = viewModel,
        modifier = modifier,
        colorScheme = colorScheme,
        customFieldRenderer = customFieldRenderer,
        onSubmitSuccess = onSubmitSuccess,
        onSubmitError = onSubmitError,
        onFieldChanged = onFieldChanged
    )
}