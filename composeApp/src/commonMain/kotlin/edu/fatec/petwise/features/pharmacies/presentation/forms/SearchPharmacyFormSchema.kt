package edu.fatec.petwise.features.pharmacies.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createSearchPharmacyFormConfiguration(): FormConfiguration = FormConfiguration(
    id = "search_pharmacy_form",
    title = "Buscar Farmácia",
    description = "Encontre farmácias registradas na plataforma.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 400,
        spacing = 12,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "query",
            label = "Buscar farmácia",
            type = FormFieldType.TEXT,
            placeholder = "Nome, email ou telefone...",
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
            id = "verified",
            label = "Apenas Verificadas",
            type = FormFieldType.CHECKBOX,
            default = JsonPrimitive(false)
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Aplicar Filtros",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_CHANGE,
    submitBehavior = SubmitBehavior.DEFAULT,
    styling = FormStyling(
        primaryColor = "#4CAF50",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 48,
        borderRadius = 8,
        spacing = 12
    )
)

object SearchPharmacyFormSchema {
    val configuration = createSearchPharmacyFormConfiguration()
    
    fun getInitialValues(): Map<String, Any> = mapOf(
        "query" to "",
        "verified" to false
    )
    
    fun validateSearchQuery(query: String): Boolean {
        return query.isEmpty() || query.length >= 2
    }
}
