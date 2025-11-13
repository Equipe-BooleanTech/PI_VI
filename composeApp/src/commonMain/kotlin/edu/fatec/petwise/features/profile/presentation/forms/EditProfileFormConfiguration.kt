package edu.fatec.petwise.features.profile.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val editProfileFormConfiguration: FormConfiguration = FormConfiguration(
    id = "edit_profile_form",
    title = "Editar Perfil",
    description = "Atualize suas informações pessoais",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "fullName",
            label = "Nome Completo",
            type = FormFieldType.TEXT,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome completo é obrigatório"
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
            id = "email",
            label = "Email",
            type = FormFieldType.EMAIL,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Email é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.EMAIL,
                    message = "Por favor, informe um email válido"
                )
            )
        ),
        FormFieldDefinition(
            id = "phone",
            label = "Telefone",
            type = FormFieldType.PHONE,
            placeholder = "(00) 00000-0000",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.PHONE,
                    message = "Formato de telefone inválido"
                )
            ),
            formatting = FieldFormatting(mask = "(##) #####-####")
        ),
        
        FormFieldDefinition(
            id = "cpf",
            label = "CPF",
            type = FormFieldType.TEXT,
            placeholder = "000.000.000-00",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.CPF,
                    message = "CPF inválido"
                )
            ),
            formatting = FieldFormatting(mask = "###.###.###-##")
        ),
        
        FormFieldDefinition(
            id = "crmv",
            label = "CRMV",
            type = FormFieldType.TEXT,
            placeholder = "Registro do Conselho Regional",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.PATTERN,
                    value = JsonPrimitive("^[0-9]{4,6}$"),
                    message = "CRMV deve conter de 4 a 6 dígitos"
                )
            ),
            formatting = FieldFormatting(mask = "####-##")
        ),
        FormFieldDefinition(
            id = "specialization",
            label = "Especialização",
            type = FormFieldType.SELECT,
            placeholder = "Selecione sua especialização",
            selectOptions = listOf(
                SelectOption("GENERAL_CLINIC", "Clínica Geral"),
                SelectOption("SURGERY", "Cirurgia"),
                SelectOption("DERMATOLOGY", "Dermatologia"),
                SelectOption("CARDIOLOGY", "Cardiologia"),
                SelectOption("ORTHOPEDICS", "Ortopedia"),
                SelectOption("OPHTHALMOLOGY", "Oftalmologia"),
                SelectOption("OTHER", "Outra")
            )
        ),
        
        FormFieldDefinition(
            id = "cnpj",
            label = "CNPJ",
            type = FormFieldType.TEXT,
            placeholder = "00.000.000/0000-00",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.CNPJ,
                    message = "CNPJ inválido"
                )
            ),
            formatting = FieldFormatting(mask = "##.###.###/####-##")
        ),
        FormFieldDefinition(
            id = "companyName",
            label = "Nome da Empresa",
            type = FormFieldType.TEXT,
            formatting = FieldFormatting(capitalize = true)
        ),

        FormFieldDefinition(
            id = "submitEditProfile",
            type = FormFieldType.SUBMIT,
            label = "Salvar Alterações"
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#00b942",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        warningColor = "#FF9500",
        backgroundColor = "#FFFFFF",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)
