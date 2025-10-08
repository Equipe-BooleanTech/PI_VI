package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val registerFormConfiguration: FormConfiguration = FormConfiguration(
    id = "register_form",
    title = "PetWise",
    description = "Crie sua conta para começar a cuidar do seu pet.",
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
            id = "userType",
            label = "Tipo de Usuário",
            type = FormFieldType.SELECT,
            options = listOf("Cliente", "Veterinário", "Farmácia", "Admin"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o tipo de usuário"
                )
            )
        ),

        FormFieldDefinition(
            id = "cpf",
            label = "CPF",
            type = FormFieldType.TEXT,
            placeholder = "000.000.000-00",
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Cliente")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "CPF é obrigatório"
                ),
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
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Veterinário")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "CRMV é obrigatório"
                ),
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
            options = listOf("Clínica Geral", "Cirurgia", "Dermatologia", "Cardiologia", "Ortopedia", "Neurologia", "Oftalmologia", "Outra"),
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Veterinário")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Especialização é obrigatória"
                )
            )
        ),

        FormFieldDefinition(
            id = "cnpj",
            label = "CNPJ",
            type = FormFieldType.TEXT,
            placeholder = "00.000.000/0000-00",
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Farmácia")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "CNPJ é obrigatório"
                ),
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
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Farmácia")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome da empresa é obrigatório"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),

        FormFieldDefinition(
            id = "adminCode",
            label = "Código de Administrador",
            type = FormFieldType.TEXT,
            visibility = VisibilityRule(
                conditions = listOf(
                    VisibilityCondition(
                        fieldId = "userType",
                        operator = ComparisonOperator.EQUALS,
                        value = JsonPrimitive("Admin")
                    )
                )
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Código de administrador é obrigatório"
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
            id = "password",
            label = "Senha",
            type = FormFieldType.PASSWORD,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Senha é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(8),
                    message = "A senha deve ter pelo menos 8 caracteres"
                ),
                ValidationRule(
                    type = ValidationType.PASSWORD_STRENGTH,
                    message = "A senha deve conter letras e números"
                )
            )
        ),
        FormFieldDefinition(
            id = "confirmPassword",
            label = "Confirmar Senha",
            type = FormFieldType.PASSWORD,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Confirmação de senha é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.MATCHES_FIELD,
                    field = "password",
                    message = "As senhas não conferem"
                )
            )
        ),

        FormFieldDefinition(
            id = "submitRegister",
            type = FormFieldType.SUBMIT,
            label = "Criar Conta"
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#007AFF",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        warningColor = "#FF9500",
        backgroundColor = "#FFFFFF",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)