package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class RequestPasswordResetUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String): Result<String> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Email inválido"))
        }

        return authRepository.requestPasswordReset(email)
    }
}

