package edu.fatec.petwise.features.pets.presentation.forms

import edu.fatec.petwise.features.auth.shared.Field
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator
import kotlinx.serialization.json.JsonPrimitive

val addPetFormSchema: FormSchema = FormSchema(
    id = "add_pet_form",
    title = "Adicionar Novo Pet",
    description = "Preencha as informações do pet para adicionar ao sistema.",
    fields = listOf(
        Field(
            id = "name",
            label = "Nome",
            type = "text",
            placeholder = "Nome do pet",
            validators = listOf(
                Validator(type = "required", message = "Nome é obrigatório"),
                Validator(type = "minLength", value = JsonPrimitive(2), message = "Nome deve ter pelo menos 2 caracteres")
            )
        ),
        Field(
            id = "breed",
            label = "Raça",
            type = "text",
            placeholder = "Raça do pet",
            validators = listOf(
                Validator(type = "required", message = "Raça é obrigatória"),
                Validator(type = "minLength", value = JsonPrimitive(2), message = "Raça deve ter pelo menos 2 caracteres")
            )
        ),
        Field(
            id = "species",
            label = "Espécie",
            type = "select",
            options = listOf("Cão", "Gato", "Ave", "Coelho", "Outro"),
            validators = listOf(
                Validator(type = "required", message = "Selecione a espécie")
            )
        ),
        Field(
            id = "gender",
            label = "Sexo",
            type = "segmented",
            options = listOf("Macho", "Fêmea"),
            validators = listOf(
                Validator(type = "required", message = "Selecione o sexo")
            )
        ),
        Field(
            id = "age",
            label = "Idade (anos)",
            type = "text",
            placeholder = "Ex: 2",
            validators = listOf(
                Validator(type = "required", message = "Idade é obrigatória"),
                Validator(type = "pattern", value = JsonPrimitive("^[0-9]+$"), message = "Digite apenas números")
            )
        ),
        Field(
            id = "weight",
            label = "Peso (kg)",
            type = "text",
            placeholder = "Ex: 15",
            validators = listOf(
                Validator(type = "required", message = "Peso é obrigatório"),
                Validator(type = "pattern", value = JsonPrimitive("^[0-9]+(\\.[0-9]+)?$"), message = "Digite um peso válido (ex: 15 ou 15.5)")
            )
        ),
        Field(
            id = "healthStatus",
            label = "Status de Saúde",
            type = "select",
            options = listOf("Excelente", "Bom", "Regular", "Atenção", "Crítico"),
            default = JsonPrimitive("Bom"),
            validators = listOf(
                Validator(type = "required", message = "Selecione o status de saúde")
            )
        ),
        Field(
            id = "ownerName",
            label = "Nome do Tutor",
            type = "text",
            placeholder = "Nome completo",
            validators = listOf(
                Validator(type = "required", message = "Nome do tutor é obrigatório"),
                Validator(type = "minLength", value = JsonPrimitive(3), message = "Nome deve ter pelo menos 3 caracteres")
            )
        ),
        Field(
            id = "ownerPhone",
            label = "Telefone",
            type = "text",
            placeholder = "(11) 99999-9999",
            mask = "phone",
            validators = listOf(
                Validator(type = "required", message = "Telefone é obrigatório"),
                Validator(type = "phone", message = "Formato de telefone inválido")
            )
        ),
        Field(
            id = "healthHistory",
            label = "Histórico de Saúde",
            type = "text",
            placeholder = "Descreva o histórico de saúde do pet...",
            validators = listOf()
        ),
        Field(
            id = "submit",
            label = "Adicionar Pet",
            type = "submit"
        )
    )
)