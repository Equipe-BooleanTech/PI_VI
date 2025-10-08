package edu.fatec.petwise.presentation.shared.form.validation

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

class PetWiseValidationEngine : ValidationEngine {
    
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W]{8,}$")
    
    private val rawCpfRegex = Regex("^\\d{11}$")
    private val rawCnpjRegex = Regex("^\\d{14}$")
    private val rawPhoneRegex = Regex("^\\d{10,11}$")
    private val rawZipRegex = Regex("^\\d{8}$")
    
    private val maskedFields = setOf("cpf", "cnpj", "phone", "cep", "crmv")
    
    override suspend fun validateField(
        field: FormFieldDefinition,
        value: Any?,
        allValues: Map<String, Any>
    ): ValidationResult {
        val stringValue = value?.toString() ?: ""
        val errors = mutableListOf<FormError.ValidationError>()
        
        val fieldLabel = field.label ?: field.id.capitalize()
        
        for (rule in field.validators) {
            val error = validateRule(field, rule, stringValue, allValues, fieldLabel)
            if (error != null) {
                errors.add(error)
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
    
    private fun validateRule(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        allValues: Map<String, Any>,
        fieldLabel: String
    ): FormError.ValidationError? {
        
        return when (rule.type) {
            ValidationType.REQUIRED -> validateRequired(field, rule, value, fieldLabel)
            ValidationType.EMAIL -> validateEmail(field, rule, value, fieldLabel)
            ValidationType.PHONE -> validatePhone(field, rule, value, fieldLabel)
            ValidationType.CPF -> validateCpf(field, rule, value, fieldLabel)
            ValidationType.CNPJ -> validateCnpj(field, rule, value, fieldLabel)
            ValidationType.CEP -> validateCep(field, rule, value, fieldLabel)
            ValidationType.MIN_LENGTH -> validateMinLength(field, rule, value, fieldLabel)
            ValidationType.MAX_LENGTH -> validateMaxLength(field, rule, value, fieldLabel)
            ValidationType.PATTERN -> validatePattern(field, rule, value, fieldLabel)
            ValidationType.NUMERIC -> validateNumeric(field, rule, value, fieldLabel)
            ValidationType.DECIMAL -> validateDecimal(field, rule, value, fieldLabel)
            ValidationType.PASSWORD_STRENGTH -> validatePassword(field, rule, value, fieldLabel)
            ValidationType.MATCHES_FIELD -> validateMatches(field, rule, value, allValues, fieldLabel)
            else -> null
        }
    }
    
    private fun validateRequired(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        return if (value.isBlank()) {
            createValidationError(
                field, 
                rule, 
                rule.message ?: "$fieldLabel é obrigatório"
            )
        } else null
    }
    
    private fun validateEmail(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        return if (value.isNotBlank() && !emailRegex.matches(value)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "Email inválido, verifique o formato"
            )
        } else null
    }
    
    private fun validatePhone(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        if (value.isBlank()) return null
        
        val rawValue = value.filter { it.isDigit() }
        return if (!rawPhoneRegex.matches(rawValue)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "Telefone inválido (deve ter 10-11 dígitos)"
            )
        } else null
    }
    
    private fun validateCpf(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        if (value.isBlank()) return null
        
        val rawValue = value.filter { it.isDigit() }
        return if (!rawCpfRegex.matches(rawValue) || !isValidCpf(rawValue)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "CPF inválido (deve ter 11 dígitos)"
            )
        } else null
    }
    
    private fun validateCnpj(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        if (value.isBlank()) return null
        
        val rawValue = value.filter { it.isDigit() }
        return if (!rawCnpjRegex.matches(rawValue) || !isValidCnpj(rawValue)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "CNPJ inválido (deve ter 14 dígitos)"
            )
        } else null
    }
    
    private fun validateCep(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        if (value.isBlank()) return null
        
        val rawValue = value.filter { it.isDigit() }
        return if (!rawZipRegex.matches(rawValue)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "CEP inválido (deve ter 8 dígitos)"
            )
        } else null
    }
    
    private fun validateMinLength(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        val minLength = rule.value?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val valueToCheck = if (field.id in maskedFields) {
            value.filter { it.isDigit() || it.isLetter() }
        } else {
            value
        }
        
        return if (value.isNotBlank() && valueToCheck.length < minLength) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel deve ter no mínimo $minLength caracteres"
            )
        } else null
    }
    
    private fun validateMaxLength(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        val maxLength = rule.value?.jsonPrimitive?.content?.toIntOrNull() ?: Int.MAX_VALUE
        val valueToCheck = if (field.id in maskedFields) {
            value.filter { it.isDigit() || it.isLetter() }
        } else {
            value
        }
        
        return if (valueToCheck.length > maxLength) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel deve ter no máximo $maxLength caracteres"
            )
        } else null
    }
    
    private fun validatePattern(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        val pattern = rule.value?.jsonPrimitive?.content ?: return null
        
        return if (value.isNotBlank() && !Regex(pattern).matches(value)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel formato inválido"
            )
        } else null
    }
    
    private fun validateNumeric(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        return if (value.isNotBlank() && value.toDoubleOrNull() == null) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel deve ser um número"
            )
        } else null
    }
    
    private fun validateDecimal(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        return if (value.isNotBlank() && value.toDoubleOrNull() == null) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel deve ser um número válido"
            )
        } else null
    }
    
    private fun validatePassword(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        fieldLabel: String
    ): FormError.ValidationError? {
        return if (value.isNotBlank() && !passwordRegex.matches(value)) {
            createValidationError(
                field,
                rule,
                rule.message ?: "Senha deve conter ao menos 8 caracteres, incluindo letras e números"
            )
        } else null
    }
    
    private fun validateMatches(
        field: FormFieldDefinition,
        rule: ValidationRule,
        value: String,
        allValues: Map<String, Any>,
        fieldLabel: String
    ): FormError.ValidationError? {
        val fieldToMatch = rule.field ?: return null
        val matchValue = allValues[fieldToMatch]?.toString() ?: ""
        
        return if (value.isNotBlank() && matchValue.isNotBlank() && value != matchValue) {
            createValidationError(
                field,
                rule,
                rule.message ?: "$fieldLabel deve ser igual ao campo referenciado"
            )
        } else null
    }
    
    private fun createValidationError(
        field: FormFieldDefinition,
        rule: ValidationRule,
        message: String
    ): FormError.ValidationError {
        return FormError.ValidationError(
            id = "validation_${field.id}_${rule.type.name.lowercase()}_${currentTimeMs()}",
            message = message,
            fieldId = field.id,
            validationType = rule.type
        )
    }
    
    private fun isValidCpf(cpf: String): Boolean {
        if (cpf.length != 11) return false
        
        if (cpf.all { it == cpf[0] }) return false
        
        val sum1 = (0..8).sumOf { i -> (cpf[i].digitToInt() * (10 - i)) }
        val digit1 = ((sum1 * 10) % 11).let { if (it == 10) 0 else it }
        
        if (digit1 != cpf[9].digitToInt()) return false
        
        val sum2 = (0..9).sumOf { i -> (cpf[i].digitToInt() * (11 - i)) }
        val digit2 = ((sum2 * 10) % 11).let { if (it == 10) 0 else it }
        
        return digit2 == cpf[10].digitToInt()
    }
    
    private fun isValidCnpj(cnpj: String): Boolean {
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
    
    override suspend fun validateForm(
        configuration: FormConfiguration,
        values: Map<String, Any>
    ): ValidationResult {
        val errors = mutableListOf<FormError.ValidationError>()
        
        for (field in configuration.fields) {
            val fieldValue = values[field.id]
            val validationResult = validateField(field, fieldValue, values)
            
            if (validationResult is ValidationResult.Invalid) {
                errors.addAll(validationResult.errors)
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun String.capitalize(): String {
        return if (this.isNotEmpty()) this.first().uppercaseChar() + this.substring(1) else this
    }
}