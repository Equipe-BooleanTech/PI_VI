package edu.fatec.petwise.features.labs.domain.usecases

import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow

class GetLabsUseCase(
    private val repository: LabRepository
) {
    operator fun invoke(): Flow<List<Lab>> = repository.getAllLabs()

    fun searchLabs(query: String): Flow<List<Lab>> = repository.searchLabs(query)
}

class GetLabByIdUseCase(
    private val repository: LabRepository
) {
    operator fun invoke(id: String): Flow<Lab?> = repository.getLabById(id)
}

class AddLabUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(lab: Lab): Result<Lab> {
        return if (validateLab(lab)) {
            repository.addLab(lab)
        } else {
            Result.failure(IllegalArgumentException("Lab data is invalid"))
        }
    }

    private fun validateLab(lab: Lab): Boolean {
        return lab.name.isNotBlank()
    }
}

class UpdateLabUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(lab: Lab): Result<Lab> = repository.updateLab(lab)
}

class DeleteLabUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteLab(id)
}
