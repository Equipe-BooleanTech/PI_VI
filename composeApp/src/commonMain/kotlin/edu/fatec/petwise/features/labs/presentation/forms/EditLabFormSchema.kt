package edu.fatec.petwise.features.labs.presentation.forms

import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.presentation.shared.form.*
import kotlinx.serialization.json.JsonPrimitive

fun createEditLabFormConfiguration(lab: Lab): FormConfiguration = FormConfiguration(
    id = "edit_lab_form",
    title = "Editar Laboratório - ${lab.name}",
    description = "Atualize as informações do laboratório.",
    layout = FormLayout(
        columns = 1,
        maxWidth = 600,
        spacing = 16,
        responsive = true
    ),
    fields = listOf(
        FormFieldDefinition(
            id = "name",
            label = "Nome do Laboratório",
            type = FormFieldType.TEXT,
            placeholder = "Ex: LabVet, BioLab, VetCare...",
            default = JsonPrimitive(lab.name),
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
            id = "contactInfo",
            label = "Informações de Contato",
            type = FormFieldType.TEXT,
            placeholder = "Telefone, email ou endereço...",
            default = JsonPrimitive(lab.contactInfo ?: ""),
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
        primaryColor = "#009688",
        errorColor = "#FF3B30",
        successColor = "#34C759",
        fieldHeight = 56,
        borderRadius = 8,
        spacing = 16
    )
)