package edu.fatec.petwise.features.consultas.presentation.forms

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createAddConsultaFormConfiguration(petOptions: List<SelectOption> = emptyList()): FormConfiguration = FormConfiguration(
    id = "add_consulta_form",
    title = "Agendar Nova Consulta",
    description = "Preencha as informações da consulta para agendar.",
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
            id = "veterinarianName",
            label = "Veterinário(a)",
            type = FormFieldType.TEXT,
            placeholder = "Nome do veterinário",
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
            id = "consultaType",
            label = "Tipo de Consulta",
            type = FormFieldType.SELECT,
            options = listOf(
                "Consulta de Rotina",
                "Emergência",
                "Retorno",
                "Vacinação",
                "Cirurgia",
                "Exame",
                "Odontologia",
                "Estética",
                "Outro"
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o tipo de consulta"
                )
            )
        ),
        FormFieldDefinition(
            id = "consultaDate",
            label = "Data da Consulta",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "consultaTime",
            label = "Horário",
            type = FormFieldType.TIME,
            placeholder = "HH:MM",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Horário é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "symptoms",
            label = "Sintomas / Motivo",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva os sintomas ou motivo da consulta...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Descrição dos sintomas é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "ownerName",
            label = "Nome do Tutor",
            type = FormFieldType.TEXT,
            placeholder = "Nome completo",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome do tutor é obrigatório"
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
            id = "ownerPhone",
            label = "Telefone do Tutor",
            type = FormFieldType.PHONE,
            placeholder = "(11) 99999-9999",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Telefone é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.PHONE,
                    message = "Formato de telefone inválido"
                )
            ),
            formatting = FieldFormatting(mask = "(##) #####-####")
        ),
        FormFieldDefinition(
            id = "price",
            label = "Valor da Consulta (R$)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 150.00",
            default = JsonPrimitive("0.00"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Valor é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um valor válido (ex: 150 ou 150.50)"
                )
            )
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Agendar Consulta",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)

fun createEditConsultaFormConfiguration(consulta: Consulta, petOptions: List<SelectOption> = emptyList()): FormConfiguration = FormConfiguration(
    id = "edit_consulta_form",
    title = "Editar Consulta",
    description = "Atualize as informações da consulta.",
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
            selectOptions = petOptions,
            default = JsonPrimitive(consulta.petId),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione um pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "veterinarianName",
            label = "Veterinário(a)",
            type = FormFieldType.TEXT,
            placeholder = "Nome do veterinário",
            default = JsonPrimitive(consulta.veterinarianName),
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
            id = "consultaType",
            label = "Tipo de Consulta",
            type = FormFieldType.SELECT,
            options = listOf(
                "Consulta de Rotina",
                "Emergência",
                "Retorno",
                "Vacinação",
                "Cirurgia",
                "Exame",
                "Odontologia",
                "Estética",
                "Outro"
            ),
            default = JsonPrimitive(consulta.consultaType.displayName),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o tipo de consulta"
                )
            )
        ),
        FormFieldDefinition(
            id = "consultaDate",
            label = "Data da Consulta",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            default = JsonPrimitive(consulta.consultaDate),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "consultaTime",
            label = "Horário",
            type = FormFieldType.TIME,
            placeholder = "HH:MM",
            default = JsonPrimitive(consulta.consultaTime),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Horário é obrigatório"
                )
            )
        ),
        FormFieldDefinition(
            id = "symptoms",
            label = "Sintomas / Motivo",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva os sintomas ou motivo da consulta...",
            default = JsonPrimitive(consulta.symptoms),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Descrição dos sintomas é obrigatória"
                )
            )
        ),
        FormFieldDefinition(
            id = "diagnosis",
            label = "Diagnóstico",
            type = FormFieldType.TEXTAREA,
            placeholder = "Diagnóstico médico...",
            default = JsonPrimitive(consulta.diagnosis),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "treatment",
            label = "Tratamento",
            type = FormFieldType.TEXTAREA,
            placeholder = "Tratamento prescrito...",
            default = JsonPrimitive(consulta.treatment),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "prescriptions",
            label = "Prescrições",
            type = FormFieldType.TEXTAREA,
            placeholder = "Medicamentos e dosagens...",
            default = JsonPrimitive(consulta.prescriptions),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "price",
            label = "Valor da Consulta (R$)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 150.00",
            default = JsonPrimitive(consulta.price.toString()),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Valor é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um valor válido (ex: 150 ou 150.50)"
                )
            )
        ),
        FormFieldDefinition(
            id = "nextAppointment",
            label = "Próxima Consulta",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            default = consulta.nextAppointment?.let { JsonPrimitive(it) },
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "notes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            placeholder = "Observações adicionais...",
            default = JsonPrimitive(consulta.notes),
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
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)
