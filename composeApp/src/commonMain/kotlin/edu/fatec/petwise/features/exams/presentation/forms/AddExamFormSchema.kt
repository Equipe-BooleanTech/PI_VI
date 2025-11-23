package edu.fatec.petwise.features.exams.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val addExamFormConfiguration: FormConfiguration = FormConfiguration(
    id = "add_exam_form",
    title = "Registrar Exame",
    description = "Preencha as informações do exame para registrar no sistema.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "petId",
            label = "Pet",
            type = FormFieldType.SELECT,
            placeholder = "Selecione o pet",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "examType",
            label = "Tipo de Exame",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Hemograma, Raio-X, Ultrassom",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Tipo de exame é obrigatório"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "examDate",
            label = "Data do Exame",
            type = FormFieldType.DATETIME,
            placeholder = "DD/MM/AAAA",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data do exame é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status do Exame",
            type = FormFieldType.TEXT,
            placeholder = "Status do exame",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Status do exame é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "results",
            label = "Resultados (opcional)",
            type = FormFieldType.TEXTAREA,
            placeholder = "Resultados do exame...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações (opcional)",
            type = FormFieldType.TEXTAREA,
            placeholder = "Informações adicionais sobre o exame...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Registrar Exame",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#F44336",
        successColor = "#4CAF50",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)