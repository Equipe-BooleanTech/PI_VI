package edu.fatec.petwise.features.auth.domain.usecases

import kotlinx.coroutines.delay

class ResetPasswordUseCase {

    suspend fun execute(token: String, newPassword: String): Result<String> {
        delay(1500)

        if (token.isBlank()) {
            return Result.failure(Exception("Token de recuperação inválido"))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("A senha deve ter pelo menos 8 caracteres"))
        }

        if (!newPassword.any { it.isDigit() } || !newPassword.any { it.isLetter() }) {
            return Result.failure(Exception("A senha deve conter letras e números"))
        }



        return Result.success("Senha redefinida com sucesso!")
    }
}
