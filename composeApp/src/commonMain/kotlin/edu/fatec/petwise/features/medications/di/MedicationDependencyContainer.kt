package edu.fatec.petwise.features.medications.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.medications.data.datasource.MedicationDataSource
import edu.fatec.petwise.features.medications.data.datasource.RemoteMedicationDataSourceImpl
import edu.fatec.petwise.features.medications.data.repository.MedicationRepositoryImpl
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import edu.fatec.petwise.features.medications.domain.usecases.*
import edu.fatec.petwise.features.medications.presentation.viewmodel.AddMedicationViewModel
import edu.fatec.petwise.features.medications.presentation.viewmodel.MedicationsViewModel
import edu.fatec.petwise.features.medications.presentation.viewmodel.UpdateMedicationViewModel
import kotlinx.coroutines.cancel

object MedicationDependencyContainer {

    private var remoteDataSource: MedicationDataSource? = null

    private var repository: MedicationRepository? = null

    private var medicationsViewModel: MedicationsViewModel? = null

    private var addMedicationViewModel: AddMedicationViewModel? = null

    private var updateMedicationViewModel: UpdateMedicationViewModel? = null

    fun getRemoteDataSource(): MedicationDataSource {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemoteMedicationDataSourceImpl(
            NetworkModule.medicationApiService
        )
        remoteDataSource = created
        return created
    }

    fun getRepository(): MedicationRepository {
        val existing = repository
        if (existing != null) return existing
        val created = MedicationRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildMedicationUseCases(): MedicationUseCases {
        val repo = getRepository()
        return MedicationUseCases(
            getMedications = GetMedicationsUseCase(repo),
            getMedicationById = GetMedicationByIdUseCase(repo),
            addMedication = AddMedicationUseCase(repo),
            updateMedication = UpdateMedicationUseCase(repo),
            deleteMedication = DeleteMedicationUseCase(repo),
            markAsCompleted = MarkMedicationAsCompletedUseCase(repo),
            pauseMedication = PauseMedicationUseCase(repo),
            resumeMedication = ResumeMedicationUseCase(repo)
        )
    }

    private fun buildMedicationsViewModel(): MedicationsViewModel {
        val useCases = buildMedicationUseCases()
        return MedicationsViewModel(
            medicationUseCases = useCases
        )
    }

    private fun buildAddMedicationViewModel(): AddMedicationViewModel {
        val useCases = buildMedicationUseCases()
        return AddMedicationViewModel(
            addMedicationUseCase = useCases.addMedication
        )
    }

    private fun buildUpdateMedicationViewModel(): UpdateMedicationViewModel {
        val useCases = buildMedicationUseCases()
        return UpdateMedicationViewModel(
            updateMedicationUseCase = useCases.updateMedication,
            getMedicationByIdUseCase = useCases.getMedicationById
        )
    }

    fun provideMedicationsViewModel(): MedicationsViewModel {
        val existing = medicationsViewModel
        if (existing != null) return existing
        val created = buildMedicationsViewModel()
        medicationsViewModel = created
        return created
    }

    fun provideAddMedicationViewModel(): AddMedicationViewModel {
        val existing = addMedicationViewModel
        if (existing != null) return existing
        val created = buildAddMedicationViewModel()
        addMedicationViewModel = created
        return created
    }

    fun provideUpdateMedicationViewModel(): UpdateMedicationViewModel {
        val existing = updateMedicationViewModel
        if (existing != null) return existing
        val created = buildUpdateMedicationViewModel()
        updateMedicationViewModel = created
        return created
    }

    fun reset() {
        medicationsViewModel?.viewModelScope?.cancel()
        addMedicationViewModel?.viewModelScope?.cancel()
        updateMedicationViewModel?.viewModelScope?.cancel()

        medicationsViewModel = null
        addMedicationViewModel = null
        updateMedicationViewModel = null

        repository = null
        remoteDataSource = null
    }
}