package edu.fatec.petwise.presentation.shared.form

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow


@Immutable
sealed class FormEvent {
    abstract val formId: String
    abstract val timestamp: Long

    data class FieldValueChanged(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val oldValue: Any?,
        val newValue: Any?,
        val isUserInput: Boolean = true
    ) : FormEvent()

    data class FieldFocused(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String
    ) : FormEvent()

    data class FieldBlurred(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val value: Any?
    ) : FormEvent()

    data class FieldValidated(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val isValid: Boolean,
        val errors: List<FormError.ValidationError>
    ) : FormEvent()

    data class FormSubmitted(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val values: Map<String, Any>,
        val isValid: Boolean
    ) : FormEvent()

    data class FormValidated(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val isValid: Boolean,
        val errors: List<FormError>
    ) : FormEvent()

    data class FormReset(
        override val formId: String,
        override val timestamp: Long = currentTimeMs()
    ) : FormEvent()

    data class FormConfigurationChanged(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val newConfiguration: FormConfiguration
    ) : FormEvent()

    data class AsyncValidationStarted(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val validationType: ValidationType
    ) : FormEvent()

    data class AsyncValidationCompleted(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val validationType: ValidationType,
        val result: ValidationResult
    ) : FormEvent()

    data class ApiCallStarted(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val operation: String,
        val parameters: Map<String, Any> = emptyMap()
    ) : FormEvent()

    data class ApiCallCompleted(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val operation: String,
        val result: ApiResult
    ) : FormEvent()

    data class ErrorOccurred(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val error: FormError
    ) : FormEvent()

    data class ErrorResolved(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val errorId: String
    ) : FormEvent()

    data class CustomEvent(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val eventType: String,
        val data: Map<String, Any> = emptyMap()
    ) : FormEvent()

    data class NavigationRequested(
        override val formId: String,
        override val timestamp: Long = currentTimeMs(),
        val destination: String
    ) : FormEvent()
}

@Immutable
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<FormError.ValidationError>) : ValidationResult()
    data class Pending(val operation: String) : ValidationResult()
}

@Immutable
sealed class ApiResult {
    data class Success(val data: Map<String, Any>) : ApiResult()
    data class Error(val error: FormError.ApiError) : ApiResult()
    data class NetworkError(val error: FormError.NetworkError) : ApiResult()
}


interface FormEventHandler {
    suspend fun handleEvent(event: FormEvent): EventHandlingResult
    fun canHandle(event: FormEvent): Boolean
    val priority: Int get() = 0
}

sealed class EventHandlingResult {
    object Handled : EventHandlingResult()
    object Ignored : EventHandlingResult()
    data class Propagate(val newEvent: FormEvent) : EventHandlingResult()
    data class Multiple(val results: List<EventHandlingResult>) : EventHandlingResult()
}

interface FormEventDispatcher {
    suspend fun dispatch(event: FormEvent)
    fun registerHandler(handler: FormEventHandler)
    fun unregisterHandler(handler: FormEventHandler)
    val events: Flow<FormEvent>
}

interface FormLifecycleCallbacks {
    suspend fun onFormInitialized(formId: String, configuration: FormConfiguration) {}
    suspend fun onFormSubmitting(formId: String, values: Map<String, Any>) {}
    suspend fun onFormSubmitSuccess(formId: String, result: ApiResult.Success) {}
    suspend fun onFormSubmitError(formId: String, error: FormError) {}
    suspend fun onFormValidationChanged(formId: String, isValid: Boolean, errors: List<FormError>) {}
    suspend fun onFormReset(formId: String) {}
    suspend fun onFormDestroyed(formId: String) {}
}

interface FormOperationCallbacks {
    suspend fun onSuccess(operation: String, result: Any?) {}
    suspend fun onFailure(operation: String, error: FormError) {}
    suspend fun onProgress(operation: String, progress: Float) {}
    suspend fun onRetry(operation: String, attempt: Int) {}
}

interface FieldCallbacks {
    suspend fun onFieldChanged(fieldId: String, oldValue: Any?, newValue: Any?) {}
    suspend fun onFieldValidated(fieldId: String, isValid: Boolean, errors: List<FormError.ValidationError>) {}
    suspend fun onFieldFocused(fieldId: String) {}
    suspend fun onFieldBlurred(fieldId: String) {}
}

@Immutable
data class EventConfiguration(
    val enableAsyncValidation: Boolean = true,
    val enableEventLogging: Boolean = false,
    val debounceTimeMs: Long = 300,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,
    val enableBatching: Boolean = false,
    val batchSizeLimit: Int = 10,
    val batchTimeoutMs: Long = 100
)

class ValidationEventHandler : FormEventHandler {

    override suspend fun handleEvent(event: FormEvent): EventHandlingResult {
        return when (event) {
            is FormEvent.FieldValueChanged -> {
                EventHandlingResult.Handled
            }
            is FormEvent.FieldBlurred -> {
                EventHandlingResult.Handled
            }
            else -> EventHandlingResult.Ignored
        }
    }

    override fun canHandle(event: FormEvent): Boolean {
        return event is FormEvent.FieldValueChanged ||
               event is FormEvent.FieldBlurred
    }

    override val priority: Int = 100
}

class SubmissionEventHandler(
    private val callbacks: FormOperationCallbacks
) : FormEventHandler {

    override suspend fun handleEvent(event: FormEvent): EventHandlingResult {
        return when (event) {
            is FormEvent.FormSubmitted -> {
                if (event.isValid) {
                    callbacks.onSuccess("submit", event.values)
                } else {
                    callbacks.onFailure(
                        "submit",
                        FormError.BusinessLogicError(
                            id = "validation_failed",
                            message = "Form validation failed"
                        )
                    )
                }
                EventHandlingResult.Handled
            }
            else -> EventHandlingResult.Ignored
        }
    }

    override fun canHandle(event: FormEvent): Boolean {
        return event is FormEvent.FormSubmitted
    }

    override val priority: Int = 200
}