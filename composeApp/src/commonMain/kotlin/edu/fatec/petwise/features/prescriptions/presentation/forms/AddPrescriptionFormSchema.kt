package edu.fatec.petwise.features.prescriptions.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val addPrescriptionFormConfiguration: FormConfiguration = FormConfiguration(
    id = "add_prescription_form",
    title = "Adicionar Prescrição",
    description = "Registre uma nova prescrição médica para o pet.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "petId",
            label = "Selecione o Pet",
            type = FormFieldType.SELECT,
            placeholder = "Escolha um pet",
            selectOptions = emptyList(), // This will be populated by the screen
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione um pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "medicationName",
            label = "Nome do Medicamento",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Amoxicilina, Ibuprofeno, Prednisolona...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome do medicamento é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Nome deve ter pelo menos 2 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "dosage",
            label = "Dosagem",
            type = FormFieldType.TEXT,
            placeholder = "Ex: 10mg, 5ml, 1 comprimido...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Dosagem é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "frequency",
            label = "Frequência",
            type = FormFieldType.TEXT,
            placeholder = "Ex: 2x ao dia, a cada 8 horas, 1x por semana...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Frequência é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "duration",
            label = "Duração",
            type = FormFieldType.TEXT,
            placeholder = "Ex: 7 dias, 2 semanas, 1 mês...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Duração é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "startDate",
            label = "Data de Início",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data de início é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "endDate",
            label = "Data de Fim (Opcional)",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "instructions",
            label = "Instruções",
            type = FormFieldType.TEXTAREA,
            placeholder = "Instruções especiais para administração...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status",
            type = FormFieldType.SELECT,
            options = listOf("Ativa", "Concluída", "Cancelada", "Suspensa"),
            default = JsonPrimitive("Ativa"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o status"
                )
            )
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Adicionar Prescrição",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#4CAF50",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)