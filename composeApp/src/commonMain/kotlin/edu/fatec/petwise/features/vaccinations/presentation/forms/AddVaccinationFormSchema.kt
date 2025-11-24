package edu.fatec.petwise.features.vaccinations.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import kotlinx.serialization.json.JsonPrimitive

val vaccineTypeOptions = VaccineType.values().map { SelectOption(key = it.name, value = it.getDisplayName()) }

val addVaccinationFormConfiguration: FormConfiguration = FormConfiguration(
    id = "add_vaccination_form",
    title = "Registrar Vacinação",
    description = "Preencha as informações da vacinação para registrar no sistema.",
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
            id = "vaccineType",
            label = "Tipo de Vacina",
            type = FormFieldType.SELECT,
            selectOptions = vaccineTypeOptions,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o tipo de vacina"
                )
            )
        ),
        FormFieldDefinition(
            id = "vaccinationDate",
            label = "Data de Vacinação",
            type = FormFieldType.DATETIME,
            placeholder = "DD/MM/AAAA",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data de aplicação é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "nextDoseDate",
            label = "Próxima Dose (opcional)",
            type = FormFieldType.DATETIME,
            placeholder = "DD/MM/AAAA",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "totalDoses",
            label = "Total de Doses",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 3",
            default = JsonPrimitive("1"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Total de doses é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
                )
            )
        ),
        FormFieldDefinition(
            id = "manufacturer",
            label = "Fabricante",
            type = FormFieldType.TEXT,
            placeholder = "Nome do laboratório fabricante",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Fabricante é obrigatório"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "observations",
            label = "Observações (opcional)",
            type = FormFieldType.TEXTAREA,
            placeholder = "Informações adicionais sobre a vacinação...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Registrar Vacinação",
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
