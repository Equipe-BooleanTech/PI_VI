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
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import kotlinx.coroutines.flow.first

class GetCardsStatisticsUseCase(
    private val petRepository: PetRepository,
    private val consultaRepository: RemoteConsultaDataSourceImpl,
    private val vacinaRepository: VaccinationRepository,
    private val medicamentoRepository: MedicationRepository
) {
    suspend operator fun invoke(userType: String): List<Int> {
        val petCount = when (userType) {
            "OWNER" -> petRepository.getAllPets().first().size
            "VETERINARY" -> petRepository.getAllPets().first().size
            else -> 0
        }
        val consultaCount = when (userType) {
            "OWNER" -> 0 // OWNER cannot access general appointments endpoint
            "VETERINARY" -> consultaRepository.getUpcomingConsultas().size
            else -> 0
        }
        val vacinaCount = when (userType) {
            "OWNER" -> 0 // TODO: implement upcoming vaccines for owner
            "VETERINARY" -> vacinaRepository.getAllVaccinations().first().size
            else -> 0
        }
        val medicamentoCount = when (userType) {
            "OWNER" -> 0 // TODO: implement medications for owner
            "PHARMACY" -> medicamentoRepository.getAllMedications().first().size
            else -> 0
        }
        return listOf(petCount, consultaCount, vacinaCount, medicamentoCount)
    }
}

class GetUpcomingConsultasUseCase(
    private val consultaRepository: RemoteConsultaDataSourceImpl
) {
    suspend operator fun invoke(userType: String): List<Consulta> {
        return when (userType) {
            "OWNER" -> emptyList() // OWNER cannot access general appointments endpoint
            "VETERINARY" -> consultaRepository.getUpcomingConsultas()
            else -> emptyList()
        }
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
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    suspend operator fun invoke(userType: String = "OWNER"): Int {
        return when (userType) {
            "OWNER" -> {
                try {
                    // For OWNER users, get only prescriptions for their pets
                    val userProfile = getUserProfileUseCase.execute().getOrNull()
                    if (userProfile != null) {
                        val userPets = petRepository.getAllPets().first()
                        val userPetIds = userPets.map { it.id }.toSet()
                        val allPrescriptions = prescriptionRepository.getAllPrescriptions().first()
                        allPrescriptions.count { it.petId in userPetIds }
                    } else {
                        0
                    }
                } catch (e: Exception) {
                    println("Error filtering prescriptions for OWNER: ${e.message}")
                    0
                }
            }
            "VETERINARY" -> prescriptionRepository.getAllPrescriptions().first().size
            else -> 0
        }
    }
}

class GetExamsCountUseCase(
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    suspend operator fun invoke(userType: String = "OWNER"): Int {
        return when (userType) {
            "OWNER" -> {
                try {
                    // For OWNER users, get only exams for their pets
                    val userProfile = getUserProfileUseCase.execute().getOrNull()
                    if (userProfile != null) {
                        val userPets = petRepository.getAllPets().first()
                        val userPetIds = userPets.map { it.id }.toSet()
                        val allExams = examRepository.getAllExams().first()
                        allExams.count { it.petId in userPetIds }
                    } else {
                        0
                    }
                } catch (e: Exception) {
                    println("Error filtering exams for OWNER: ${e.message}")
                    0
                }
            }
            "VETERINARY" -> examRepository.getAllExams().first().size
            else -> 0
        }
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
