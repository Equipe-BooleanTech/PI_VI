package edu.fatec.petwise.features.veterinaries.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createSearchVeterinaryFormConfiguration(): FormConfiguration = FormConfiguration(
    id = "search_veterinary_form",
    title = "Buscar Veterinário",
    description = "Encontre veterinários registrados na plataforma.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 400,
        spacing = 12,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "query",
            label = "Buscar veterinário",
            type = FormFieldType.TEXT,
            placeholder = "Nome, email ou especialização...",
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Digite pelo menos 2 caracteres"
                )
            )
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Buscar",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_CHANGE,
    submitBehavior = SubmitBehavior.DEFAULT,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 48,
        borderRadius = 8,
        spacing = 12
    )
)

object SearchVeterinaryFormSchema {
    val configuration = createSearchVeterinaryFormConfiguration()
    
    fun getInitialValues(): Map<String, Any> = mapOf(
        "searchQuery" to "",
        "verified" to false
    )
    
    fun validateSearchQuery(query: String): Boolean {
        return query.length >= 2
    }
}