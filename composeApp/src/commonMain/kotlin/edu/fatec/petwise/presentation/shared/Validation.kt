package edu.fatec.petwise.features.auth.shared

import kotlinx.serialization.json.JsonPrimitive

data class FieldState(
    val id: String,
    val value: String = "",
    val visible: Boolean = true,
    val errors: List<String> = emptyList(),
    val touched: Boolean = false,
    val submitted: Boolean = false
)

object Validators {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val phoneRegex = Regex("^\\+?[0-9]{10,15}$")
    private val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W]{8,}$")
    private val cpfRegex = Regex("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$")
    private val cnpjRegex = Regex("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")
    private val zipRegex = Regex("^\\d{5}-\\d{3}$")
    
    private val rawCpfRegex = Regex("^\\d{11}$")
    private val rawCnpjRegex = Regex("^\\d{14}$")
    private val rawPhoneRegex = Regex("^\\d{10,11}$")
    private val rawZipRegex = Regex("^\\d{8}$")
    
    private var _schema: FormSchema? = null
    private var _formSubmitted = false

    fun setSchema(schema: FormSchema) {
        _schema = schema
    }
    
    fun setFormSubmitted(submitted: Boolean) {
        _formSubmitted = submitted
    }
    
    private fun isMaskedField(fieldId: String): Boolean {
        return fieldId in listOf("cpf", "cnpj", "phone", "cep", "crmv")
    }
    
    fun validate(field: Field, values: Map<String, String>): List<String> {
        val validators = field.validators ?: return emptyList()
        val value = values[field.id].orEmpty()
        val errors = mutableListOf<String>()
        
        val fieldLabel = field.label ?: field.id.capitalize()

        for (validator in validators) {
            when (validator.type) {
                "required" -> if (value.isBlank()) {
                    errors += validator.message?.takeIf { it.isNotBlank() } 
                        ?: "$fieldLabel é obrigatório"
                }

                "email" -> if (value.isNotBlank() && !emailRegex.matches(value)) {
                    errors += validator.message?.takeIf { it.isNotBlank() } 
                        ?: "Email inválido, verifique o formato"
                }

                "minLength" -> {
                    val min = (validator.value as? JsonPrimitive)?.intOrNull ?: 0
                    val valueToCheck = if (isMaskedField(field.id)) {
                        value.filter { it.isDigit() || it.isLetter() }
                    } else {
                        value
                    }
                    if (value.isNotBlank() && valueToCheck.length < min) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "$fieldLabel deve ter no mínimo $min caracteres"
                    }
                }

                "maxLength" -> {
                    val max = (validator.value as? JsonPrimitive)?.intOrNull ?: Int.MAX_VALUE
                    val valueToCheck = if (isMaskedField(field.id)) {
                        value.filter { it.isDigit() || it.isLetter() }
                    } else {
                        value
                    }
                    if (valueToCheck.length > max) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "$fieldLabel deve ter no máximo $max caracteres"
                    }
                }
                
                "matchesField" -> {
                    validator.field?.let { fieldToMatch ->
                        val matchValue = values[fieldToMatch].orEmpty()
                        val matchField = _schema?.fields?.find { it.id == fieldToMatch }
                        val matchFieldLabel = matchField?.label?.lowercase() ?: fieldToMatch.capitalize()
                        
                        if (value.isNotBlank() && matchValue.isNotBlank() && value != matchValue) {
                            errors += validator.message?.takeIf { it.isNotBlank() }
                                ?: "$fieldLabel deve ser igual ao $matchFieldLabel"
                        }
                    }
                }
                
                "pattern" -> {
                    validator.value?.let { patternValue ->
                        val pattern = patternValue.toString()
                        if (value.isNotBlank() && !Regex(pattern).matches(value)) {
                            errors += validator.message?.takeIf { it.isNotBlank() }
                                ?: "$fieldLabel formato inválido"
                        }
                    }
                }
                
                "phone" -> if (value.isNotBlank()) {
                    val rawValue = value.filter { it.isDigit() }
                    if (!rawPhoneRegex.matches(rawValue)) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "Telefone inválido (deve ter 10-11 dígitos)"
                    }
                }
                
                "cpf" -> if (value.isNotBlank()) {
                    val rawValue = value.filter { it.isDigit() }
                    if (!rawCpfRegex.matches(rawValue)) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "CPF inválido (deve ter 11 dígitos)"
                    }
                }
                
                "cnpj" -> if (value.isNotBlank()) {
                    val rawValue = value.filter { it.isDigit() }
                    if (!rawCnpjRegex.matches(rawValue)) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "CNPJ inválido (deve ter 14 dígitos)"
                    }
                }
                
                "cep" -> if (value.isNotBlank()) {
                    val rawValue = value.filter { it.isDigit() }
                    if (!rawZipRegex.matches(rawValue)) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "CEP inválido (deve ter 8 dígitos)"
                    }
                }
                
                "number" -> if (value.isNotBlank() && value.toDoubleOrNull() == null) {
                    errors += validator.message?.takeIf { it.isNotBlank() }
                        ?: "$fieldLabel deve ser um número"
                }
                
                "min" -> {
                    val min = (validator.value as? JsonPrimitive)?.content?.toDoubleOrNull() ?: 0.0
                    val numValue = value.toDoubleOrNull()
                    if (numValue != null && numValue < min) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "$fieldLabel deve ser maior ou igual a $min"
                    }
                }
                
                "max" -> {
                    val max = (validator.value as? JsonPrimitive)?.content?.toDoubleOrNull() ?: Double.MAX_VALUE
                    val numValue = value.toDoubleOrNull()
                    if (numValue != null && numValue > max) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "$fieldLabel deve ser menor ou igual a $max"
                    }
                }
                
                "password" -> if (value.isNotBlank() && !passwordRegex.matches(value)) {
                    errors += validator.message?.takeIf { it.isNotBlank() }
                        ?: "Senha deve conter ao menos 8 caracteres, incluindo letras e números"
                }
                
                "custom" -> {
                    validator.message?.takeIf { it.isNotBlank() }?.let {
                        errors += it
                    }
                }
            }
        }
        return errors
    }
}

private val JsonPrimitive.intOrNull: Int?
    get() = this.content.toIntOrNull()

private fun String.capitalize(): String {
    return if (this.isNotEmpty()) this.first().uppercaseChar() + this.substring(1) else this
}

fun formatValidationError(field: Field, errorType: String, value: String? = null): String {
    val fieldLabel = field.label ?: field.id.capitalize()
    return when (errorType) {
        "required" -> "$fieldLabel é obrigatório"
        "email" -> "Email inválido, verifique o formato"
        "minLength" -> "$fieldLabel deve ter no mínimo ${value ?: ""} caracteres"
        "maxLength" -> "$fieldLabel deve ter no máximo ${value ?: ""} caracteres"
        "pattern" -> "Formato inválido para $fieldLabel"
        "matchesField" -> "$fieldLabel não confere"
        "phone" -> "Telefone inválido"
        "cpf" -> "CPF inválido"
        "cnpj" -> "CNPJ inválido"
        "cep" -> "CEP inválido"
        "password" -> "Senha deve conter ao menos 8 caracteres, incluindo letras e números"
        else -> "$fieldLabel inválido"
    }
}
