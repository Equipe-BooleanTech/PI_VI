package edu.fatec.petwise.features.pets.domain.usecases

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class GetConsultasByPetUseCase(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(petId: String): List<Consulta> {
        return try {
            consultaRepository.getConsultasByPetId(petId).firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao buscar consultas do pet $petId: ${e.message}")
            emptyList()
        }
    }
}

class GetVaccinationsByPetUseCase(
    private val vaccinationRepository: VaccinationRepository
) {
    suspend operator fun invoke(petId: String): List<Vaccination> {
        return try {
            vaccinationRepository.getVaccinationsByPetId(petId).firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao buscar vacinações do pet $petId: ${e.message}")
            emptyList()
        }
    }
}

class GetMedicationsByPetUseCase(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(petId: String): List<Medication> {
        return try {
            medicationRepository.getMedicationsByPetId(petId).firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao buscar medicações do pet $petId: ${e.message}")
            emptyList()
        }
    }
}

class GetExamsByPetUseCase(
    private val examRepository: ExamRepository
) {
    suspend operator fun invoke(petId: String): List<Exam> {
        return try {
            examRepository.getExamsByPetId(petId).firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao buscar exames do pet $petId: ${e.message}")
            emptyList()
        }
    }
}

class GetPrescriptionsByPetUseCase(
    private val prescriptionRepository: PrescriptionRepository
) {
    suspend operator fun invoke(petId: String): List<Prescription> {
        return try {
            prescriptionRepository.getPrescriptionsByPetId(petId).firstOrNull() ?: emptyList<Prescription>()
        } catch (e: Exception) {
            println("Erro ao buscar prescrições do pet $petId: ${e.message}")
            emptyList<Prescription>()
        }
    }
}