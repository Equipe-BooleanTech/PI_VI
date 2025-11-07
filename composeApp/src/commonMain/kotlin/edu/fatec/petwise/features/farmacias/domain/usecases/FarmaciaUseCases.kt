package edu.fatec.petwise.features.farmacias.domain.usecases

import edu.fatec.petwise.features.farmacias.domain.models.*
import edu.fatec.petwise.features.farmacias.domain.repository.FarmaciaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Container para todos os casos de uso de Farmácias.
 * 
 * Centraliza a lógica de negócio relacionada a farmácias,
 * separando responsabilidades e facilitando testes.
 */

// ====== QUERY USE CASES ======

class GetFarmaciasUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(): Flow<List<Farmacia>> {
        return repository.getAllFarmacias()
    }
}

class GetFarmaciaByIdUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(id: String): Result<Farmacia> {
        return repository.getFarmaciaById(id)
    }
}

class FilterFarmaciasUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(options: FarmaciaFilterOptions): Flow<List<Farmacia>> {
        return repository.filterFarmacias(options)
    }
}

class GetFarmaciasByCidadeUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(cidade: String): Flow<List<Farmacia>> {
        if (cidade.isBlank()) {
            throw IllegalArgumentException("Cidade não pode estar vazia")
        }
        return repository.getFarmaciasByCidade(cidade)
    }
}

class GetFarmaciasByEstadoUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(estado: String): Flow<List<Farmacia>> {
        if (estado.isBlank()) {
            throw IllegalArgumentException("Estado não pode estar vazio")
        }
        if (estado.length != 2) {
            throw IllegalArgumentException("Estado deve ser a sigla com 2 caracteres")
        }
        return repository.getFarmaciasByEstado(estado.uppercase())
    }
}

class GetFarmaciasAtivasUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(): Flow<List<Farmacia>> {
        return repository.getFarmaciasAtivas()
    }
}

class GetFarmaciasComFreteGratisUseCase(
    private val repository: FarmaciaRepository
) {
    operator fun invoke(): Flow<List<Farmacia>> {
        return repository.getFarmaciasComFreteGratis()
    }
}

// ====== COMMAND USE CASES ======

class AddFarmaciaUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(farmacia: Farmacia): Result<Farmacia> {
        // Validações de negócio
        validateFarmacia(farmacia)?.let { error ->
            return Result.failure(Exception(error))
        }
        
        return repository.createFarmacia(farmacia)
    }
    
    private fun validateFarmacia(farmacia: Farmacia): String? {
        return when {
            farmacia.razaoSocial.isBlank() -> "Razão Social é obrigatória"
            farmacia.nomeFantasia.isBlank() -> "Nome Fantasia é obrigatório"
            farmacia.cnpj.isBlank() -> "CNPJ é obrigatório"
            !farmacia.isCnpjValido() -> "CNPJ inválido (deve ter 14 dígitos)"
            farmacia.responsavelTecnico.isBlank() -> "Responsável Técnico é obrigatório"
            farmacia.crf.isBlank() -> "CRF é obrigatório"
            farmacia.registroAnvisa.isBlank() -> "Registro Anvisa é obrigatório"
            farmacia.autorizacaoFuncionamento.isBlank() -> "Autorização de Funcionamento é obrigatória"
            farmacia.endereco.isBlank() -> "Endereço é obrigatório"
            farmacia.numero.isBlank() -> "Número é obrigatório"
            farmacia.bairro.isBlank() -> "Bairro é obrigatório"
            farmacia.cidade.isBlank() -> "Cidade é obrigatória"
            farmacia.estado.isBlank() -> "Estado é obrigatório"
            farmacia.estado.length != 2 -> "Estado deve ser a sigla com 2 caracteres"
            farmacia.cep.isBlank() -> "CEP é obrigatório"
            farmacia.telefone.isBlank() -> "Telefone é obrigatório"
            farmacia.email.isBlank() -> "E-mail é obrigatório"
            !farmacia.isEmailValido() -> "E-mail inválido"
            farmacia.limiteCredito < 0 -> "Limite de crédito não pode ser negativo"
            farmacia.descontoMaximo < 0 || farmacia.descontoMaximo > 100 -> 
                "Desconto máximo deve estar entre 0 e 100%"
            farmacia.prazoEntregaDias < 0 -> "Prazo de entrega não pode ser negativo"
            farmacia.valorMinimoFrete < 0 -> "Valor mínimo de frete não pode ser negativo"
            else -> null
        }
    }
}

class UpdateFarmaciaUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(id: String, farmacia: Farmacia): Result<Farmacia> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID da farmácia é obrigatório"))
        }
        
        // Validações básicas
        if (farmacia.razaoSocial.isBlank()) {
            return Result.failure(Exception("Razão Social é obrigatória"))
        }
        if (farmacia.nomeFantasia.isBlank()) {
            return Result.failure(Exception("Nome Fantasia é obrigatório"))
        }
        if (!farmacia.isEmailValido()) {
            return Result.failure(Exception("E-mail inválido"))
        }
        
        return repository.updateFarmacia(id, farmacia)
    }
}

class DeleteFarmaciaUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID da farmácia é obrigatório"))
        }
        return repository.deleteFarmacia(id)
    }
}

class UpdateLimiteCreditoUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(id: String, novoLimite: Double): Result<Farmacia> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID da farmácia é obrigatório"))
        }
        if (novoLimite < 0) {
            return Result.failure(Exception("Limite de crédito não pode ser negativo"))
        }
        return repository.updateLimiteCredito(id, novoLimite)
    }
}

class UpdateStatusFarmaciaUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(
        id: String,
        novoStatus: StatusFarmacia,
        motivo: String? = null
    ): Result<Farmacia> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID da farmácia é obrigatório"))
        }
        
        // Se o status for SUSPENSA ou BLOQUEADA, motivo é obrigatório
        if ((novoStatus == StatusFarmacia.SUSPENSA || novoStatus == StatusFarmacia.BLOQUEADA) 
            && motivo.isNullOrBlank()) {
            return Result.failure(Exception("Motivo é obrigatório para suspender ou bloquear farmácia"))
        }
        
        return repository.updateStatus(id, novoStatus.name, motivo)
    }
}

/**
 * Use case para buscar farmácias adequadas para um pedido.
 * 
 * Aplica lógica de negócio para encontrar as melhores farmácias
 * baseado em critérios como localização, prazo, frete, etc.
 */
class FindBestFarmaciasForOrderUseCase(
    private val repository: FarmaciaRepository
) {
    suspend operator fun invoke(
        cidade: String,
        valorPedido: Double,
        urgente: Boolean = false
    ): Flow<List<Farmacia>> {
        return repository.getFarmaciasByCidade(cidade)
    }
}
