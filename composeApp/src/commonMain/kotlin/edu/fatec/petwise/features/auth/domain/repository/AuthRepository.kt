package edu.fatec.petwise.features.auth.domain.repository

/**
 * Repository interface for authentication operations.
 * Follows clean architecture principles by defining the contract
 * in the domain layer while implementation resides in the data layer.
 */
interface AuthRepository {
    
    /**
     * Authenticates a user with email and password
     */
    suspend fun login(email: String, password: String): Result<String>
    
    /**
     * Registers a new user
     */
    suspend fun register(userData: Map<String, String>): Result<String>
    
    /**
     * Requests a password reset for the given email
     */
    suspend fun requestPasswordReset(email: String): Result<String>
    
    /**
     * Resets password using the provided token
     */
    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    
    /**
     * Logs out the current user
     */
    suspend fun logout(): Result<Unit>
}
