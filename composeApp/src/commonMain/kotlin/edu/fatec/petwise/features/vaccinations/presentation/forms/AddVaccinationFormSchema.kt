package edu.fatec.petwise.features.vaccinations.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

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
            id = "vaccineName",
            label = "Nome da Vacina",
            type = FormFieldType.TEXT,
            placeholder = "Nome comercial da vacina",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome da vacina é obrigatório"
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
            id = "vaccineType",
            label = "Tipo de Vacina",
            type = FormFieldType.SELECT,
            options = listOf(
                "Vacina V8",
                "Vacina V10",
                "Antirrábica",
                "Gripe Canina",
                "Giárdia",
                "Leptospirose",
                "Tríplice Felina",
                "Quádrupla Felina",
                "Leucemia Felina",
                "Raiva Felina",
                "Outras"
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o tipo de vacina"
                )
            )
        ),
        FormFieldDefinition(
            id = "applicationDate",
            label = "Data de Aplicação",
            type = FormFieldType.DATE,
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
            type = FormFieldType.DATE,
            placeholder = "DD/MM/AAAA",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "doseNumber",
            label = "Número da Dose",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 1",
            default = JsonPrimitive("1"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Número da dose é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
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
            id = "veterinarianName",
            label = "Nome do Veterinário",
            type = FormFieldType.TEXT,
            placeholder = "Nome completo",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome do veterinário é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(3),
                    message = "Nome deve ter pelo menos 3 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "veterinarianCrmv",
            label = "CRMV",
            type = FormFieldType.TEXT,
            placeholder = "CRMV-UF 00000",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "CRMV é obrigatório"
                )
            ),
            formatting = FieldFormatting(uppercase = true)
        ),
        FormFieldDefinition(
            id = "clinicName",
            label = "Clínica/Hospital",
            type = FormFieldType.TEXT,
            placeholder = "Nome da clínica veterinária",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome da clínica é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(3),
                    message = "Nome deve ter pelo menos 3 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "batchNumber",
            label = "Número do Lote",
            type = FormFieldType.TEXT,
            placeholder = "Lote da vacina",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Número do lote é obrigatório"
                )
            ),
            formatting = FieldFormatting(uppercase = true)
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
