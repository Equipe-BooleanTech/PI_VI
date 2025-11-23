package edu.fatec.petwise.features.medications.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.pets.domain.models.Pet
import kotlinx.serialization.json.JsonPrimitive

fun addMedicationFormConfiguration(prescriptions: List<Prescription>, pets: List<Pet>): FormConfiguration = FormConfiguration(
    id = "add_medication_form",
    title = "Adicionar Novo Medicamento",
    description = "Preencha as informações do medicamento para adicionar ao sistema.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "medicationName",
            label = "Nome do Medicamento",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Carprofeno, Doxiciclina",
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
            placeholder = "Ex: 50mg, 2ml, 1 comprimido",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Dosagem é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(1),
                    message = "Dosagem deve ter pelo menos 1 caractere"
                )
            )
        ),
        FormFieldDefinition(
            id = "frequency",
            label = "Frequência",
            type = FormFieldType.SELECT,
            options = listOf(
                "1x ao dia",
                "2x ao dia", 
                "3x ao dia",
                "4x ao dia",
                "A cada 8 horas",
                "A cada 12 horas",
                "Conforme necessário",
                "Outro"
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione a frequência"
                )
            )
        ),
        FormFieldDefinition(
            id = "durationDays",
            label = "Duração (dias)",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 7, 14, 30",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Duração é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(1),
                    message = "Duração deve ser pelo menos 1 dia"
                )
            )
        ),
        FormFieldDefinition(
            id = "startDate",
            label = "Data de Início",
            type = FormFieldType.DATETIME,
            placeholder = "Selecione a data",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data de início é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "endDate",
            label = "Data de Término",
            type = FormFieldType.DATETIME,
            placeholder = "Selecione a data",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data de término é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "prescriptionId",
            label = "Prescrição",
            type = FormFieldType.SELECT,
            options = prescriptions.map { prescription ->
                val pet = pets.find { it.id == prescription.petId }
                val petName = pet?.name ?: "Pet não encontrado"
                "${prescription.id} | $petName | ${prescription.medications}"
            }, 
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione uma prescrição"
                )
            )
        ),
        FormFieldDefinition(
            id = "sideEffects",
            label = "Efeitos Colaterais Observados",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva qualquer efeito colateral observado...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Adicionar Medicamento",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#007AFF",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)