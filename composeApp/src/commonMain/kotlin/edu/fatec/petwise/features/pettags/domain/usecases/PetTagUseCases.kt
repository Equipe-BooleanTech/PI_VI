package edu.fatec.petwise.features.pettags.domain.usecases

import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult
import edu.fatec.petwise.features.pettags.domain.repository.PetTagRepository


class StartPairingUseCase(
    private val repository: PetTagRepository
) {
    suspend operator fun invoke(petId: String, readerId: String = "app-reader"): Result<String> {
        if (petId.isBlank()) {
            return Result.failure(IllegalArgumentException("Pet ID não pode estar vazio"))
        }
        return repository.startPairing(petId, readerId)
    }
}


class CheckInUseCase(
    private val repository: PetTagRepository
) {
    suspend operator fun invoke(tagUid: String, readerId: String = "app-reader"): Result<TagCheckInResult> {
        if (tagUid.isBlank()) {
            return Result.failure(IllegalArgumentException("Tag UID não pode estar vazio"))
        }
        return repository.checkIn(tagUid, readerId)
    }
}


class GetPetByTagUseCase(
    private val repository: PetTagRepository
) {
    suspend operator fun invoke(tagUid: String): Result<TagCheckInResult> {
        if (tagUid.isBlank()) {
            return Result.failure(IllegalArgumentException("Tag UID não pode estar vazio"))
        }
        return repository.getPetByTag(tagUid)
    }
}


class GetLastReadUseCase(
    private val repository: PetTagRepository
) {
    suspend operator fun invoke(): Result<TagReadResult> {
        return repository.getLastRead()
    }
}
