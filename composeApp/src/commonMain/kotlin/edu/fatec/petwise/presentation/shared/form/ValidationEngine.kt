package edu.fatec.petwise.presentation.shared.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.JsonPrimitive

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
        val stringValue = value?.toString() ?: ""

        for (rule in field.validators) {
            val validationError = validateRule(rule, field.id, stringValue, allValues)
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
        value: String,
        allValues: Map<String, Any>
    ): FormError.ValidationError? {

        val isValid = when (rule.type) {
            ValidationType.REQUIRED -> value.isNotBlank()

            ValidationType.EMAIL -> {
                if (value.isBlank()) return null
                value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            }

            ValidationType.PHONE -> {
                if (value.isBlank()) return null
                val rawPhone = value.replace(Regex("[^0-9]"), "")
                rawPhone.matches(Regex("^\\d{10,11}$"))
            }

            ValidationType.CPF -> {
                if (value.isBlank()) return null
                validateCPF(value.replace(Regex("[^0-9]"), ""))
            }

            ValidationType.CNPJ -> {
                if (value.isBlank()) return null
                validateCNPJ(value.replace(Regex("[^0-9]"), ""))
            }

            ValidationType.CEP -> {
                if (value.isBlank()) return null
                val rawCep = value.replace(Regex("[^0-9]"), "")
                rawCep.matches(Regex("^\\d{8}$"))
            }

            ValidationType.MIN_LENGTH -> {
                val minLength = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toIntOrNull()
                } ?: 0
                value.length >= minLength
            }

            ValidationType.MAX_LENGTH -> {
                val maxLength = rule.value?.let {
                    (it as? JsonPrimitive)?.content?.toIntOrNull()
                } ?: Int.MAX_VALUE
                value.length <= maxLength
            }

            ValidationType.PATTERN -> {
                if (value.isBlank()) return null
                val pattern = rule.value?.let {
                    (it as? JsonPrimitive)?.content
                } ?: return null
                try {
                    value.matches(Regex(pattern))
                } catch (e: Exception) {
                    false
                }
            }

            ValidationType.NUMERIC -> {
                if (value.isBlank()) return null
                value.matches(Regex("^[0-9]+$"))
            }

            ValidationType.DECIMAL -> {
                if (value.isBlank()) return null
                try {
                    value.toDouble()
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            }

            ValidationType.PASSWORD_STRENGTH -> {
                if (value.isBlank()) return null
                value.length >= 8 &&
                value.any { it.isLetter() } &&
                value.any { it.isDigit() }
            }

            ValidationType.MATCHES_FIELD -> {
                val otherFieldId = rule.field ?: return null
                val otherValue = allValues[otherFieldId]?.toString() ?: ""
                value == otherValue
            }

            ValidationType.DATE -> {
                if (value.isBlank()) return null
                try {
                    value.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))
                } catch (e: Exception) {
                    false
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
        if (cpf.length != 11 || cpf.all { it == cpf[0] }) return false

        val digits = cpf.map { it.toString().toInt() }

        val sum1 = (0..8).sumOf { digits[it] * (10 - it) }
        val digit1 = ((sum1 * 10) % 11).let { if (it < 2) 0 else 11 - it }

        if (digits[9] != digit1) return false

        val sum2 = (0..9).sumOf { digits[it] * (11 - it) }
        val digit2 = ((sum2 * 10) % 11).let { if (it < 2) 0 else 11 - it }

        return digits[10] == digit2
    }

    private fun validateCNPJ(cnpj: String): Boolean {
        if (cnpj.length != 14 || cnpj.all { it == cnpj[0] }) return false

        val digits = cnpj.map { it.toString().toInt() }
        val weights1 = listOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val weights2 = listOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

        val sum1 = (0..11).sumOf { digits[it] * weights1[it] }
        val digit1 = sum1 % 11
        val checkDigit1 = if (digit1 < 2) 0 else 11 - digit1

        if (digits[12] != checkDigit1) return false

        val sum2 = (0..12).sumOf { digits[it] * weights2[it] }
        val digit2 = sum2 % 11
        val checkDigit2 = if (digit2 < 2) 0 else 11 - digit2

        return digits[13] == checkDigit2
    }
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