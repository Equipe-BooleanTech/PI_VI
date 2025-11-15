package edu.fatec.petwise.features.dashboard.domain.usecases

import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import edu.fatec.petwise.features.food.domain.repository.FoodRepository
import edu.fatec.petwise.features.hygiene.domain.repository.HygieneRepository
import edu.fatec.petwise.features.toys.domain.repository.ToyRepository
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
            println("GetUserTypeUseCase: Buscando userType via API (sempre fresh)")
            val profileResult = authRepository.getUserProfile()
            profileResult.map { profile ->
                val tokenStorage = edu.fatec.petwise.features.auth.di.AuthDependencyContainer.getTokenStorage()
                tokenStorage.saveUserType(profile.userType)
                println("GetUserTypeUseCase: UserType obtido da API: ${profile.userType}")
                profile.userType
            }
        } catch (e: Exception) {
            println("GetUserTypeUseCase: Erro ao buscar userType: ${e.message}")
            Result.failure(e)
        }
    }
}

class GetPrescriptionsCountUseCase(
    private val prescriptionRepository: PrescriptionRepository
) {
    suspend operator fun invoke(): Int {
        return prescriptionRepository.getAllPrescriptions().first().size
    }
}

class GetExamsCountUseCase(
    private val examRepository: ExamRepository
) {
    suspend operator fun invoke(): Int {
        return examRepository.getAllExams().first().size
    }
}

class GetLabsCountUseCase(
    private val labRepository: LabRepository
) {
    suspend operator fun invoke(): Int {
        return labRepository.getAllLabs().first().size
    }
}

class GetFoodCountUseCase(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(): Int {
        return foodRepository.getAllFood().first().size
    }
}

class GetHygieneCountUseCase(
    private val hygieneRepository: HygieneRepository
) {
    suspend operator fun invoke(): Int {
        return hygieneRepository.getAllHygieneProducts().first().size
    }
}

class GetToysCountUseCase(
    private val toyRepository: ToyRepository
) {
    suspend operator fun invoke(): Int {
        return toyRepository.getAllToys().first().size
    }
}
