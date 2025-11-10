package edu.fatec.petwise.features.veterinaries.domain.usecases

import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
import edu.fatec.petwise.features.veterinaries.domain.models.VeterinaryFilterOptions
import edu.fatec.petwise.features.veterinaries.domain.repository.VeterinaryRepository
import kotlinx.coroutines.flow.Flow

class GetAllVeterinariesUseCase(
    private val veterinaryRepository: VeterinaryRepository
) {
    operator fun invoke(): Flow<List<Veterinary>> {
        return veterinaryRepository.getAllVeterinaries()
    }
}

class GetVeterinaryByIdUseCase(
    private val veterinaryRepository: VeterinaryRepository
) {
    operator fun invoke(id: String): Flow<Veterinary?> {
        return veterinaryRepository.getVeterinaryById(id)
    }
}

class FilterVeterinariesUseCase(
    private val veterinaryRepository: VeterinaryRepository
) {
    operator fun invoke(options: VeterinaryFilterOptions): Flow<List<Veterinary>> {
        return veterinaryRepository.filterVeterinaries(options)
    }
}

class GetVerifiedVeterinariesUseCase(
    private val veterinaryRepository: VeterinaryRepository
) {
    operator fun invoke(): Flow<List<Veterinary>> {
        return veterinaryRepository.getVerifiedVeterinaries()
    }
}

data class VeterinaryUseCases(
    val getAllVeterinaries: GetAllVeterinariesUseCase,
    val getVeterinaryById: GetVeterinaryByIdUseCase,
    val filterVeterinaries: FilterVeterinariesUseCase,
    val getVerifiedVeterinaries: GetVerifiedVeterinariesUseCase
)