package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(userData: Map<String, String>): Result<String> {
        val email = userData["email"]
        if (email.isNullOrBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (!email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        val password = userData["password"]
        if (password.isNullOrBlank()) {
            return Result.failure(Exception("Senha não pode estar vazia"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Senha deve ter pelo menos 8 caracteres"))
        }

        val fullName = userData["fullName"]
        if (fullName.isNullOrBlank()) {
            return Result.failure(Exception("Nome não pode estar vazio"))
        }

        val userType = userData["userType"]
        if (userType.isNullOrBlank()) {
            return Result.failure(Exception("Tipo de usuário é obrigatório"))
        }

        when (userType) {
            "OWNER" -> {
                val cpf = userData["cpf"]
                if (cpf.isNullOrBlank()) {
                    return Result.failure(Exception("CPF é obrigatório para clientes"))
                }
            }
            "VETERINARIAN" -> {
                val crmv = userData["crmv"]
                if (crmv.isNullOrBlank()) {
                    return Result.failure(Exception("CRMV é obrigatório para veterinários"))
                }
                val specialization = userData["specialization"]
                if (specialization.isNullOrBlank()) {
                    return Result.failure(Exception("Especialização é obrigatória para veterinários"))
                }
            }
            "PHARMACY" -> {
                val cnpj = userData["cnpj"]
                if (cnpj.isNullOrBlank()) {
                    return Result.failure(Exception("CNPJ é obrigatório para farmácias"))
                }
                val companyName = userData["companyName"]
                if (companyName.isNullOrBlank()) {
                    return Result.failure(Exception("Nome da empresa é obrigatório para farmácias"))
                }
            }
        }

        val phone = userData["phone"]
        if (phone.isNullOrBlank()) {
            return Result.failure(Exception("Telefone é obrigatório"))
        }

        println("Dados do usuário recebidos: $userData")
        return authRepository.register(userData)
    }
}
