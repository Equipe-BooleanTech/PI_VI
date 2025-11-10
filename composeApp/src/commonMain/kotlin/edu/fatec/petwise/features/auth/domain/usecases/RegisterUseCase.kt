package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest): Result<String> {
        if (registerRequest.email.isBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (!registerRequest.email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        if (registerRequest.password.isBlank()) {
            return Result.failure(Exception("Senha não pode estar vazia"))
        }

        if (registerRequest.password.length < 8) {
            return Result.failure(Exception("Senha deve ter pelo menos 8 caracteres"))
        }

        if (registerRequest.fullName.isBlank()) {
            return Result.failure(Exception("Nome não pode estar vazio"))
        }

        if (registerRequest.userType.isBlank()) {
            return Result.failure(Exception("Tipo de usuário é obrigatório"))
        }

        when (registerRequest.userType) {
            "OWNER" -> {
                if (registerRequest.cpf.isNullOrBlank()) {
                    return Result.failure(Exception("CPF é obrigatório para clientes"))
                }
            }
            "VETERINARY" -> {
                if (registerRequest.crmv.isNullOrBlank()) {
                    return Result.failure(Exception("CRMV é obrigatório para veterinários"))
                }
            }
            "PHARMACY" -> {
                if (registerRequest.cnpj.isNullOrBlank()) {
                    return Result.failure(Exception("CNPJ é obrigatório para farmácias"))
                }
                if (registerRequest.companyName.isNullOrBlank()) {
                    return Result.failure(Exception("Nome da empresa é obrigatório para farmácias"))
                }
            }
        }

        if (registerRequest.phone?.isBlank() != false) {
            return Result.failure(Exception("Telefone é obrigatório"))
        }

        return authRepository.register(registerRequest)
    }

}
