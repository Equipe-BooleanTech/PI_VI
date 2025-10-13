package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(token: String, newPassword: String): Result<String> {
        if (token.isBlank()) {
            return Result.failure(Exception("Token de recuperação inválido"))
        }

        if (newPassword.isBlank()) {
            return Result.failure(Exception("Nova senha não pode estar vazia"))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("A senha deve ter pelo menos 8 caracteres"))
        }

        if (!newPassword.any { it.isDigit() } || !newPassword.any { it.isLetter() }) {
            return Result.failure(Exception("A senha deve conter letras e números"))
        }

        return authRepository.resetPassword(token, newPassword)
    }
}

