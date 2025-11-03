package edu.fatec.petwise.features.dashboard.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import kotlinx.coroutines.flow.first

class GetCardsStatisticsUseCase(
    private val petRepository: PetRepository,
    private val consultaRepository: RemoteConsultaDataSourceImpl,
    private val vacinaRepository: VaccinationRepository,
) {
    suspend operator fun invoke(): List<Int> {
        val petCount = petRepository.getAllPets().first().size
        val consultaCount = consultaRepository.getAllConsultas().size
        val vacinaCount = vacinaRepository.getAllVaccinations().first().size
        return listOf(petCount, consultaCount, vacinaCount)
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