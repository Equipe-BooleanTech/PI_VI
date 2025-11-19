package edu.fatec.petwise.features.pets.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
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
            default = JsonPrimitive(pet.name.toString()),
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
            default = JsonPrimitive(pet.breed.toString()),
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
            id = "species",
            label = "Espécie",
            type = FormFieldType.SELECT,
            options = listOf("Cão", "Gato", "Ave", "Coelho", "Outro"),
            default = JsonPrimitive(
                when (pet.species) {
                    edu.fatec.petwise.features.pets.domain.models.PetSpecies.DOG -> "Cão"
                    edu.fatec.petwise.features.pets.domain.models.PetSpecies.CAT -> "Gato"
                    edu.fatec.petwise.features.pets.domain.models.PetSpecies.BIRD -> "Ave"
                    edu.fatec.petwise.features.pets.domain.models.PetSpecies.RABBIT -> "Coelho"
                    edu.fatec.petwise.features.pets.domain.models.PetSpecies.OTHER -> "Outro"
                    else -> "Outro"
                }
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione a espécie"
                )
            )
        ),
        FormFieldDefinition(
            id = "gender",
            label = "Sexo",
            type = FormFieldType.SEGMENTED_CONTROL,
            default = JsonPrimitive(
                when (pet.gender.toString()) {
                    "MALE" -> "Macho"
                    "FEMALE" -> "Fêmea"
                    else -> "Macho"
                }
            ),
            options = listOf("Macho", "Fêmea"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o sexo"
                )
            )
        ),
        FormFieldDefinition(
            id = "age",
            label = "Idade (anos)",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 2",
            default = JsonPrimitive(pet.age.toString()),
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
            default = JsonPrimitive(pet.weight.toString()),
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
            id = "healthStatus",
            label = "Estado de Saúde",
            type = FormFieldType.SELECT,
            options = listOf("Excelente", "Bom", "Regular", "Atenção", "Crítico"),
            default = JsonPrimitive(
                when (pet.healthStatus) {
                    HealthStatus.EXCELLENT -> "Excelente"
                    HealthStatus.GOOD -> "Bom"
                    HealthStatus.REGULAR -> "Regular"
                    HealthStatus.ATTENTION -> "Atenção"
                    HealthStatus.CRITICAL -> "Crítico"
                    else -> "Excelente"
                }
            ),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione o estado de saúde"
                )
            )
        ),
        FormFieldDefinition(
            id = "healthHistory",
            label = "Histórico de Saúde",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva o histórico de saúde do pet...",
            default = JsonPrimitive(pet.healthHistory.toString()),
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