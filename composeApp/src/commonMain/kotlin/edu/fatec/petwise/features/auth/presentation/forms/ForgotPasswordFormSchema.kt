package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*

val forgotPasswordFormConfiguration = FormConfiguration(
    id = "forgot_password_form",
    title = "Recuperar Senha",
    description = "Digite seu email para receber instruções de redefinição de senha.",
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
            placeholder = "seu@email.com",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Informe seu email cadastrado"
                ),
                ValidationRule(
                    type = ValidationType.EMAIL,
                    message = "Por favor, informe um email válido"
                )
            )
        ),
        FormFieldDefinition(
            id = "submitForgotPassword",
            type = FormFieldType.SUBMIT,
            label = "Enviar Link de Recuperação"
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
