package edu.fatec.petwise.features.prescriptions.presentation.forms

import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createEditPrescriptionFormConfiguration(prescription: Prescription): FormConfiguration = FormConfiguration(
    id = "edit_prescription_form",
    title = "Editar Prescrição - ${prescription.medicationName}",
    description = "Atualize as informações da prescrição médica.",
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
            placeholder = "Escolha um pet",
            selectOptions = emptyList(), // This will be populated by the screen
            default = JsonPrimitive(prescription.petId),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione um pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "veterinaryId",
            label = "Veterinário",
            type = FormFieldType.TEXT,
            placeholder = "ID do veterinário responsável",
            default = JsonPrimitive(prescription.veterinaryId),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Veterinário é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "medicationName",
            label = "Nome do Medicamento",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Amoxicilina, Ibuprofeno, Prednisolona...",
            default = JsonPrimitive(prescription.medicationName),
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
            default = JsonPrimitive(prescription.dosage),
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
            default = JsonPrimitive(prescription.frequency),
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
            default = JsonPrimitive(prescription.duration),
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
            default = JsonPrimitive(prescription.startDate),
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
            default = JsonPrimitive(prescription.endDate ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "instructions",
            label = "Instruções",
            type = FormFieldType.TEXTAREA,
            placeholder = "Instruções especiais para administração...",
            default = JsonPrimitive(prescription.instructions ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            default = JsonPrimitive(prescription.notes ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status",
            type = FormFieldType.SELECT,
            options = listOf("Ativa", "Concluída", "Cancelada", "Suspensa"),
            default = JsonPrimitive(prescription.status),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o status"
                )
            )
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
        primaryColor = "#4CAF50",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)