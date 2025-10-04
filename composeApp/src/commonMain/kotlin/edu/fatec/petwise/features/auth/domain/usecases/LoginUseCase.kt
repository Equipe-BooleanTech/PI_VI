package edu.fatec.petwise.features.auth.domain.usecases

import kotlinx.coroutines.delay


class LoginUseCase {
    suspend fun execute(email: String, password: String): Result<String> {

        delay(1000)
        

        return if (email.contains("@") && password.length >= 6) {
            Result.success("user_id_123")
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }
}