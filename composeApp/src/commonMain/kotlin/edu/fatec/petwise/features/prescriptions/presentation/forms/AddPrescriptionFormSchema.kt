package edu.fatec.petwise.features.prescriptions.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun addPrescriptionFormConfiguration(petOptions: List<SelectOption> = emptyList()): FormConfiguration = FormConfiguration(
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
            selectOptions = petOptions,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione um pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "prescriptionDate",
            label = "Data da Prescrição",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data da prescrição é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "instructions",
            label = "Instruções",
            type = FormFieldType.TEXTAREA,
            placeholder = "Instruções para administração do medicamento...",
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
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "validUntil",
            label = "Válido Até (Opcional)",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "observations",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            validators = emptyList()
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