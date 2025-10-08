package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator
import kotlinx.serialization.json.JsonPrimitive

val resetPasswordSchema = FormSchema(
    id = "reset_password_form",
    title = "Criar Nova Senha",
    description = "Digite sua nova senha para acessar sua conta.",
    fields = listOf(
        Field(
            id = "newPassword",
            label = "Nova Senha",
            type = "password",
            placeholder = "Digite sua nova senha",
            validators = listOf(
                Validator(
                    type = "required",
                    message = "Informe sua nova senha"
                ),
                Validator(
                    type = "minLength", 
                    value = JsonPrimitive(8),
                    message = "A senha deve ter pelo menos 8 caracteres"
                ),
                Validator(
                    type = "password",
                    message = "A senha deve conter letras e números"
                )
            )
        ),
        Field(
            id = "confirmNewPassword",
            label = "Confirmar Nova Senha",
            type = "password",
            placeholder = "Confirme sua nova senha",
            validators = listOf(
                Validator(
                    type = "required",
                    message = "Confirme sua nova senha"
                ),
                Validator(
                    type = "matchesField", 
                    field = "newPassword",
                    message = "As senhas não conferem"
                )
            )
        ),
        Field(
            id = "submitResetPassword",
            type = "submit",
            label = "Redefinir Senha"
        )
    )
)
