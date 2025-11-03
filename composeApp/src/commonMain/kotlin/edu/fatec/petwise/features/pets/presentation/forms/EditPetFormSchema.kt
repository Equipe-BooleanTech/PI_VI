package edu.fatec.petwise.features.pets.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.pets.domain.models.Pet
import kotlinx.serialization.json.JsonPrimitive

fun createEditPetFormConfiguration(pet: Pet): FormConfiguration = FormConfiguration(
    id = "edit_pet_form",
    title = "Editar Pet - ${pet.name}",
    description = "Atualize as informações do seu pet.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "name",
            label = "Nome",
            type = FormFieldType.TEXT,
            placeholder = "Nome do pet",
            default = JsonPrimitive(pet.name),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Nome deve ter pelo menos 2 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "breed",
            label = "Raça",
            type = FormFieldType.TEXT,
            placeholder = "Raça do pet",
            default = JsonPrimitive(pet.breed),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Raça é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Raça deve ter pelo menos 2 caracteres"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "age",
            label = "Idade (anos)",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 2",
            default = JsonPrimitive(pet.age),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Idade é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
                )
            )
        ),
        FormFieldDefinition(
            id = "weight",
            label = "Peso (kg)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 15.5",
            default = JsonPrimitive(pet.weight),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Peso é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um peso válido (ex: 15 ou 15.5)"
                )
            )
        ),
        FormFieldDefinition(
            id = "healthHistory",
            label = "Histórico de Saúde",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva o histórico de saúde do pet...",
            default = JsonPrimitive(pet.healthHistory),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Salvar Alterações",
            type = FormFieldType.SUBMIT
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