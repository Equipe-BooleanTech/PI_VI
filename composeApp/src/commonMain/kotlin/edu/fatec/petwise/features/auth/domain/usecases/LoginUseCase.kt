package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(loginRequest: edu.fatec.petwise.core.network.dto.LoginRequest): Result<String> {
        if (loginRequest.email.isBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (!loginRequest.email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        if (loginRequest.password.isBlank()) {
            return Result.failure(Exception("Senha não pode estar vazia"))
        }

        if (loginRequest.password.length < 6) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
        }

        return authRepository.login(loginRequest.email, loginRequest.password)
    }

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
