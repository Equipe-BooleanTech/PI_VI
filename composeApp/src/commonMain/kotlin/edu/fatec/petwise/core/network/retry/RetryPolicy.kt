package edu.fatec.petwise.core.network.retry

import edu.fatec.petwise.core.network.NetworkException
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelay: Long = 1000L,
    val maxDelay: Long = 10000L,
    val backoffMultiplier: Double = 2.0,
    val jitterFactor: Double = 0.2,
    val retryableExceptions: Set<Class<out NetworkException>> = defaultRetryableExceptions
) {
    companion object {
        val defaultRetryableExceptions = setOf(
            NetworkException.NoConnectivity::class.java,
            NetworkException.Timeout::class.java,
            NetworkException.ServerError::class.java,
            NetworkException.BadGateway::class.java,
            NetworkException.ServiceUnavailable::class.java,
            NetworkException.DnsError::class.java
        )

        val NONE = RetryPolicy(maxAttempts = 1)

        val CONSERVATIVE = RetryPolicy(
            maxAttempts = 2,
            initialDelay = 500L,
            maxDelay = 2000L
        )

        val AGGRESSIVE = RetryPolicy(
            maxAttempts = 5,
            initialDelay = 1000L,
            maxDelay = 30000L,
            backoffMultiplier = 2.0
        )

        val DEFAULT = RetryPolicy()
    }

    fun calculateDelay(attemptNumber: Int): Long {
        val exponentialDelay = (initialDelay * backoffMultiplier.pow(attemptNumber.toDouble())).toLong()
        val cappedDelay = min(exponentialDelay, maxDelay)
        
        val jitter = cappedDelay * jitterFactor * (Random.nextDouble() - 0.5) * 2
        return (cappedDelay + jitter).toLong().coerceAtLeast(0L)
    }

    fun isRetryable(exception: NetworkException, attemptNumber: Int): Boolean {
        if (attemptNumber >= maxAttempts) return false
        
        return when (exception) {
            is NetworkException.RateLimitExceeded -> true
            else -> exception.isRetryable && retryableExceptions.any { it.isInstance(exception) }
        }
    }

    fun getRateLimitDelay(exception: NetworkException.RateLimitExceeded): Long {
        return exception.retryAfter?.times(1000L) ?: calculateDelay(1)
    }
}

data class RetryContext(
    val attemptNumber: Int = 0,
    val lastException: NetworkException? = null,
    val totalDelay: Long = 0L
) {
    fun nextAttempt(exception: NetworkException, delay: Long): RetryContext {
        return copy(
            attemptNumber = attemptNumber + 1,
            lastException = exception,
            totalDelay = totalDelay + delay
        )
    }
}
