package edu.fatec.petwise.features.vaccinations.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import kotlinx.serialization.json.JsonPrimitive

val vaccinationStatusOptions = VaccinationStatus.values().map { SelectOption(key = it.name, value = it.getDisplayName()) }

fun createEditVaccinationFormConfiguration(vaccination: Vaccination): FormConfiguration = FormConfiguration(
    id = "edit_vaccination_form",
    title = "Editar Vacina - ${vaccination.vaccineType.getDisplayName()}",
    description = "Atualize as informações da vacinação.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "vaccinationDate",
            label = "Data de Aplicação",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/AAAA",
            default = JsonPrimitive(vaccination.vaccinationDate),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data de aplicação é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Digite uma data válida (DD/MM/AAAA)"
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
            ),
            default = JsonPrimitive(vaccination.vaccineType.name)
        ),
        FormFieldDefinition(
            id = "nextDoseDate",
            label = "Data do Próximo Reforço (Opcional)",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/AAAA",
            default = vaccination.nextDoseDate?.let { JsonPrimitive(it) },
            validators = listOf(
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Digite uma data válida (DD/MM/AAAA)"
                )
            )
        ),
        FormFieldDefinition(
            id = "totalDoses",
            label = "Número Total de Doses",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 3",
            default = JsonPrimitive(vaccination.totalDoses),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Número de doses é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
                ),
                ValidationRule(
                    type = ValidationType.CUSTOM,
                    value = JsonPrimitive(1),
                    message = "Número de doses deve ser pelo menos 1"
                )
            )
        ),
        FormFieldDefinition(
            id = "manufacturer",
            label = "Fabricante (Opcional)",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Zoetis, MSD, etc.",
            default = vaccination.manufacturer?.let { JsonPrimitive(it) },
            validators = emptyList(),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "status",
            label = "Status da Vacinação",
            type = FormFieldType.SELECT,
            selectOptions = vaccinationStatusOptions,
            default = JsonPrimitive(vaccination.status.name),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Status é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "observations",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Adicione observações sobre a vacinação...",
            default = JsonPrimitive(vaccination.observations),
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
