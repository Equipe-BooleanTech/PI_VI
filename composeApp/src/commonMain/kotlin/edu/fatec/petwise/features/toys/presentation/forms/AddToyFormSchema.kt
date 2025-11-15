package edu.fatec.petwise.features.toys.presentation.forms

import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

val addToyFormConfiguration: FormConfiguration = FormConfiguration(
    id = "add_toy_form",
    title = "Adicionar Brinquedo",
    description = "Registre um novo brinquedo para pets.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "name",
            label = "Nome do Brinquedo",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Bola de Tênis, Corda Interativa, Pelúcia...",
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
            placeholder = "Ex: Kong, Chuckit, Petco...",
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
            options = listOf("Brinquedos Interativos", "Brinquedos de Mordida", "Brinquedos de Busca", "Pelúcias", "Bolas", "Corda", "Outros"),
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
            placeholder = "Descreva o brinquedo...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "price",
            label = "Preço (R$)",
            type = FormFieldType.DECIMAL,
            placeholder = "Ex: 29.90",
            default = JsonPrimitive("0.00"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Preço é obrigatório"
                ),
                ValidationRule(
                    type = ValidationType.DECIMAL,
                    message = "Digite um preço válido (ex: 29.90)"
                )
            )
        ),
        FormFieldDefinition(
            id = "stock",
            label = "Estoque",
            type = FormFieldType.NUMBER,
            placeholder = "Ex: 50",
            default = JsonPrimitive("0"),
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
            options = listOf("Unidade", "Pacote", "Caixa", "Kit"),
            default = JsonPrimitive("Unidade"),
            validators = listOf(
                ValidationRule(
                    type = ValidationType.REQUIRED,
                    message = "Selecione a unidade"
                )
            )
        ),
        FormFieldDefinition(
            id = "material",
            label = "Material",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Plástico, Tecido, Borracha, Madeira...",
            validators = emptyList(),
            formatting = FieldFormatting(capitalize = true)
        ),
        FormFieldDefinition(
            id = "ageRecommendation",
            label = "Recomendação de Idade",
            type = FormFieldType.TEXT,
            placeholder = "Ex: Para filhotes, Adultos, Todos os portes...",
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "imageUrl",
            label = "URL da Imagem",
            type = FormFieldType.TEXT,
            placeholder = "https://...",
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
            label = "Adicionar Brinquedo",
            type = FormFieldType.SUBMIT
        )
    ),
    validationBehavior = ValidationBehavior.ON_BLUR,
    submitBehavior = SubmitBehavior.API_CALL,
    styling = FormStyling(
        primaryColor = "#FF9800",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)