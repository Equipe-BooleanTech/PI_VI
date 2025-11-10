package edu.fatec.petwise.features.suprimentos.presentation.forms

import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createAddSuprimentoFormConfiguration(petOptions: List<SelectOption> = emptyList()): FormConfiguration = FormConfiguration(
    id = "add_suprimento_form",
    title = "Adicionar Suprimento",
    description = "Registre um novo suprimento para seu pet.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "petId",
            label = "Selecione o Pet",
            type = FormFieldType.SELECT,
            placeholder = "Escolha um pet",
            selectOptions = petOptions,
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione um pet"
                )
            )
        ),
        FormFieldDefinition(
            id = "description",
            label = "Descrição do Produto",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Ração Premium, Coleira Vermelha, Brinquedo de Corda...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Descrição é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(3),
                    message = "Descrição deve ter pelo menos 3 caracteres"
                )
            )
        ),
        FormFieldDefinition(
            id = "category",
            label = "Categoria",
            type = FormFieldType.SELECT,
            options = SuprimentCategory.getAllDisplayNames(),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione uma categoria"
                )
            )
        ),
        FormFieldDefinition(
            id = "price",
            label = "Preço (R$)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 45.90",
            default = JsonPrimitive("0.00"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Preço é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um preço válido (ex: 45.90)"
                )
            )
        ),
        FormFieldDefinition(
            id = "orderDate",
            label = "Data da Compra",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/YYYY",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Data da compra é obrigatória"
                ),
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Data inválida"
                )
            )
        ),
        FormFieldDefinition(
            id = "shopName",
            label = "Nome da Loja",
            type = FormFieldType.TEXT,
            placeholder = "Ex: PetShop Central, Amazon, Cobasi...",
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Nome da loja é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.MIN_LENGTH,
                    value = JsonPrimitive(2),
                    message = "Nome da loja deve ter pelo menos 2 caracteres"
                )
            )
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Adicionar Suprimento",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.DEFAULT,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)