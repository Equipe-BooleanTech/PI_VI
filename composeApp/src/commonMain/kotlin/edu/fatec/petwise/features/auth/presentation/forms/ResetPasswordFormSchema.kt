package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val resetPasswordFormConfiguration = FormConfiguration(
    id = "reset_password_form",
    title = "Criar Nova Senha",
    description = "Digite sua nova senha para acessar sua conta.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 400,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "newPassword",
            label = "Nova Senha",
            type = FormFieldType.PASSWORD,
            placeholder = "Digite sua nova senha",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Informe sua nova senha"
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
            id = "confirmNewPassword",
            label = "Confirmar Nova Senha",
            type = FormFieldType.PASSWORD,
            placeholder = "Confirme sua nova senha",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Confirme sua nova senha"
                ),
                ValidationRule(
                    type = ValidationType.MATCHES_FIELD,
                    field = "newPassword",
                    message = "As senhas não conferem"
                )
            )
        ),
        FormFieldDefinition(
            id = "submitResetPassword",
            type = FormFieldType.SUBMIT,
            label = "Redefinir Senha"
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
