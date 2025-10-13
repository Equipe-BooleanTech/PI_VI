package edu.fatec.petwise.core.network.retry

import edu.fatec.petwise.core.network.NetworkException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class CircuitBreaker(
    private val failureThreshold: Int = 5,
    private val successThreshold: Int = 2,
    private val timeout: Long = 60_000L, 
    private val halfOpenTimeout: Long = 30_000L 
) {
    private var state: State = State.CLOSED
    private var failureCount: Int = 0
    private var successCount: Int = 0
    private var lastFailureTime: Long = 0L
    private val mutex = Mutex()

    enum class State {
        CLOSED,   
        OPEN,     
        HALF_OPEN 
    }

    suspend fun isRequestAllowed(): Boolean = mutex.withLock {
        when (state) {
            State.CLOSED -> true
            State.OPEN -> {
                if (shouldAttemptReset()) {
                    state = State.HALF_OPEN
                    successCount = 0
                    println("CircuitBreaker: Transicionando para HALF_OPEN")
                    true
                } else {
                    false
                }
            }
            State.HALF_OPEN -> true
        }
    }

    suspend fun recordSuccess() = mutex.withLock {
        when (state) {
            State.CLOSED -> {
                failureCount = 0
            }
            State.HALF_OPEN -> {
                successCount++
                if (successCount >= successThreshold) {
                    state = State.CLOSED
                    failureCount = 0
                    successCount = 0
                    println("CircuitBreaker: Transicionando para CLOSED (recuperado)")
                }
            }
            State.OPEN -> {
                state = State.HALF_OPEN
            }
        }
    }


    suspend fun recordFailure(exception: NetworkException) = mutex.withLock {
        if (!isServiceFailure(exception)) return@withLock

        lastFailureTime = Clock.System.now().toEpochMilliseconds()
        
        when (state) {
            State.CLOSED -> {
                failureCount++
                if (failureCount >= failureThreshold) {
                    state = State.OPEN
                    println("CircuitBreaker: Transicionando para OPEN (muitas falhas)")
                }
            }
            State.HALF_OPEN -> {
                state = State.OPEN
                failureCount = failureThreshold
                println("CircuitBreaker: Retornando para OPEN (falha durante teste)")
            }
            State.OPEN -> {
                lastFailureTime = Clock.System.now().toEpochMilliseconds()
            }
        }
    }

    suspend fun getState(): State = mutex.withLock { state }

    suspend fun reset() = mutex.withLock {
        state = State.CLOSED
        failureCount = 0
        successCount = 0
        lastFailureTime = 0L
        println("CircuitBreaker: Reset manual executado")
    }

    private fun shouldAttemptReset(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return (now - lastFailureTime) >= timeout
    }

    private fun isServiceFailure(exception: NetworkException): Boolean {
        return when (exception) {
            is NetworkException.ServerError,
            is NetworkException.BadGateway,
            is NetworkException.ServiceUnavailable,
            is NetworkException.Timeout,
            is NetworkException.NoConnectivity -> true
            else -> false
        }
    }

    suspend fun getMetrics(): CircuitBreakerMetrics = mutex.withLock {
        CircuitBreakerMetrics(
            state = state,
            failureCount = failureCount,
            successCount = successCount,
            lastFailureTime = lastFailureTime
        )
    }
}

data class CircuitBreakerMetrics(
    val state: CircuitBreaker.State,
    val failureCount: Int,
    val successCount: Int,
    val lastFailureTime: Long
)

class CircuitBreakerOpenException(
    message: String = "Circuit breaker está aberto - serviço temporariamente indisponível"
) : Exception(message)
