package edu.fatec.petwise.features.labs.domain.usecases

import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow

class GetLabResultsUseCase(
    private val repository: LabRepository
) {
    operator fun invoke(): Flow<List<LabResult>> = repository.getAllLabResults()

    fun searchLabResults(query: String): Flow<List<LabResult>> = repository.searchLabResults(query)
}

class GetLabResultByIdUseCase(
    private val repository: LabRepository
) {
    operator fun invoke(id: String): Flow<LabResult?> = repository.getLabResultById(id)
}

class AddLabResultUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(labResult: LabResult): Result<LabResult> {
        return if (validateLabResult(labResult)) {
            repository.addLabResult(labResult)
        } else {
            Result.failure(IllegalArgumentException("Lab result data is invalid"))
        }
    }

    private fun validateLabResult(labResult: LabResult): Boolean {
        return labResult.labType.isNotBlank() &&
               labResult.labDate.isNotBlank() &&
               labResult.status.isNotBlank()
    }
}

class UpdateLabResultUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(labResult: LabResult): Result<LabResult> = repository.updateLabResult(labResult)
}

class DeleteLabResultUseCase(
    private val repository: LabRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteLabResult(id)
}
