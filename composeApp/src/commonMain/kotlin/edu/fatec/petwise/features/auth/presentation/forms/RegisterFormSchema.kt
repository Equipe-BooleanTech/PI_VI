import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator

val registerSchema = FormSchema(
  id = "register_form",
  title = "PetWise",
  description = "Crie sua conta para começar a cuidar do seu pet.",
  fields = listOf(
    Field(
      id = "fullName",
      label = "Nome Completo",
      type = "text",
      validators = listOf(Validator(type = "required"))
    ),
    Field(
      id = "email",
      label = "Email",
      type = "email",
      validators = listOf(Validator(type = "required"))
    ),
    Field(
      id = "userType",
      label = "Tipo de Usuário",
      type = "select",
      options = listOf("Cliente", "Veterinário", "Farmácia", "Admin")
    ),
    Field(
      id = "password",
      label = "Senha",
      type = "password",
      validators = listOf(Validator(type = "required"))
    ),
    Field(
      id = "confirmPassword",
      label = "Confirmar Senha",
      type = "password",
      validators = listOf(
        Validator(type = "required"),
        Validator(type = "matchesField", field = "password")
      )
    ),
    Field(
      id = "submitRegister",
      type = "submit",
      label = "Criar Conta"
    )
  )
)
