package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator

val loginSchema = FormSchema(
    id = "login_form",
    title = "PetWise",
    description = "Acesse sua conta para gerenciar seus pets.",
    fields = listOf(
        Field(
            id = "email",
            label = "Email",
            type = "email",
            validators = listOf(
                Validator(type = "required", message = "Informe seu email para entrar"),
                Validator(type = "email")
            )
        ),
        Field(
            id = "password",
            label = "Senha",
            type = "password",
            validators = listOf(
                Validator(type = "required", message = "Informe sua senha para entrar")
            )
        ),
        Field(
            id = "submitLogin",
            type = "submit",
            label = "Entrar"
        )
    )
)