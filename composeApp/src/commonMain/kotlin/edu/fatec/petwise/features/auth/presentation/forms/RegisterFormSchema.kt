package edu.fatec.petwise.features.auth.presentation.forms

import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val registerSchema: FormSchema = FormSchema(
  id = "register_form",
  title = "PetWise",
  description = "Crie sua conta para começar a cuidar do seu pet.",
  fields = listOf(
    Field(
      id = "fullName",
      label = "Nome Completo",
      type = "text",
      validators = listOf(
        Validator(type = "required"),
        Validator(type = "minLength", value = JsonPrimitive(3))
      )
    ),
    Field(
      id = "email",
      label = "Email",
      type = "email",
      validators = listOf(
        Validator(type = "required"),
        Validator(type = "email", message = "Por favor, informe um email válido")
      )
    ),
    Field(
      id = "userType",
      label = "Tipo de Usuário",
      type = "select",
      options = listOf("Cliente", "Veterinário", "Farmácia", "Admin"),
      validators = listOf(
        Validator(type = "required", message = "Selecione o tipo de usuário")
      )
    ),

    Field(
      id = "cpf",
      label = "CPF",
      type = "text",
      placeholder = "000.000.000-00",
      visibleIf = mapOf("userType" to JsonPrimitive("Cliente")),
      validators = listOf(
        Validator(type = "required", message = "CPF é obrigatório"),
        Validator(type = "cpf", message = "CPF inválido")
      ),
      mask = "cpf"
    ),

    Field(
      id = "crmv",
      label = "CRMV",
      type = "text",
      placeholder = "Registro do Conselho Regional",
      visibleIf = mapOf("userType" to JsonPrimitive("Veterinário")),
      validators = listOf(
        Validator(type = "required", message = "CRMV é obrigatório"),
        Validator(type = "pattern", value = JsonPrimitive("^[0-9]{4,6}$"), message = "CRMV deve conter de 4 a 6 dígitos")
      ),
      mask = "crmv"
    ),
    Field(
      id = "specialization",
      label = "Especialização",
      type = "select",
      placeholder = "Selecione sua especialização",
      options = listOf("Clínica Geral", "Cirurgia", "Dermatologia", "Cardiologia", "Ortopedia", "Neurologia", "Oftalmologia", "Outra"),
      visibleIf = mapOf("userType" to JsonPrimitive("Veterinário")),
      validators = listOf(
        Validator(type = "required", message = "Especialização é obrigatória")
      )
    ),

    Field(
      id = "cnpj",
      label = "CNPJ",
      type = "text",
      placeholder = "00.000.000/0000-00",
      visibleIf = mapOf("userType" to JsonPrimitive("Farmácia")),
      validators = listOf(
        Validator(type = "required", message = "CNPJ é obrigatório"),
        Validator(type = "cnpj", message = "CNPJ inválido")
      ),
      mask = "cnpj"
    ),
    Field(
      id = "companyName",
      label = "Nome da Empresa",
      type = "text",
      visibleIf = mapOf("userType" to JsonPrimitive("Farmácia")),
      validators = listOf(
        Validator(type = "required", message = "Nome da empresa é obrigatório")
      )
    ),

    Field(
      id = "adminCode",
      label = "Código de Administrador",
      type = "text",
      visibleIf = mapOf("userType" to JsonPrimitive("Admin")),
      validators = listOf(
        Validator(type = "required", message = "Código de administrador é obrigatório")
      )
    ),

    Field(
      id = "phone",
      label = "Telefone",
      type = "text",
      placeholder = "(00) 00000-0000",
      validators = listOf(
        Validator(type = "required", message = "Telefone é obrigatório"),
        Validator(type = "phone", message = "Formato de telefone inválido")
      ),
      mask = "phone"
    ),

    Field(
      id = "password",
      label = "Senha",
      type = "password",
      validators = listOf(
        Validator(type = "required"),
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
      id = "confirmPassword",
      label = "Confirmar Senha",
      type = "password",
      validators = listOf(
        Validator(type = "required"),
        Validator(
          type = "matchesField",
          field = "password",
          message = "As senhas não conferem"
        )
      )
    ),

    Field(
      id = "submitRegister",
      type = "submit",
      label = "Criar Conta"
    )
  )
)