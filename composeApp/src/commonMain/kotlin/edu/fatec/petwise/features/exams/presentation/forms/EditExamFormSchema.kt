package edu.fatec.petwise.features.exams.presentation.forms

import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createEditExamFormConfiguration(exam: Exam): FormConfiguration = FormConfiguration(
    id = "edit_exam_form",
    title = "Editar Exame - ${exam.examType}",
    description = "Atualize as informações do exame.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "examType",
            label = "Tipo de Exame",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Hemograma, Bioquímica, Urinálise...",
            default = JsonPrimitive(exam.examType),
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
            id = "examDate",
            label = "Data do Exame",
            type = FormFieldType.DATE,
            placeholder = "Selecione a data do exame",
            default = JsonPrimitive(exam.examDate),
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
            default = JsonPrimitive(exam.results ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status",
            type = FormFieldType.SELECT,
            placeholder = "Selecione o status",
            default = JsonPrimitive(exam.status),
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
            default = JsonPrimitive(exam.notes ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Salvar Alterações",
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