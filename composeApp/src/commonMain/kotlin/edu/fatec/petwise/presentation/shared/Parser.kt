package edu.fatec.petwise.features.auth.shared

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SchemaParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(raw: String): FormSchema =
        json.decodeFromString<FormSchema>(raw)
}
