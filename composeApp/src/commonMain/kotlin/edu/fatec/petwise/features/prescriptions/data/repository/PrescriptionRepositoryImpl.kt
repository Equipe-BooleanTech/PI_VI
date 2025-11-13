package edu.fatec.petwise.features.prescriptions.data.repository

import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSource
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PrescriptionRepositoryImpl(
    private val remoteDataSource: RemotePrescriptionDataSource
) : PrescriptionRepository {

    override fun getAllPrescriptions(): Flow<List<Prescription>> = flow {
        try {
            println("Repositório: Buscando todas as prescrições via API")
            val prescriptions = remoteDataSource.getAllPrescriptions()
            println("Repositório: ${prescriptions.size} prescrições carregadas com sucesso da API")
            emit(prescriptions)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar prescrições da API - ${e.message}")
                throw e
        }
    }

    override fun getPrescriptionById(id: String): Flow<Prescription?> = flow {
        try {
            println("Repositório: Buscando prescrição por ID '$id' via API")
            val prescription = remoteDataSource.getPrescriptionById(id)
            if (prescription != null) {
                println("Repositório: Prescrição '${prescription.medicationName}' encontrada com sucesso")
            } else {
                println("Repositório: Prescrição com ID '$id' não encontrada")
            }
            emit(prescription)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar prescrição por ID '$id' - ${e.message}")
                throw e
        }
    }

    override fun searchPrescriptions(query: String): Flow<List<Prescription>> = flow {
        try {
            println("Repositório: Iniciando busca de prescrições com consulta '$query'")
            val prescriptions = remoteDataSource.searchPrescriptions(query)
            println("Repositório: Busca concluída - ${prescriptions.size} prescrições encontradas")
            emit(prescriptions)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar prescrições na API - ${e.message}")
                throw e
        }
    }

    override fun getPrescriptionsByPetId(petId: String): Flow<List<Prescription>> = flow {
        try {
            println("Repositório: Buscando prescrições do pet '$petId' via API")
            val prescriptions = remoteDataSource.getPrescriptionsByPetId(petId)
            println("Repositório: ${prescriptions.size} prescrições encontradas")
            emit(prescriptions)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar prescrições do pet - ${e.message}")
                throw e
        }
    }

    override fun getPrescriptionsByVeterinaryId(veterinaryId: String): Flow<List<Prescription>> = flow {
        try {
            println("Repositório: Buscando prescrições do veterinário '$veterinaryId' via API")
            val prescriptions = remoteDataSource.getPrescriptionsByVeterinaryId(veterinaryId)
            println("Repositório: ${prescriptions.size} prescrições encontradas")
            emit(prescriptions)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar prescrições do veterinário - ${e.message}")
                throw e
        }
    }

    override suspend fun addPrescription(prescription: Prescription): Result<Prescription> {
        return try {
            println("Repositório: Adicionando nova prescrição '${prescription.medicationName}' via API")
            val createdPrescription = remoteDataSource.createPrescription(prescription)
            println("Repositório: Prescrição '${createdPrescription.medicationName}' criada com sucesso - ID: ${createdPrescription.id}")
            Result.success(createdPrescription)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar prescrição '${prescription.medicationName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updatePrescription(prescription: Prescription): Result<Prescription> {
        return try {
            println("Repositório: Atualizando prescrição '${prescription.medicationName}' (ID: ${prescription.id}) via API")
            val updatedPrescription = remoteDataSource.updatePrescription(prescription)
            println("Repositório: Prescrição '${updatedPrescription.medicationName}' atualizada com sucesso")
            Result.success(updatedPrescription)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar prescrição '${prescription.medicationName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deletePrescription(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo prescrição com ID '$id' via API")
            remoteDataSource.deletePrescription(id)
            println("Repositório: Prescrição excluída com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir prescrição com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}

