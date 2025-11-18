package edu.fatec.petwise.features.vaccinations.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.vaccinations.data.datasource.RemoteVaccinationDataSourceImpl
import edu.fatec.petwise.features.vaccinations.data.repository.VaccinationRepositoryImpl
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.vaccinations.domain.usecases.AddVaccinationUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.DeleteVaccinationUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.FilterVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetVaccinationsByPetIdUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetOverdueVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetUpcomingVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.MarkVaccinationAsAppliedUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.ScheduleNextDoseUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.UpdateVaccinationUseCase
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.UpdateVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.VaccinationsViewModel
import kotlinx.coroutines.cancel

object VaccinationDependencyContainer {

    
    private var remoteDataSource: RemoteVaccinationDataSourceImpl? = null

    
    private var repository: VaccinationRepository? = null

    
    private var vaccinationsViewModel: VaccinationsViewModel? = null

    
    private var addVaccinationViewModel: AddVaccinationViewModel? = null

    
    private var updateVaccinationViewModel: UpdateVaccinationViewModel? = null

    private fun getRemoteDataSource(): RemoteVaccinationDataSourceImpl {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemoteVaccinationDataSourceImpl(NetworkModule.vaccinationApiService)
        remoteDataSource = created
        return created
    }

    private fun getRepository(): VaccinationRepository {
        val existing = repository
        if (existing != null) return existing
        val created = VaccinationRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildVaccinationsViewModel(): VaccinationsViewModel {
        val repo = getRepository()
        return VaccinationsViewModel(
            getVaccinationsUseCase = GetVaccinationsUseCase(repo),
            getVaccinationsByPetIdUseCase = GetVaccinationsByPetIdUseCase(repo),
            filterVaccinationsUseCase = FilterVaccinationsUseCase(repo),
            getUpcomingVaccinationsUseCase = GetUpcomingVaccinationsUseCase(repo),
            getOverdueVaccinationsUseCase = GetOverdueVaccinationsUseCase(repo),
            deleteVaccinationUseCase = DeleteVaccinationUseCase(repo),
            markVaccinationAsAppliedUseCase = MarkVaccinationAsAppliedUseCase(repo),
            scheduleNextDoseUseCase = ScheduleNextDoseUseCase(repo)
        )
    }

    private fun buildAddVaccinationViewModel(): AddVaccinationViewModel {
        val repo = getRepository()
        return AddVaccinationViewModel(
            addVaccinationUseCase = AddVaccinationUseCase(repo),
            getUserProfileUseCase = AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    private fun buildUpdateVaccinationViewModel(): UpdateVaccinationViewModel {
        val repo = getRepository()
        return UpdateVaccinationViewModel(
            updateVaccinationUseCase = UpdateVaccinationUseCase(repo)
        )
    }

    fun provideVaccinationsViewModel(): VaccinationsViewModel {
        val existing = vaccinationsViewModel
        if (existing != null) return existing
        val created = buildVaccinationsViewModel()
        vaccinationsViewModel = created
        return created
    }

    fun provideAddVaccinationViewModel(): AddVaccinationViewModel {
        val existing = addVaccinationViewModel
        if (existing != null) return existing
        val created = buildAddVaccinationViewModel()
        addVaccinationViewModel = created
        return created
    }

    fun provideUpdateVaccinationViewModel(): UpdateVaccinationViewModel {
        val existing = updateVaccinationViewModel
        if (existing != null) return existing
        val created = buildUpdateVaccinationViewModel()
        updateVaccinationViewModel = created
        return created
    }

    fun provideDeleteVaccinationUseCase(): DeleteVaccinationUseCase {
        return DeleteVaccinationUseCase(getRepository())
    }

    fun reset() {
        vaccinationsViewModel?.viewModelScope?.cancel()
        addVaccinationViewModel?.viewModelScope?.cancel()
        updateVaccinationViewModel?.viewModelScope?.cancel()

        vaccinationsViewModel = null
        addVaccinationViewModel = null
        updateVaccinationViewModel = null

        repository = null
        remoteDataSource = null
    }
}
