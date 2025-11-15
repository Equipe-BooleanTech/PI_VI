package edu.fatec.petwise.features.food.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val addFoodFormConfiguration = FormConfiguration(
    id = "add_food_form",
    title = "Adicionar Ração/Alimento",
    description = "Cadastre um novo produto de alimentação para pets.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "name",
            label = "Nome do Produto",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Ração Premium para Cães Adultos, Sachê de Atum...",
            default = JsonPrimitive(""),
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
            id = "brand",
            label = "Marca",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Pedigree, Royal Canin, Whiskas...",
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Marca é obrigatória"
                )
            ),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "category",
            label = "Categoria",
            type = FormFieldType.SELECT,
            options = listOf("Ração Seca", "Ração Úmida", "Petiscos", "Suplementos", "Medicamentos", "Outros"),
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione uma categoria"
                )
            )
        ),
        FormFieldDefinition(
            id = "description",
            label = "Descrição",
            type = FormFieldType.TEXTAREA,
            placeholder = "Descreva o produto de alimentação...",
            default = JsonPrimitive(""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "price",
            label = "Preço (R$)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 89.90",
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Preço é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um preço válido (ex: 89.90)"
                )
            )
        ),
        FormFieldDefinition(
            id = "stock",
            label = "Estoque",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 100",
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Estoque é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.NUMERIC,
                    message = "Digite apenas números"
                )
            )
        ),
        FormFieldDefinition(
            id = "unit",
            label = "Unidade",
            type = FormFieldType.SELECT,
            options = listOf("Kg", "Unidade", "Pacote", "Lata", "Sachê", "Caixa"),
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione a unidade"
                )
            )
        ),
        FormFieldDefinition(
            id = "expiryDate",
            label = "Data de Validade",
            type = FormFieldType.DATE,
            placeholder = "DD/MM/AAAA",
            default = JsonPrimitive(""),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.DATE,
                    message = "Digite uma data válida (DD/MM/AAAA)"
                )
            )
        ),
        FormFieldDefinition(
            id = "imageUrl",
            label = "URL da Imagem",
            type = FormFieldType.TEXT,
            placeholder = "https://...",
            default = JsonPrimitive(""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "active",
            label = "Ativo",
            type = FormFieldType.SWITCH,
            default = JsonPrimitive(true),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Cadastrar Produto",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)