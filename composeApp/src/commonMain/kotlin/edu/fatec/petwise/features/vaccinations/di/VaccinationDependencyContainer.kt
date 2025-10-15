package edu.fatec.petwise.features.vaccinations.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.vaccinations.data.datasource.RemoteVaccinationDataSourceImpl
import edu.fatec.petwise.features.vaccinations.data.repository.VaccinationRepositoryImpl
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.vaccinations.domain.usecases.*
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.AddVaccinationViewModel
import edu.fatec.petwise.features.vaccinations.presentation.viewmodel.VaccinationsViewModel

object VaccinationDependencyContainer {

    private val remoteDataSource: RemoteVaccinationDataSourceImpl by lazy {
        RemoteVaccinationDataSourceImpl(NetworkModule.vaccinationApiService)
    }

    private val repository: VaccinationRepository by lazy {
        VaccinationRepositoryImpl(remoteDataSource)
    }

    private val getVaccinationsUseCase: GetVaccinationsUseCase by lazy {
        GetVaccinationsUseCase(repository)
    }

    private val getVaccinationByIdUseCase: GetVaccinationByIdUseCase by lazy {
        GetVaccinationByIdUseCase(repository)
    }

    private val getVaccinationsByPetIdUseCase: GetVaccinationsByPetIdUseCase by lazy {
        GetVaccinationsByPetIdUseCase(repository)
    }

    private val filterVaccinationsUseCase: FilterVaccinationsUseCase by lazy {
        FilterVaccinationsUseCase(repository)
    }

    private val getUpcomingVaccinationsUseCase: GetUpcomingVaccinationsUseCase by lazy {
        GetUpcomingVaccinationsUseCase(repository)
    }

    private val getOverdueVaccinationsUseCase: GetOverdueVaccinationsUseCase by lazy {
        GetOverdueVaccinationsUseCase(repository)
    }

    private val addVaccinationUseCase: AddVaccinationUseCase by lazy {
        AddVaccinationUseCase(repository)
    }

    private val updateVaccinationUseCase: UpdateVaccinationUseCase by lazy {
        UpdateVaccinationUseCase(repository)
    }

    private val deleteVaccinationUseCase: DeleteVaccinationUseCase by lazy {
        DeleteVaccinationUseCase(repository)
    }

    private val markVaccinationAsAppliedUseCase: MarkVaccinationAsAppliedUseCase by lazy {
        MarkVaccinationAsAppliedUseCase(repository)
    }

    private val scheduleNextDoseUseCase: ScheduleNextDoseUseCase by lazy {
        ScheduleNextDoseUseCase(repository)
    }

    private val vaccinationsViewModel: VaccinationsViewModel by lazy {
        VaccinationsViewModel(
            getVaccinationsUseCase = getVaccinationsUseCase,
            getVaccinationsByPetIdUseCase = getVaccinationsByPetIdUseCase,
            filterVaccinationsUseCase = filterVaccinationsUseCase,
            getUpcomingVaccinationsUseCase = getUpcomingVaccinationsUseCase,
            getOverdueVaccinationsUseCase = getOverdueVaccinationsUseCase,
            deleteVaccinationUseCase = deleteVaccinationUseCase,
            markVaccinationAsAppliedUseCase = markVaccinationAsAppliedUseCase,
            scheduleNextDoseUseCase = scheduleNextDoseUseCase
        )
    }

    private val addVaccinationViewModel: AddVaccinationViewModel by lazy {
        AddVaccinationViewModel(
            addVaccinationUseCase = addVaccinationUseCase
        )
    }

    fun provideVaccinationsViewModel(): VaccinationsViewModel = vaccinationsViewModel

    fun provideAddVaccinationViewModel(): AddVaccinationViewModel = addVaccinationViewModel
}
