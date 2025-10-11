package edu.fatec.petwise.features.auth.data.repository

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        delay(1000)

        return if (email.contains("@") && password.length >= 6) {
            Result.success("user_id_123")
        } else {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    override suspend fun register(userData: Map<String, String>): Result<String> {
        delay(1500)

        val email = userData["email"] ?: return Result.failure(Exception("Email obrigatório"))
        val password = userData["password"] ?: return Result.failure(Exception("Senha obrigatória"))

        if (!email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Senha muito curta"))
        }

        return Result.success("user_id_${currentTimeMs()}")
    }

    override suspend fun requestPasswordReset(email: String): Result<String> {
        delay(1500)

        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Email inválido"))
        }



        return Result.success("Um link de recuperação foi enviado para $email")
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        delay(1500)

        if (token.isBlank()) {
            return Result.failure(Exception("Token inválido ou expirado"))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("A senha deve ter pelo menos 8 caracteres"))
        }



        return Result.success("Senha redefinida com sucesso!")
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)



        return Result.success(Unit)
    }
}
