package edu.fatec.petwise.features.prescriptions.presentation.forms

import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createEditPrescriptionFormConfiguration(prescription: Prescription): FormConfiguration = FormConfiguration(
    id = "edit_prescription_form",
    title = "Editar Prescrição",
    description = "Atualize as informações da prescrição médica.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "instructions",
            label = "Instruções",
            type = FormFieldType.TEXTAREA,
            placeholder = "Instruções para administração do medicamento...",
            default = JsonPrimitive(prescription.instructions),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Instruções são obrigatórias"
                )
            )
        ),
        FormFieldDefinition(
            id = "medications",
            label = "Medicamentos",
            type = FormFieldType.TEXTAREA,
            placeholder = "Liste os medicamentos prescritos...",
            default = JsonPrimitive(prescription.medications),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Medicamentos são obrigatórios"
                )
            )
        ),
        FormFieldDefinition(
            id = "diagnosis",
            label = "Diagnóstico",
            type = FormFieldType.TEXTAREA,
            placeholder = "Diagnóstico do pet...",
            default = JsonPrimitive(prescription.diagnosis ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "validUntil",
            label = "Válido Até (Opcional)",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            default = JsonPrimitive(prescription.validUntil ?: ""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "observations",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            default = JsonPrimitive(prescription.observations),
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
        primaryColor = "#4CAF50",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)