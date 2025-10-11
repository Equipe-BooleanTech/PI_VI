package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*

val loginFormConfiguration = FormConfiguration(
    id = "login_form",
    title = "PetWise",
    description = "Acesse sua conta para gerenciar seus pets.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 400,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "email",
            label = "Email",
            type = FormFieldType.EMAIL,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Informe seu email para entrar"
                ),
                ValidationRule(
                    type = ValidationType.EMAIL,
                    message = "Por favor, informe um email válido"
                )
            )
        ),
        FormFieldDefinition(
            id = "password",
            label = "Senha",
            type = FormFieldType.PASSWORD,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Informe sua senha para entrar"
                )
            )
        ),
        FormFieldDefinition(
            id = "submitLogin",
            type = FormFieldType.SUBMIT,
            label = "Entrar"
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