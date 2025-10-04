package edu.fatec.petwise.features.auth.shared

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.Serializable

@Serializable
data class FormSchema(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val layout: Layout? = null,
    val fields: List<Field>
)

@Serializable
data class Layout(val columns: Int = 1, val maxWidth: Int? = null)

@Serializable
data class Field(
    val id: String,
    val label: String? = null,
    val type: String,
    val placeholder: String? = null,
    val options: List<String>? = null,
    val default: JsonElement? = null,
    val validators: List<Validator>? = null,
    val visibleIf: Map<String, JsonElement>? = null,
    val mask: String? = null
)

@Serializable
data class Validator(val type: String, val message: String? = null, val value: JsonElement? = null, val field: String? = null)