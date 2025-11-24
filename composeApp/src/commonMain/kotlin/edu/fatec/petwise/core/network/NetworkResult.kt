  package edu.fatec.petwise.core.network

import kotlinx.serialization.Serializable

sealed class NetworkResult<out T> {

    data class Success<T>(
        val data: T,
        val metadata: ApiMetadata? = null
    ) : NetworkResult<T>()

    data class Error(
        val exception: NetworkException,
        val cachedData: Any? = null
    ) : NetworkResult<Nothing>()

    data class Loading(val progress: Float? = null) : NetworkResult<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Cannot get data during loading state")
    }

    inline fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data), metadata)
        is Error -> Error(exception, cachedData)
        is Loading -> Loading(progress)
    }

    inline fun <R> flatMap(transform: (T) -> NetworkResult<R>): NetworkResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> Error(exception, cachedData)
        is Loading -> Loading(progress)
    }

    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (NetworkException) -> Unit): NetworkResult<T> {
        if (this is Error) action(exception)
        return this
    }

    inline fun onLoading(action: (Float?) -> Unit): NetworkResult<T> {
        if (this is Loading) action(progress)
        return this
    }
}

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    abstract val isRetryable: Boolean

    class NoConnectivity(
        message: String = "Sem conexão com a internet"
    ) : NetworkException(message) {
        override val isRetryable: Boolean = true
    }


    class Timeout(
        message: String = "Tempo de requisição esgotado",
        val timeoutType: TimeoutType = TimeoutType.REQUEST
    ) : NetworkException(message) {
        override val isRetryable: Boolean = true
    }

    enum class TimeoutType {
        CONNECTION, SOCKET, REQUEST
    }

    data class HttpError(
        val code: Int,
        val errorBody: String? = null,
        val errorResponse: ApiErrorResponse? = null
    ) : NetworkException("${extractErrorMessage(errorResponse, errorBody)}") {
        override val isRetryable: Boolean = code >= 500
        val category: HttpErrorCategory = when (code) {
            in 400..499 -> HttpErrorCategory.CLIENT_ERROR
            in 500..599 -> HttpErrorCategory.SERVER_ERROR
            else -> HttpErrorCategory.UNKNOWN
        }
    }

    enum class HttpErrorCategory {
        CLIENT_ERROR, SERVER_ERROR, UNKNOWN
    }

    class SerializationError(
        message: String = "Falha ao processar resposta do servidor",
        cause: Throwable? = null,
        val rawResponse: String? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = false
    }

    class ServerError(
        val code: Int = 500,
        message: String = "Erro no servidor",
        cause: Throwable? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = true
    }

    class ClientError(
        val code: Int,
        message: String = "Erro na requisição",
        val validationErrors: Map<String, List<String>>? = null
    ) : NetworkException(message) {
        override val isRetryable: Boolean = false
    }

    class Unauthorized(
        message: String = "Autenticação necessária",
        val shouldRefreshToken: Boolean = true
    ) : NetworkException(message) {
        override val isRetryable: Boolean = shouldRefreshToken
    }

    class Forbidden(
        message: String = "Acesso negado",
        val requiredPermission: String? = null
    ) : NetworkException(message) {
        override val isRetryable: Boolean = false
    }

    class NotFound(
        message: String = "Usuário não encontrado",
        val resourceType: String? = null,
        val resourceId: String? = null
    ) : NetworkException(message) {
        override val isRetryable: Boolean = false
    }


    class Conflict(
        message: String = "Conflito ao processar requisição",
        val conflictDetails: String? = null
    ) : NetworkException(message) {
        override val isRetryable: Boolean = false
    }

    class RateLimitExceeded(
        message: String = "Limite de requisições excedido",
        val retryAfter: Long? = null,
    ) : NetworkException(message) {
        override val isRetryable: Boolean = true
    }

    class ServiceUnavailable(
        message: String = "Serviço temporariamente indisponível",
        val retryAfter: Long? = null
    ) : NetworkException(message) {
        override val isRetryable: Boolean = true
    }

    class BadGateway(
        message: String = "Erro de gateway"
    ) : NetworkException(message) {
        override val isRetryable: Boolean = true
    }

    class SslError(
        message: String = "Erro de certificado SSL",
        cause: Throwable? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = false
    }

    class DnsError(
        message: String = "Falha ao resolver nome do servidor",
        cause: Throwable? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = true
    }

    class RequestCancelled(
        message: String = "Operação cancelada pelo usuário",
        cause: Throwable? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = false
    }

    class Unknown(
        message: String = "Erro desconhecido",
        cause: Throwable? = null
    ) : NetworkException(message, cause) {
        override val isRetryable: Boolean = false
    }
}


