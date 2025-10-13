package edu.fatec.petwise.presentation.shared.form

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SelectOption(
    val key: String,
    val value: String
)

@Serializable
@Immutable
data class FormConfiguration(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val layout: FormLayout = FormLayout(),
    val fields: List<FormFieldDefinition>,
    val validationBehavior: ValidationBehavior = ValidationBehavior.ON_SUBMIT,
    val submitBehavior: SubmitBehavior = SubmitBehavior.DEFAULT,
    val apiConfiguration: ApiConfiguration? = null,
    val styling: FormStyling = FormStyling()
)

@Serializable
@Immutable
data class FormLayout(
    val columns: Int = 1,
    val maxWidth: Int? = null,
    val spacing: Int = 16,
    val responsive: Boolean = true
)

@Serializable
@Immutable
data class FormFieldDefinition(
    val id: String,
    val label: String? = null,
    val type: FormFieldType,
    val placeholder: String? = null,
    val description: String? = null,
    val options: List<String>? = null,
    val selectOptions: List<SelectOption>? = null,
    val default: JsonElement? = null,
    val validators: List<ValidationRule> = emptyList(),
    val visibility: VisibilityRule? = null,
    val formatting: FieldFormatting? = null,
    val accessibility: AccessibilityConfig = AccessibilityConfig(),
    val customProperties: Map<String, JsonElement> = emptyMap()
)

@Serializable
enum class FormFieldType {
    TEXT,
    EMAIL,
    PASSWORD,
    PHONE,
    NUMBER,
    DECIMAL,
    SELECT,
    MULTI_SELECT,
    RADIO,
    CHECKBOX,
    SWITCH,
    SEGMENTED_CONTROL,
    DATE,
    TIME,
    DATETIME,
    TEXTAREA,
    FILE_UPLOAD,
    SUBMIT,
    BUTTON,
    DIVIDER,
    SPACER,
    CUSTOM
}

@Serializable
@Immutable
data class ValidationRule(
    val type: ValidationType,
    val message: String? = null,
    val value: JsonElement? = null,
    val field: String? = null,
    val customValidator: String? = null,
    val async: Boolean = false,
    val debounceMs: Long = 300
)

@Serializable
enum class ValidationType {
    REQUIRED,
    EMAIL,
    PHONE,
    CPF,
    CNPJ,
    CEP,
    MIN_LENGTH,
    MAX_LENGTH,
    PATTERN,
    NUMERIC,
    DECIMAL,
    DATE,
    PASSWORD_STRENGTH,
    MATCHES_FIELD,
    UNIQUE,
    CUSTOM,
    API_VALIDATION
}

@Serializable
enum class ValidationBehavior {
    ON_CHANGE,
    ON_BLUR,
    ON_SUBMIT,
    DEBOUNCED
}

@Serializable
enum class SubmitBehavior {
    DEFAULT,
    API_CALL,
    CUSTOM_HANDLER,
    MULTI_STEP
}

@Serializable
@Immutable
data class VisibilityRule(
    val conditions: List<VisibilityCondition>,
    val operator: LogicalOperator = LogicalOperator.AND
)

@Serializable
@Immutable
data class VisibilityCondition(
    val fieldId: String,
    val operator: ComparisonOperator,
    val value: JsonElement
)

@Serializable
enum class LogicalOperator { AND, OR }

@Serializable
enum class ComparisonOperator {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    NOT_CONTAINS,
    GREATER_THAN,
    LESS_THAN,
    IN_LIST,
    NOT_IN_LIST
}

@Serializable
@Immutable
data class FieldFormatting(
    val mask: String? = null,
    val prefix: String? = null,
    val suffix: String? = null,
    val capitalize: Boolean = false,
    val uppercase: Boolean = false,
    val lowercase: Boolean = false
)

@Serializable
@Immutable
data class AccessibilityConfig(
    val contentDescription: String? = null,
    val semanticsRole: String? = null,
    val announcements: List<String> = emptyList()
)

@Serializable
@Immutable
data class ApiConfiguration(
    val submitUrl: String? = null,
    val validationEndpoints: Map<String, String> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
    val timeout: Long = 10000
)

@Serializable
@Immutable
data class FormStyling(
    val primaryColor: String = "#007AFF",
    val errorColor: String = "#FF3B30",
    val successColor: String = "#34C759",
    val warningColor: String = "#FF9500",
    val backgroundColor: String = "#FFFFFF",
    val fieldHeight: Int = 56,
    val borderRadius: Int = 8,
    val spacing: Int = 16
)