package edu.fatec.petwise.presentation.shared.form

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive


@Immutable
data class FormState(
    val id: String,
    val configuration: FormConfiguration,
    val fieldStates: Map<String, FieldState> = emptyMap(),
    val isSubmitting: Boolean = false,
    val isValidating: Boolean = false,
    val isValid: Boolean = false,
    val errors: ErrorState = ErrorState(),
    val metadata: FormMetadata = FormMetadata(),
    val asyncOperations: Map<String, AsyncOperationState> = emptyMap()
)

@Immutable
data class FieldState(
    val id: String,
    val value: Any? = null,
    val displayValue: String = "",
    val isVisible: Boolean = true,
    val isEnabled: Boolean = true,
    val isFocused: Boolean = false,
    val isTouched: Boolean = false,
    val isDirty: Boolean = false,
    val isValidating: Boolean = false,
    val isValid: Boolean = true,
    val errors: List<FormError.ValidationError> = emptyList(),
    val lastValidated: Long = 0,
    val metadata: Map<String, Any> = emptyMap()
)

@Immutable
data class FormMetadata(
    val createdAt: Long = currentTimeMs(),
    val lastModified: Long = currentTimeMs(),
    val submitCount: Int = 0,
    val validationCount: Int = 0,
    val customData: Map<String, Any> = emptyMap()
)

@Immutable
data class AsyncOperationState(
    val id: String,
    val operation: String,
    val isRunning: Boolean = false,
    val startedAt: Long = 0,
    val progress: Float = 0f,
    val retryCount: Int = 0,
    val result: Any? = null,
    val error: FormError? = null
)

private fun generateFormId(): String {
    return "form_${(0..999999).random()}"
}