fun <T> Result<T>.toNetworkResult(): NetworkResult<T> = fold(
    onSuccess = { NetworkResult.Success(it) },
    onFailure = { exception ->
        NetworkResult.Error(
            when (exception) {
                is NetworkException -> exception
                else -> NetworkException.Unknown(
                    exception.message ?: "Erro desconhecido",
                    exception
                )
            }
        )
    }
)

fun <T> NetworkResult<T>.toResult(): Result<T> = when (this) {
    is NetworkResult.Success -> Result.success(data)
    is NetworkResult.Error -> Result.failure(exception)
    is NetworkResult.Loading -> Result.failure(
        IllegalStateException("Não é possível converter estado de carregamento para Result")
    )
}

fun <T1, T2, R> NetworkResult<T1>.combine(
    other: NetworkResult<T2>,
    transform: (T1, T2) -> R
): NetworkResult<R> {
    return when {
        this is NetworkResult.Success && other is NetworkResult.Success -> 
            NetworkResult.Success(transform(this.data, other.data))
        this is NetworkResult.Error -> this
        other is NetworkResult.Error -> other
        else -> NetworkResult.Loading()
    }
}

inline fun <T> NetworkResult<T>.mapError(
    transform: (NetworkException) -> NetworkException
): NetworkResult<T> = when (this) {
    is NetworkResult.Success -> this
    is NetworkResult.Error -> NetworkResult.Error(transform(exception), cachedData)
    is NetworkResult.Loading -> this
}

fun <T> NetworkResult<T>.recover(
    defaultValue: T
): NetworkResult<T> = when (this) {
    is NetworkResult.Success -> this
    is NetworkResult.Error -> NetworkResult.Success(defaultValue)
    is NetworkResult.Loading -> this
}

suspend fun <T> NetworkResult<T>.recoverWith(
    fallback: suspend (NetworkException) -> NetworkResult<T>
): NetworkResult<T> = when (this) {
    is NetworkResult.Success -> this
    is NetworkResult.Error -> fallback(exception)
    is NetworkResult.Loading -> this
}

private fun extractErrorMessage(errorResponse: ApiErrorResponse?, errorBody: String?): String {
    return when {
        errorResponse?.message != null -> errorResponse.message
        errorBody != null -> {
            try {
                when {
                    errorBody.contains("\"message\":") -> {
                        val patterns = listOf("\"message\":\"", "\"message\": \"")
                        for (pattern in patterns) {
                            val messageStart = errorBody.indexOf(pattern)
                            if (messageStart != -1) {
                                val start = messageStart + pattern.length
                                val end = errorBody.indexOf("\"", start)
                                if (end != -1) {
                                    return errorBody.substring(start, end)
                                }
                            }
                        }
                        errorBody
                    }
                    errorBody.contains("\"error\":") -> {
                        val patterns = listOf("\"error\":\"", "\"error\": \"")
                        for (pattern in patterns) {
                            val errorStart = errorBody.indexOf(pattern)
                            if (errorStart != -1) {
                                val start = errorStart + pattern.length
                                val end = errorBody.indexOf("\"", start)
                                if (end != -1) {
                                    return errorBody.substring(start, end)
                                }
                            }
                        }
                        errorBody
                    }
                    errorBody.contains("Regra de negócio violada") -> "Email ou senha incorretos"
                    errorBody.length > 100 -> "Erro no servidor"
                    else -> errorBody
                }
            } catch (e: Exception) {
                "Erro inesperado"
            }
        }
        else -> "Erro desconhecido"
    }
}

