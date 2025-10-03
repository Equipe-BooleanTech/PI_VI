package edu.fatec.petwise.features.auth.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

data class FormUiState(val fields: List<FieldState>, val isSubmitting: Boolean = false)

class FormStore(
    initialSchema: FormSchema,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private var _schema = initialSchema
    val schema: FormSchema get() = _schema

    private val fieldStateMap = mutableMapOf<String, FieldState>()

    private val _state = MutableStateFlow(FormUiState(emptyList()))
    val state: StateFlow<FormUiState> = _state

    init {
        initializeFields()
        recomputeVisibilityAndValidation()
    }

    private fun initializeFields() {
        fieldStateMap.clear()
        for (field in _schema.fields) {
            val defaultValue = field.default?.jsonPrimitive?.content ?: ""
            fieldStateMap[field.id] = FieldState(
                id = field.id,
                value = defaultValue,
                visible = true,
                errors = emptyList()
            )
        }
    }

    fun updateSchema(newSchema: FormSchema) {
        _schema = newSchema
        initializeFields()
        recomputeVisibilityAndValidation()
    }

    fun updateValue(fieldId: String, newValue: String) {
        fieldStateMap[fieldId]?.let { fieldState ->
            fieldStateMap[fieldId] = fieldState.copy(value = newValue)
            recomputeVisibilityAndValidation()
        }
    }

    private fun recomputeVisibilityAndValidation() {
        // Update visibility based on current field values
        for (field in _schema.fields) {
            fieldStateMap[field.id]?.let { fieldState ->
                val isVisible = evaluateVisibility(field)
                fieldStateMap[field.id] = fieldState.copy(visible = isVisible)
            }
        }

        // Validate visible fields
        val currentValues = fieldStateMap.mapValues { it.value.value }
        for (field in _schema.fields) {
            fieldStateMap[field.id]?.let { fieldState ->
                val errors = if (fieldState.visible) {
                    Validators.validate(field, currentValues)
                } else {
                    emptyList()
                }
                fieldStateMap[field.id] = fieldState.copy(errors = errors)
            }
        }

        _state.value = FormUiState(fieldStateMap.values.toList())
    }

    private fun evaluateVisibility(field: Field): Boolean {
        val cond = field.visibleIf ?: return true
        for ((key, value) in cond) {
            val desired = value.jsonPrimitive.content
            val actual = fieldStateMap[key]?.value ?: ""
            if (actual != desired) return false
        }
        return true
    }

    fun getCurrentValues(): Map<String, String> {
        return fieldStateMap.mapValues { it.value.value }
    }

    fun submit(onResult: (success: Boolean, errors: Map<String, List<String>>) -> Unit) {
        scope.launch {
            recomputeVisibilityAndValidation()
            val errs = fieldStateMap.filter {
                it.value.visible && it.value.errors.isNotEmpty()
            }.mapValues { it.value.errors }

            if (errs.isNotEmpty()) {
                onResult(false, errs)
            } else {
                onResult(true, emptyMap())
            }
        }
    }
}
