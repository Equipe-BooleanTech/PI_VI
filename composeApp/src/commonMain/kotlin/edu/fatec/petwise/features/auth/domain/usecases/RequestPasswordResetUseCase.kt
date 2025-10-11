package edu.fatec.petwise.features.auth.domain.usecases

import kotlinx.coroutines.delay

class RequestPasswordResetUseCase {

    suspend fun execute(email: String): Result<String> {

        delay(1500)

        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Email inválido"))
        }



        return Result.success("Um link de recuperação foi enviado para $email")
    }
}
