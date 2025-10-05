package edu.fatec.petwise.features.auth.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import edu.fatec.petwise.features.auth.domain.usecases.LoginUseCase

data class FormUiState(
    val fields: List<FieldState>, 
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class FormStore(
    initialSchema: FormSchema,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val loginUseCase: LoginUseCase = LoginUseCase()
) {
    private var _schema = initialSchema
    val schema: FormSchema get() = _schema

    private val fieldStateMap = mutableMapOf<String, FieldState>()

    private val _state = MutableStateFlow(FormUiState(emptyList()))
    val state: StateFlow<FormUiState> = _state

    init {
        Validators.setSchema(_schema)
        initializeFields()
        recomputeVisibilityAndValidation()
    }

    private fun initializeFields() {
        fieldStateMap.clear()
        for (field in _schema.fields) {

            if (field.type == "submit") continue
            
            val defaultValue = field.default?.jsonPrimitive?.content ?: ""
            fieldStateMap[field.id] = FieldState(
                id = field.id,
                value = defaultValue,
                visible = true,
                errors = emptyList(),
                touched = false,
                submitted = false
            )
        }
        
        _state.value = FormUiState(fieldStateMap.values.toList())
    }

    fun updateSchema(newSchema: FormSchema) {
        _schema = newSchema
        Validators.setSchema(newSchema)
        Validators.setFormSubmitted(false)
        initializeFields()
        recomputeVisibilityAndValidation()
    }
    
    fun validateField(fieldId: String): List<String> {
        val field = _schema.fields.find { it.id == fieldId } ?: return emptyList()
        val values = getCurrentValues()
        return Validators.validate(field, values)
    }

    fun updateValue(fieldId: String, newValue: String) {
        fieldStateMap[fieldId]?.let { fieldState ->
            val firstTouch = !fieldState.touched
            
            fieldStateMap[fieldId] = fieldState.copy(value = newValue, touched = true)
            
            recomputeVisibilityAndValidation()
        }
    }

    private fun recomputeVisibilityAndValidation() {
        for (field in _schema.fields) {
            fieldStateMap[field.id]?.let { fieldState ->
                val isVisible = evaluateVisibility(field)
                fieldStateMap[field.id] = fieldState.copy(visible = isVisible)
            }
        }

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

            Validators.setFormSubmitted(true)
            _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)

            fieldStateMap.forEach { (id, state) ->
                fieldStateMap[id] = state.copy(submitted = true, touched = true)
            }
            

            recomputeVisibilityAndValidation()
            val errs = fieldStateMap.filter {
                it.value.visible && it.value.errors.isNotEmpty()
            }.mapValues { it.value.errors }

            if (errs.isNotEmpty()) {
                _state.value = _state.value.copy(isSubmitting = false)
                onResult(false, errs)
            } else {
                when (_schema.id) {
                    "login_form" -> {
                        try {
                            val email = fieldStateMap["email"]?.value ?: ""
                            val password = fieldStateMap["password"]?.value ?: ""
                            
                            val result = loginUseCase.execute(email, password)
                            
                            result.fold(
                                onSuccess = { 
                                    _state.value = _state.value.copy(isSubmitting = false)
                                    onResult(true, emptyMap()) 
                                },
                                onFailure = { error ->
                                    _state.value = _state.value.copy(
                                        isSubmitting = false, 
                                        errorMessage = error.message ?: "Login failed"
                                    )
                                    onResult(false, mapOf("loginError" to listOf(error.message ?: "Invalid credentials")))
                                }
                            )
                        } catch (e: Exception) {
                            _state.value = _state.value.copy(
                                isSubmitting = false, 
                                errorMessage = e.message ?: "An unknown error occurred"
                            )
                            onResult(false, mapOf("error" to listOf(e.message ?: "An unknown error occurred")))
                        }
                    }
                    else -> {
                        _state.value = _state.value.copy(isSubmitting = false)
                        onResult(true, emptyMap())
                    }
                }
            }
        }
    }
}
