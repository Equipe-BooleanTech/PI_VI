package edu.fatec.petwise.presentation.shared.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant

/**
 * Engine de validação para formulários, suportando várias regras de validação
 */
interface ValidationEngine {
    suspend fun validateField(
        field: FormFieldDefinition,
        value: Any?,
        allValues: Map<String, Any>
    ): ValidationResult

    suspend fun validateForm(
        configuration: FormConfiguration,
        values: Map<String, Any>
    ): ValidationResult
}

/**
 * Implementação padrão da ValidationEngine
 */
class DefaultValidationEngine(
    private val errorMessageProvider: ErrorMessageProvider = DefaultErrorMessageProvider()
) : ValidationEngine {

    override suspend fun validateField(
        field: FormFieldDefinition,
        value: Any?,
        allValues: Map<String, Any>
    ): ValidationResult {
        val errors = mutableListOf<FormError.ValidationError>()
        
        // Handle LocalDateTime values for date/time fields
        val stringValue = when {
            value is LocalDateTime -> {
                when (field.type) {
                    FormFieldType.DATE -> {
                        // For DATE fields, format as user-friendly date only
                        val date = value.date
                        "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
                    }
                    FormFieldType.TIME -> {
                        // For TIME fields, format as time only
                        val time = value.time
                        "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
                    }
                    FormFieldType.DATETIME -> {
                        // For DATETIME fields, format as date and time
                        val date = value.date
                        val time = value.time
                        "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year} ${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
                    }
                    else -> value.toString()
                }
            }
            value is LocalDate -> {
                "${value.dayOfMonth.toString().padStart(2, '0')}/${value.monthNumber.toString().padStart(2, '0')}/${value.year}"
            }
            else -> value?.toString() ?: ""
        }

        for (rule in field.validators) {
            val validationError = validateRule(rule, field.id, value, stringValue, allValues)
            if (validationError != null) {
                errors.add(validationError)
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    override suspend fun validateForm(
        configuration: FormConfiguration,
        values: Map<String, Any>
    ): ValidationResult {
        val allErrors = mutableListOf<FormError.ValidationError>()

        for (field in configuration.fields) {
            if (field.type == FormFieldType.SUBMIT) continue

            val fieldValue = values[field.id]
            val fieldResult = validateField(field, fieldValue, values)

            if (fieldResult is ValidationResult.Invalid) {
                allErrors.addAll(fieldResult.errors)
            }
        }

        return if (allErrors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(allErrors)
        }
    }

    private suspend fun validateRule(
        rule: ValidationRule,
        fieldId: String,
        rawValue: Any?,
        stringValue: String,
        allValues: Map<String, Any>
    ): FormError.ValidationError? {

        when (rule.type) {
            ValidationType.MAX_DATE -> {
                if (rawValue == null) return null
                val maxDateMillis = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toLongOrNull()
                } ?: return null
                
                return try {
                    val dateMillis = when (rawValue) {
                        is LocalDateTime -> rawValue.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        is LocalDate -> rawValue.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        is String -> parseDateToEpochMillis(rawValue)
                        else -> parseDateToEpochMillis(stringValue)
                    } ?: return FormError.ValidationError(
                        id = "${fieldId}_max_date_${currentTimeMs()}",
                        message = rule.message ?: "Data inválida",
                        fieldId = fieldId,
                        validationType = rule.type
                    )
                    if (dateMillis > maxDateMillis) {
                        FormError.ValidationError(
                            id = "${fieldId}_max_date_${currentTimeMs()}",
                            message = rule.message ?: "Data não pode ser no futuro",
                            fieldId = fieldId,
                            validationType = rule.type
                        )
                    } else null
                } catch (e: Exception) {
                    FormError.ValidationError(
                        id = "${fieldId}_max_date_${currentTimeMs()}",
                        message = rule.message ?: "Data inválida",
                        fieldId = fieldId,
                        validationType = rule.type
                    )
                }
            }

            ValidationType.MIN_DATE -> {
                if (rawValue == null) return null
                val minDateMillis = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toLongOrNull()
                } ?: return null
                
                return try {
                    val dateMillis = when (rawValue) {
                        is LocalDateTime -> rawValue.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        is LocalDate -> rawValue.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        is String -> parseDateToEpochMillis(rawValue)
                        else -> parseDateToEpochMillis(stringValue)
                    } ?: return FormError.ValidationError(
                        id = "${fieldId}_min_date_${currentTimeMs()}",
                        message = rule.message ?: "Data inválida",
                        fieldId = fieldId,
                        validationType = rule.type
                    )
                    if (dateMillis < minDateMillis) {
                        FormError.ValidationError(
                            id = "${fieldId}_min_date_${currentTimeMs()}",
                            message = rule.message ?: "Data muito antiga",
                            fieldId = fieldId,
                            validationType = rule.type
                        )
                    } else null
                } catch (e: Exception) {
                    FormError.ValidationError(
                        id = "${fieldId}_min_date_${currentTimeMs()}",
                        message = rule.message ?: "Data inválida",
                        fieldId = fieldId,
                        validationType = rule.type
                    )
                }
            }
            else -> {} 
        }

        val isValid = when (rule.type) {
            ValidationType.REQUIRED -> when (rawValue) {
                is LocalDateTime, is LocalDate -> true
                else -> stringValue.isNotBlank()
            }

            ValidationType.EMAIL -> {
                if (stringValue.isBlank()) return null
                stringValue.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            }

            ValidationType.PHONE -> {
                if (stringValue.isBlank()) return null
                val rawPhone = stringValue.replace(Regex("[^0-9]"), "")
                rawPhone.matches(Regex("^\\d{10,11}$"))
            }

            ValidationType.CPF -> {
                if (stringValue.isBlank()) return null
                validateCPF(stringValue.replace(Regex("[^0-9]"), ""))
            }

            ValidationType.CNPJ -> {
                if (stringValue.isBlank()) return null
                validateCNPJ(stringValue.replace(Regex("[^0-9]"), ""))
            }

            ValidationType.CEP -> {
                if (stringValue.isBlank()) return null
                val rawCep = stringValue.replace(Regex("[^0-9]"), "")
                rawCep.matches(Regex("^\\d{8}$"))
            }

            ValidationType.MIN_LENGTH -> {
                val minLength = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toIntOrNull()
                } ?: 0
                stringValue.length >= minLength
            }

            ValidationType.MAX_LENGTH -> {
                val maxLength = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toIntOrNull()
                } ?: Int.MAX_VALUE
                stringValue.length <= maxLength
            }

            ValidationType.PATTERN -> {
                if (stringValue.isBlank()) return null
                val pattern = rule.value?.let {
                    (it as? JsonPrimitive)?.content
                } ?: return null
                try {
                    stringValue.matches(Regex(pattern))
                } catch (e: Exception) {
                    false
                }
            }

            ValidationType.NUMERIC -> {
                if (stringValue.isBlank()) return null
                stringValue.matches(Regex("^[0-9]+$"))
            }

            ValidationType.DECIMAL -> {
                if (stringValue.isBlank()) return null
                try {
                    stringValue.toDouble()
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            }

            ValidationType.PASSWORD_STRENGTH -> {
                if (stringValue.isBlank()) return null
                stringValue.length >= 8 &&
                stringValue.any { it.isLetter() } &&
                stringValue.any { it.isDigit() }
            }

            ValidationType.MATCHES_FIELD -> {
                val otherFieldId = rule.field ?: return null
                val otherValue = allValues[otherFieldId]?.toString() ?: ""
                stringValue == otherValue
            }

            ValidationType.DATE -> {
                when (rawValue) {
                    is LocalDateTime, is LocalDate -> true
                    else -> {
                        if (stringValue.isBlank()) return null
                        try {
                            parseDateToEpochMillis(stringValue) != null
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }

            ValidationType.MAX_DATE, ValidationType.MIN_DATE -> {
                true
            }

            ValidationType.DATE_RANGE -> {
                when (rawValue) {
                    is LocalDateTime, is LocalDate -> true
                    else -> {
                        if (stringValue.isBlank()) return null
                        try {
                            parseDateToEpochMillis(stringValue) != null
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }

            ValidationType.UNIQUE -> {
                true
            }

            ValidationType.API_VALIDATION -> {
                true
            }

            ValidationType.CUSTOM -> {
                true
            }
        }

        return if (isValid) {
            null
        } else {
            val message = rule.message ?: run {
                errorMessageProvider.getErrorMessage(
                    rule.type,
                    fieldId
                ).getOrNull() ?: "Validation failed"
            }

            FormError.ValidationError(
                id = "${fieldId}_${rule.type.name.lowercase()}_${currentTimeMs()}",
                message = message,
                fieldId = fieldId,
                validationType = rule.type
            )
        }
    }

    private fun validateCPF(cpf: String): Boolean {
       return true
    }

    private fun validateCNPJ(cnpj: String): Boolean {
        if (cnpj.length != 14) return false
        if (cnpj.all { it == cnpj[0] }) return false

        val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val sum1 = (0..11).sumOf { i -> cnpj[i].digitToInt() * weights1[i] }
        val digit1 = ((sum1 % 11).let { if (it < 2) 0 else 11 - it })

        if (digit1 != cnpj[12].digitToInt()) return false

        val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val sum2 = (0..12).sumOf { i -> cnpj[i].digitToInt() * weights2[i] }
        val digit2 = ((sum2 % 11).let { if (it < 2) 0 else 11 - it })

        return digit2 == cnpj[13].digitToInt()
    }

    private fun parseDateToEpochMillis(dateString: String): Long? {
        return try {
            // Clean the input string first
            val cleanedString = dateString.trim()
            
            when {
                cleanedString.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> {
                    val local = LocalDate.parse(cleanedString)
                    val instant = local.atStartOfDayIn(TimeZone.currentSystemDefault())
                    instant.toEpochMilliseconds()
                }
                cleanedString.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$")) -> {
                    val parts = cleanedString.split('/')
                    if (parts.size != 3) return null
                    val day = parts[0].toIntOrNull() ?: return null
                    val month = parts[1].toIntOrNull() ?: return null
                    val year = parts[2].toIntOrNull() ?: return null
                    
                    // Validate ranges
                    if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2100) {
                        return null
                    }
                    
                    val local = LocalDate(year, month, day)
                    val instant = local.atStartOfDayIn(TimeZone.currentSystemDefault())
                    instant.toEpochMilliseconds()
                }
                cleanedString.contains('T') -> {
                    // Handle LocalDateTime strings
                    val dateTime = LocalDateTime.parse(cleanedString)
                    dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                }
                else -> null
            }
        } catch (t: Throwable) {
            null
        }
    }

    private fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
}

class DefaultFormEventDispatcher : FormEventDispatcher {

    private val handlers = mutableListOf<FormEventHandler>()
    private val _events = MutableSharedFlow<FormEvent>(extraBufferCapacity = 64)
    override val events: Flow<FormEvent> = _events.asSharedFlow()

    override suspend fun dispatch(event: FormEvent) {
        _events.emit(event)

        val sortedHandlers = handlers.sortedByDescending { it.priority }

        for (handler in sortedHandlers) {
            if (handler.canHandle(event)) {
                try {
                    val result = handler.handleEvent(event)
                    when (result) {
                        is EventHandlingResult.Propagate -> {
                            dispatch(result.newEvent)
                        }
                        is EventHandlingResult.Multiple -> {
                            result.results.forEach { nestedResult ->
                                if (nestedResult is EventHandlingResult.Propagate) {
                                    dispatch(nestedResult.newEvent)
                                }
                            }
                        }
                        EventHandlingResult.Handled -> {
                        }
                        EventHandlingResult.Ignored -> {
                        }
                    }
                } catch (e: Exception) {
                    _events.emit(
                        FormEvent.ErrorOccurred(
                            formId = event.formId,
                            error = FormError.SystemError(
                                id = "handler_error_${currentTimeMs()}",
                                message = "Event handler error: ${e.message}",
                                exception = e
                            )
                        )
                    )
                }
            }
        }
    }

    override fun registerHandler(handler: FormEventHandler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler)
        }
    }

    override fun unregisterHandler(handler: FormEventHandler) {
        handlers.remove(handler)
    }
}