class DynamicFormViewModel(
    private val initialConfiguration: FormConfiguration,
    private val eventDispatcher: FormEventDispatcher = DefaultFormEventDispatcher(),
    private val validationEngine: ValidationEngine = DefaultValidationEngine(),
    private val errorHandler: ErrorHandler = DefaultErrorHandler(),
    private val errorMessageProvider: ErrorMessageProvider = DefaultErrorMessageProvider(),
    private val lifecycleCallbacks: FormLifecycleCallbacks? = null,
    private val operationCallbacks: FormOperationCallbacks? = null,
    private val fieldCallbacks: FieldCallbacks? = null
) : ViewModel() {

    private val formScope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())

    private val _state = MutableStateFlow(
        FormState(
            id = generateFormId(),
            configuration = initialConfiguration
        )
    )
    val state: StateFlow<FormState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<FormEvent>(extraBufferCapacity = 64)
    val events: Flow<FormEvent> = _events.asSharedFlow()

    init {
        initializeForm()
        setupEventHandling()
        setupAsyncValidation()
    }

    private fun initializeForm() {
        formScope.launch {
            val fieldStates = initializeFieldStates()
            _state.value = _state.value.copy(fieldStates = fieldStates)

            lifecycleCallbacks?.onFormInitialized(
                _state.value.id,
                _state.value.configuration
            )

            emitEvent(
                FormEvent.FormConfigurationChanged(
                    formId = _state.value.id,
                    newConfiguration = initialConfiguration
                )
            )

            recomputeVisibilityAndValidation()
        }
    }

    private fun initializeFieldStates(): Map<String, FieldState> {
        return initialConfiguration.fields
            .filter { it.type != FormFieldType.SUBMIT }
            .associate { field ->
                val defaultValue = field.default?.jsonPrimitive?.content ?: ""
                field.id to FieldState(
                    id = field.id,
                    value = defaultValue,
                    displayValue = defaultValue,
                    isVisible = true,
                    isEnabled = true
                )
            }
    }

    private fun setupEventHandling() {

        eventDispatcher.registerHandler(ValidationEventHandler())
        eventDispatcher.registerHandler(
            SubmissionEventHandler(operationCallbacks ?: object : FormOperationCallbacks {})
        )

        formScope.launch {
            eventDispatcher.events.collect { event ->
                _events.emit(event)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupAsyncValidation() {
        if (initialConfiguration.validationBehavior == ValidationBehavior.DEBOUNCED) {
            formScope.launch {
                events
                    .filterIsInstance<FormEvent.FieldValueChanged>()
                    .debounce(300)
                    .collect { event ->
                        validateField(event.fieldId)
                    }
            }
        }
    }

    fun updateFieldValue(fieldId: String, newValue: Any?, isUserInput: Boolean = true) {
        formScope.launch {
            val currentState = _state.value
            val fieldState = currentState.fieldStates[fieldId] ?: return@launch
            val oldValue = fieldState.value

            val updatedFieldState = fieldState.copy(
                value = newValue,
                displayValue = newValue?.toString() ?: "",
                isTouched = if (isUserInput) true else fieldState.isTouched,
                isDirty = oldValue != newValue
            )

            val updatedStates = currentState.fieldStates.toMutableMap()
            updatedStates[fieldId] = updatedFieldState

            _state.value = currentState.copy(
                fieldStates = updatedStates,
                metadata = currentState.metadata.copy(
                    lastModified = currentTimeMs()
                )
            )

            emitEvent(
                FormEvent.FieldValueChanged(
                    formId = currentState.id,
                    fieldId = fieldId,
                    oldValue = oldValue,
                    newValue = newValue
                )
            )

            fieldCallbacks?.onFieldChanged(fieldId, oldValue, newValue)

            when (currentState.configuration.validationBehavior) {
                ValidationBehavior.ON_CHANGE -> validateField(fieldId)
                ValidationBehavior.DEBOUNCED -> {}
                else -> {}
            }

            recomputeVisibilityAndValidation()
        }
    }

    fun focusField(fieldId: String) {
        formScope.launch {
            val currentState = _state.value
            val fieldState = currentState.fieldStates[fieldId] ?: return@launch

            val updatedFieldState = fieldState.copy(isFocused = true)
            val updatedStates = currentState.fieldStates.toMutableMap()
            updatedStates[fieldId] = updatedFieldState

            _state.value = currentState.copy(fieldStates = updatedStates)

            emitEvent(
                FormEvent.FieldFocused(
                    formId = currentState.id,
                    fieldId = fieldId
                )
            )

            fieldCallbacks?.onFieldFocused(fieldId)
        }
    }

    fun blurField(fieldId: String) {
        formScope.launch {
            val currentState = _state.value
            val fieldState = currentState.fieldStates[fieldId] ?: return@launch

            val updatedFieldState = fieldState.copy(isFocused = false, isTouched = true)
            val updatedStates = currentState.fieldStates.toMutableMap()
            updatedStates[fieldId] = updatedFieldState

            _state.value = currentState.copy(fieldStates = updatedStates)

            emitEvent(
                FormEvent.FieldBlurred(
                    formId = currentState.id,
                    fieldId = fieldId,
                    value = fieldState.value
                )
            )

            fieldCallbacks?.onFieldBlurred(fieldId)

            if (currentState.configuration.validationBehavior == ValidationBehavior.ON_BLUR) {
                validateField(fieldId)
            }
        }
    }

    private suspend fun validateField(fieldId: String) {
        val currentState = _state.value
        val fieldDefinition = currentState.configuration.fields.find { it.id == fieldId }
            ?: return

        val fieldState = currentState.fieldStates[fieldId] ?: return

        val updatedFieldState = fieldState.copy(
            isValidating = true,
            lastValidated = currentTimeMs()
        )
        val updatedStates = currentState.fieldStates.toMutableMap()
        updatedStates[fieldId] = updatedFieldState

        _state.value = currentState.copy(
            fieldStates = updatedStates,
            isValidating = true
        )

        try {
            val validationResult = validationEngine.validateField(
                fieldDefinition,
                fieldState.value,
                getAllFieldValues()
            )

            val errors = when (validationResult) {
                is ValidationResult.Valid -> emptyList()
                is ValidationResult.Invalid -> validationResult.errors
                is ValidationResult.Pending -> {
                    return
                }
            }

            val finalFieldState = updatedFieldState.copy(
                isValidating = false,
                isValid = errors.isEmpty(),
                errors = errors
            )

            val finalStates = currentState.fieldStates.toMutableMap()
            finalStates[fieldId] = finalFieldState

            val finalState = currentState.copy(
                fieldStates = finalStates,
                isValidating = false
            )

            _state.value = finalState

            emitEvent(
                FormEvent.FieldValidated(
                    formId = currentState.id,
                    fieldId = fieldId,
                    isValid = errors.isEmpty(),
                    errors = errors
                )
            )

            fieldCallbacks?.onFieldValidated(fieldId, errors.isEmpty(), errors)

        } catch (e: Exception) {
            val error = FormError.SystemError(
                id = "validation_error_$fieldId",
                message = "Validation failed: ${e.message}",
                exception = e
            )

            handleError(error)
        }
    }

    fun submitForm() {
        formScope.launch {
            val currentState = _state.value

            lifecycleCallbacks?.onFormSubmitting(currentState.id, getAllFieldValues())

            _state.value = currentState.copy(
                isSubmitting = true,
                metadata = currentState.metadata.copy(
                    submitCount = currentState.metadata.submitCount + 1
                )
            )

            try {
                val validationResult = validateEntireForm()

                if (validationResult is ValidationResult.Invalid) {
                    _state.value = _state.value.copy(isSubmitting = false)

                    val businessError = FormError.BusinessLogicError(
                        id = "form_validation_failed",
                        message = "Form validation failed"
                    )

                    lifecycleCallbacks?.onFormSubmitError(currentState.id, businessError)
                    operationCallbacks?.onFailure("submit", businessError)

                    return@launch
                }

                emitEvent(
                    FormEvent.FormSubmitted(
                        formId = currentState.id,
                        values = getAllFieldValues(),
                        isValid = true
                    )
                )

                when (currentState.configuration.submitBehavior) {
                    SubmitBehavior.API_CALL -> handleApiSubmission()
                    SubmitBehavior.CUSTOM_HANDLER -> handleCustomSubmission()
                    SubmitBehavior.MULTI_STEP -> handleMultiStepSubmission()
                    SubmitBehavior.DEFAULT -> handleDefaultSubmission()
                }

            } catch (e: Exception) {
                val error = FormError.SystemError(
                    id = "submit_error",
                    message = "Submit failed: ${e.message}",
                    exception = e
                )

                _state.value = _state.value.copy(isSubmitting = false)
                handleError(error)
                lifecycleCallbacks?.onFormSubmitError(currentState.id, error)
            }
        }
    }

    private suspend fun validateEntireForm(): ValidationResult {
        val currentState = _state.value
        val allErrors = mutableListOf<FormError.ValidationError>()

        for (field in currentState.configuration.fields) {
            if (field.type == FormFieldType.SUBMIT) continue

            val fieldState = currentState.fieldStates[field.id] ?: continue
            if (!fieldState.isVisible) continue

            val fieldValidationResult = validationEngine.validateField(
                field,
                fieldState.value,
                getAllFieldValues()
            )

            if (fieldValidationResult is ValidationResult.Invalid) {
                allErrors.addAll(fieldValidationResult.errors)
            }
        }

        val isValid = allErrors.isEmpty()
        _state.value = _state.value.copy(isValid = isValid)

        emitEvent(
            FormEvent.FormValidated(
                formId = currentState.id,
                isValid = isValid,
                errors = allErrors
            )
        )

        lifecycleCallbacks?.onFormValidationChanged(currentState.id, isValid, allErrors)

        return if (isValid) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(allErrors)
        }
    }

    private suspend fun handleApiSubmission() {
        val apiConfig = _state.value.configuration.apiConfiguration
        if (apiConfig?.submitUrl != null) {

        }

        _state.value = _state.value.copy(isSubmitting = false)

        val successResult = ApiResult.Success(getAllFieldValues())
        lifecycleCallbacks?.onFormSubmitSuccess(_state.value.id, successResult)
        operationCallbacks?.onSuccess("submit", getAllFieldValues())
    }

    private suspend fun handleCustomSubmission() {

        _state.value = _state.value.copy(isSubmitting = false)
        operationCallbacks?.onSuccess("submit", getAllFieldValues())
    }

    private suspend fun handleMultiStepSubmission() {
        _state.value = _state.value.copy(isSubmitting = false)
        operationCallbacks?.onSuccess("submit", getAllFieldValues())
    }

    private suspend fun handleDefaultSubmission() {
        delay(1000)
        _state.value = _state.value.copy(isSubmitting = false)

        val successResult = ApiResult.Success(getAllFieldValues())
        lifecycleCallbacks?.onFormSubmitSuccess(_state.value.id, successResult)
        operationCallbacks?.onSuccess("submit", getAllFieldValues())
    }

    private suspend fun recomputeVisibilityAndValidation() {
        val currentState = _state.value
        val allValues = getAllFieldValues()
        val updatedStates = currentState.fieldStates.toMutableMap()

        for (field in currentState.configuration.fields) {
            if (field.type == FormFieldType.SUBMIT) continue

            val fieldState = updatedStates[field.id] ?: continue
            val isVisible = evaluateVisibility(field, allValues)

            if (fieldState.isVisible != isVisible) {
                updatedStates[field.id] = fieldState.copy(isVisible = isVisible)
            }
        }

        _state.value = currentState.copy(fieldStates = updatedStates)
    }

    private fun evaluateVisibility(
        field: FormFieldDefinition,
        allValues: Map<String, Any>
    ): Boolean {
        val visibilityRule = field.visibility ?: return true

        return when (visibilityRule.operator) {
            LogicalOperator.AND -> {
                visibilityRule.conditions.all { condition ->
                    evaluateCondition(condition, allValues)
                }
            }
            LogicalOperator.OR -> {
                visibilityRule.conditions.any { condition ->
                    evaluateCondition(condition, allValues)
                }
            }
        }
    }

    private fun evaluateCondition(
        condition: VisibilityCondition,
        allValues: Map<String, Any>
    ): Boolean {
        val actualValue = allValues[condition.fieldId]?.toString() ?: ""
        val expectedValue = condition.value.jsonPrimitive.content

        return when (condition.operator) {
            ComparisonOperator.EQUALS -> actualValue == expectedValue
            ComparisonOperator.NOT_EQUALS -> actualValue != expectedValue
            ComparisonOperator.CONTAINS -> actualValue.contains(expectedValue, ignoreCase = true)
            ComparisonOperator.NOT_CONTAINS -> !actualValue.contains(expectedValue, ignoreCase = true)
            ComparisonOperator.GREATER_THAN -> {
                try {
                    actualValue.toDouble() > expectedValue.toDouble()
                } catch (e: NumberFormatException) {
                    false
                }
            }
            ComparisonOperator.LESS_THAN -> {
                try {
                    actualValue.toDouble() < expectedValue.toDouble()
                } catch (e: NumberFormatException) {
                    false
                }
            }
            ComparisonOperator.IN_LIST -> {
                expectedValue.split(",").map { it.trim() }.contains(actualValue)
            }
            ComparisonOperator.NOT_IN_LIST -> {
                !expectedValue.split(",").map { it.trim() }.contains(actualValue)
            }
        }
    }

    private fun getAllFieldValues(): Map<String, Any> {
        return _state.value.fieldStates.mapValues { (_, fieldState) ->
            fieldState.value ?: ""
        }
    }

    private suspend fun handleError(error: FormError) {
        val handlingResult = errorHandler.handleError(error)

        when (handlingResult) {
            is ErrorHandlingResult.Retry -> {
                delay(handlingResult.delayMs)
            }
            is ErrorHandlingResult.ShowMessage -> {
                val currentErrorState = _state.value.errors
                val updatedErrorState = currentErrorState.copy(
                    globalErrors = currentErrorState.globalErrors + error
                )
                _state.value = _state.value.copy(errors = updatedErrorState)
            }
            is ErrorHandlingResult.Navigate -> {
                emitEvent(
                    FormEvent.NavigationRequested(
                        formId = _state.value.id,
                        destination = handlingResult.destination
                    )
                )
            }
            is ErrorHandlingResult.Handled -> {

            }
            is ErrorHandlingResult.Ignore -> {

            }
        }

        emitEvent(
            FormEvent.ErrorOccurred(
                formId = _state.value.id,
                error = error
            )
        )
    }

    private suspend fun emitEvent(event: FormEvent) {
        _events.emit(event)
        eventDispatcher.dispatch(event)
    }

    fun resetForm() {
        formScope.launch {
            val currentState = _state.value
            val resetStates = initializeFieldStates()

            _state.value = currentState.copy(
                fieldStates = resetStates,
                isSubmitting = false,
                isValidating = false,
                isValid = false,
                errors = ErrorState(),
                metadata = FormMetadata(),
                asyncOperations = emptyMap()
            )

            emitEvent(FormEvent.FormReset(formId = currentState.id))
            lifecycleCallbacks?.onFormReset(currentState.id)
        }
    }

    fun updateConfiguration(newConfiguration: FormConfiguration) {
        formScope.launch {
            _state.value = _state.value.copy(configuration = newConfiguration)
            initializeForm()
        }
    }

    override fun onCleared() {
        super.onCleared()
        formScope.launch {
            lifecycleCallbacks?.onFormDestroyed(_state.value.id)
        }
    }
}