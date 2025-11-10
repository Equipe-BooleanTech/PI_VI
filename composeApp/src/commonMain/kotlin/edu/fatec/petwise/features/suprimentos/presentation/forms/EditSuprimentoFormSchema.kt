package edu.fatec.petwise.features.suprimentos.presentation.forms

import edu.fatec.petwise.features.suprimentos.domain.models.Suprimento
import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createEditSuprimentoFormConfiguration(
    suprimento: Suprimento,
    petOptions: List<SelectOption> = emptyList()
): FormConfiguration = FormConfiguration(
    id = "edit_suprimento_form",
    title = "Editar Suprimento",
    description = "Atualize as informações do suprimento.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "petId",
            label = "Pet",
            type = FormFieldType.SELECT,
            placeholder = "Escolha um pet",
            selectOptions = petOptions,
            default = JsonPrimitive(suprimento.petId),
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
            default = JsonPrimitive(suprimento.description),
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
            default = JsonPrimitive(suprimento.category.displayName),
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
            default = JsonPrimitive(suprimento.price.toString()),
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
            default = JsonPrimitive(suprimento.orderDate),
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
            default = JsonPrimitive(suprimento.shopName),
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
            label = "Salvar Alterações",
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

fun createFilterSuprimentosFormConfiguration(petOptions: List<SelectOption> = emptyList()): FormConfiguration = FormConfiguration(
    id = "filter_suprimentos_form",
    title = "Filtrar Suprimentos",
    description = "Use os filtros para encontrar suprimentos específicos.",
    layout = FormLayout(
        columns = 2,
        maxWidth = 600,
        spacing = 12,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "searchQuery",
            label = "Buscar",
            type = FormFieldType.TEXT,
            placeholder = "Descrição, loja...",
            default = JsonPrimitive(""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "petId",
            label = "Pet",
            type = FormFieldType.SELECT,
            placeholder = "Todos os pets",
            selectOptions = listOf(SelectOption("", "Todos os pets")) + petOptions,
            default = JsonPrimitive(""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "category",
            label = "Categoria",
            type = FormFieldType.SELECT,
            options = listOf("Todas as categorias") + SuprimentCategory.getAllDisplayNames(),
            default = JsonPrimitive("Todas as categorias"),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "minPrice",
            label = "Preço Mínimo",
            type = FormFieldType.DECIMAL,
            placeholder = "R$ 0,00",
            default = JsonPrimitive("")
        ),
        FormFieldDefinition(
            id = "maxPrice",
            label = "Preço Máximo",
            type = FormFieldType.DECIMAL,
            placeholder = "R$ 999,99",
            default = JsonPrimitive("")
        ),
        FormFieldDefinition(
            id = "shopName",
            label = "Loja",
            type = FormFieldType.TEXT,
            placeholder = "Nome da loja...",
            default = JsonPrimitive(""),
            validators = emptyList()
        ),
        FormFieldDefinition(
            id = "submit",
            label = "Filtrar",
            type = FormFieldType.SUBMIT
        ),
        FormFieldDefinition(
            id = "clear",
            label = "Limpar",
            type = FormFieldType.BUTTON
        )
    ),
    validationBehavior = ValidationBehavior.ON_CHANGE,
    submitBehavior = SubmitBehavior.DEFAULT,
    styling = FormStyling(
        primaryColor = "#2196F3",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 48,
        borderRadius = 8,
        spacing = 12
    )
)