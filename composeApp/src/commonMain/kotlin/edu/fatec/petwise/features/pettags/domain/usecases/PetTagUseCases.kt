package edu.fatec.petwise.features.pettags.domain.usecases

import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult
import edu.fatec.petwise.features.pettags.domain.repository.PetTagRepository

/**
 * Use case to start pairing mode for associating a tag with a pet
 */
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

/**
 * Use case to perform a check-in when a tag is scanned
 */
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

/**
 * Use case to get pet information by tag UID
 */
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

/**
 * Use case to get the last tag read from the reader
 */
class GetLastReadUseCase(
    private val repository: PetTagRepository
) {
    suspend operator fun invoke(): Result<TagReadResult> {
        return repository.getLastRead()
    }
}
