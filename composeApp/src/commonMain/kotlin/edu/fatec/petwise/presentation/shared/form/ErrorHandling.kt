package edu.fatec.petwise.presentation.shared.form

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow


@Immutable
sealed class FormError {
    abstract val id: String
    abstract val message: String
    abstract val timestamp: Long

    data class ValidationError(
        override val id: String,
        override val message: String,
        override val timestamp: Long = currentTimeMs(),
        val fieldId: String,
        val validationType: ValidationType
    ) : FormError()

    data class ApiError(
        override val id: String,
        override val message: String,
        override val timestamp: Long = currentTimeMs(),
        val code: String? = null,
        val details: Map<String, Any> = emptyMap(),
        val retryable: Boolean = true
    ) : FormError()

    data class NetworkError(
        override val id: String,
        override val message: String,
        override val timestamp: Long = currentTimeMs(),
        val cause: Throwable? = null
    ) : FormError()

    data class BusinessLogicError(
        override val id: String,
        override val message: String,
        override val timestamp: Long = currentTimeMs(),
        val context: Map<String, Any> = emptyMap()
    ) : FormError()

    data class SystemError(
        override val id: String,
        override val message: String,
        override val timestamp: Long = currentTimeMs(),
        val exception: Throwable? = null
    ) : FormError()
}

@Immutable
data class ErrorState(
    val fieldErrors: Map<String, List<FormError.ValidationError>> = emptyMap(),
    val globalErrors: List<FormError> = emptyList(),
    val isLoadingErrors: Boolean = false,
    val errorDisplayStrategy: ErrorDisplayStrategy = ErrorDisplayStrategy.IMMEDIATE
)

enum class ErrorDisplayStrategy {
    IMMEDIATE,
    ON_BLUR,
    ON_SUBMIT,
    DEBOUNCED
}

interface ErrorMessageProvider {
    suspend fun getErrorMessage(
        errorType: ValidationType,
        fieldId: String,
        context: Map<String, Any> = emptyMap()
    ): Result<String>

    suspend fun getApiErrorMessage(
        errorCode: String,
        context: Map<String, Any> = emptyMap()
    ): Result<String>
}

class DefaultErrorMessageProvider : ErrorMessageProvider {

    private val validationMessages = mapOf(
        ValidationType.REQUIRED to "Este campo é obrigatório",
        ValidationType.EMAIL to "Digite um email válido",
        ValidationType.PHONE to "Digite um telefone válido",
        ValidationType.CPF to "Digite um CPF válido",
        ValidationType.CNPJ to "Digite um CNPJ válido",
        ValidationType.CEP to "Digite um CEP válido",
        ValidationType.MIN_LENGTH to "Texto muito curto",
        ValidationType.MAX_LENGTH to "Texto muito longo",
        ValidationType.PATTERN to "Formato inválido",
        ValidationType.NUMERIC to "Digite apenas números",
        ValidationType.DECIMAL to "Digite um número válido",
        ValidationType.DATE to "Digite uma data válida",
        ValidationType.PASSWORD_STRENGTH to "Senha muito fraca",
        ValidationType.MATCHES_FIELD to "Os campos não coincidem",
        ValidationType.UNIQUE to "Este valor já está em uso"
    )

    override suspend fun getErrorMessage(
        errorType: ValidationType,
        fieldId: String,
        context: Map<String, Any>
    ): Result<String> {
        return Result.success(
            validationMessages[errorType] ?: "Erro de validação"
        )
    }

    override suspend fun getApiErrorMessage(
        errorCode: String,
        context: Map<String, Any>
    ): Result<String> {
        return Result.success("Erro do servidor: $errorCode")
    }
}

interface ErrorHandler {
    suspend fun handleError(error: FormError): ErrorHandlingResult
    fun canHandle(error: FormError): Boolean
}

sealed class ErrorHandlingResult {
    object Handled : ErrorHandlingResult()
    data class Retry(val delayMs: Long = 0) : ErrorHandlingResult()
    data class ShowMessage(val message: String) : ErrorHandlingResult()
    data class Navigate(val destination: String) : ErrorHandlingResult()
    object Ignore : ErrorHandlingResult()
}

class DefaultErrorHandler : ErrorHandler {

    override suspend fun handleError(error: FormError): ErrorHandlingResult {
        return ErrorHandlingResult.ShowMessage(error.message)
    }

    override fun canHandle(error: FormError): Boolean = true
}

interface ErrorAggregator {
    suspend fun collectErrors(
        formId: String,
        fieldValues: Map<String, Any>
    ): List<FormError>

    suspend fun processApiErrors(
        response: Map<String, Any>
    ): List<FormError>
}

class DefaultErrorAggregator(
    private val errorMessageProvider: ErrorMessageProvider
) : ErrorAggregator {

    override suspend fun collectErrors(
        formId: String,
        fieldValues: Map<String, Any>
    ): List<FormError> {
        return emptyList()
    }

    override suspend fun processApiErrors(
        response: Map<String, Any>
    ): List<FormError> {
        val errors = mutableListOf<FormError>()

        response["field_errors"]?.let { fieldErrors ->
            if (fieldErrors is Map<*, *>) {
                fieldErrors.forEach { (fieldId, errorMessages) ->
                    if (fieldId is String && errorMessages is List<*>) {
                        errorMessages.filterIsInstance<String>().forEach { message ->
                            errors.add(
                                FormError.ValidationError(
                                    id = "api_${fieldId}_${currentTimeMs()}",
                                    message = message,
                                    fieldId = fieldId,
                                    validationType = ValidationType.API_VALIDATION
                                )
                            )
                        }
                    }
                }
            }
        }

        response["errors"]?.let { globalErrors ->
            if (globalErrors is List<*>) {
                globalErrors.filterIsInstance<String>().forEach { message ->
                    errors.add(
                        FormError.ApiError(
                            id = "api_global_${currentTimeMs()}",
                            message = message,
                            code = response["error_code"] as? String
                        )
                    )
                }
            }
        }

        return errors
    }
}