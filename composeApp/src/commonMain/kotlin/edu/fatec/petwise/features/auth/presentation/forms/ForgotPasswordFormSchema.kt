package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator

val forgotPasswordSchema = FormSchema(
    id = "forgot_password_form",
    title = "Recuperar Senha",
    description = "Digite seu email para receber instruções de redefinição de senha.",
    fields = listOf(
        Field(
            id = "email",
            label = "Email",
            type = "email",
            placeholder = "seu@email.com",
            validators = listOf(
                Validator(
                    type = "required",
                    message = "Informe seu email cadastrado"
                ),
                Validator(
                    type = "email",
                    message = "Por favor, informe um email válido"
                )
            )
        ),
        Field(
            id = "submitForgotPassword",
            type = "submit",
            label = "Enviar Link de Recuperação"
        )
    )
)
