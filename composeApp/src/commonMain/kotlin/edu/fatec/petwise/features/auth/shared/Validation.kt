package edu.fatec.petwise.features.auth.shared

import kotlinx.serialization.json.JsonPrimitive

data class FieldState(
    val id: String,
    val value: String = "",
    val visible: Boolean = true,
    val errors: List<String> = emptyList()
)

object Validators {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun validate(field: Field, values: Map<String, String>): List<String> {
        val validators = field.validators ?: return emptyList()
        val value = values[field.id].orEmpty()
        val errors = mutableListOf<String>()

        for (validator in validators) {
            when (validator.type) {
                "required" -> if (value.isBlank()) {
                    errors += validator.message?.takeIf { it.isNotBlank() } ?: "Campo obrigatório"
                }

                "email" -> if (!emailRegex.matches(value)) {
                    errors += validator.message?.takeIf { it.isNotBlank() } ?: "Email inválido"
                }

                "minLength" -> {
                    val min = (validator.value as? JsonPrimitive)?.intOrNull ?: 0
                    if (value.length < min) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "Mínimo $min caracteres"
                    }
                }

                "maxLength" -> {
                    val max = (validator.value as? JsonPrimitive)?.intOrNull ?: Int.MAX_VALUE
                    if (value.length > max) {
                        errors += validator.message?.takeIf { it.isNotBlank() }
                            ?: "Máximo $max caracteres"
                    }
                }
            }
        }
        return errors
    }
}

private val JsonPrimitive.intOrNull: Int?
    get() = this.content.toIntOrNull()
