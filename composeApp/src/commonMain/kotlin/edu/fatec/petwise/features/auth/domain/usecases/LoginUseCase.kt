package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Result<String> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (!email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Senha não pode estar vazia"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
        }

        return authRepository.login(email, password)
    }
}
