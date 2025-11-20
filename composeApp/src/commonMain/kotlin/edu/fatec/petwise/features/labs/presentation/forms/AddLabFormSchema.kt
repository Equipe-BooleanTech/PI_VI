package edu.fatec.petwise.features.labs.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val addLabResultFormConfiguration: FormConfiguration = FormConfiguration(
    id = "add_lab_result_form",
    title = "Adicionar Resultado de Exame Laboratorial",
    description = "Registre um novo resultado de exame laboratorial.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "labType",
            label = "Tipo de Exame",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Hemograma, Bioquímica, Urinálise...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Tipo de exame é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Tipo deve ter pelo menos 2 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "labDate",
            label = "Data do Exame",
            type = FormFieldType.DATE,
            placeholder = "Selecione a data do exame",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data do exame é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "results",
            label = "Resultados",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva os resultados do exame...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status",
            type = FormFieldType.SELECT,
            placeholder = "Selecione o status",
            options = listOf("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Status é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Adicionar Resultado",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#009688",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)