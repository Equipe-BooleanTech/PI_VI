package edu.fatec.petwise.features.dashboard.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.first

class GetCardsStatisticsUseCase(
    private val petRepository: PetRepository,
    private val consultaRepository: RemoteConsultaDataSourceImpl,
    private val vacinaRepository: VaccinationRepository,
    private val medicamentoRepository: MedicationRepository
) {
    suspend operator fun invoke(): List<Int> {
        val petCount = petRepository.getAllPets().first().size
        val consultaCount = consultaRepository.getAllConsultas().size
        val vacinaCount = vacinaRepository.getAllVaccinations().first().size
        val medicamentoCount = medicamentoRepository.getAllMedications().first().size
        return listOf(petCount, consultaCount, vacinaCount, medicamentoCount)
    }
}

class GetUpcomingConsultasUseCase(
    private val consultaRepository: RemoteConsultaDataSourceImpl
) {
    suspend operator fun invoke(): List<Consulta> {
        return consultaRepository.getUpcomingConsultas()
    }
}

class GetUserNameUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val profileResult = authRepository.getUserProfile()
            profileResult.map { profile -> 
                profile.fullName.split(" ").firstOrNull() ?: profile.fullName
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetUserTypeUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val tokenStorage = edu.fatec.petwise.features.auth.di.AuthDependencyContainer.getTokenStorage()
            val userType = tokenStorage.getUserType()
            
            if (userType != null) {
                println("GetUserTypeUseCase: Retornando userType do cache: $userType")
                Result.success(userType)
            } else {
                println("GetUserTypeUseCase: UserType não encontrado no cache, buscando via API")
                val profileResult = authRepository.getUserProfile()
                profileResult.map { profile -> 
                    tokenStorage.saveUserType(profile.userType)
                    profile.userType
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
