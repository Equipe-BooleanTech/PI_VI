package edu.fatec.petwise.features.dashboard.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSourceImpl
import edu.fatec.petwise.features.auth.data.repository.AuthRepositoryImpl
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.pets.data.datasource.RemotePetDataSourceImpl
import edu.fatec.petwise.features.pets.data.repository.PetRepositoryImpl
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.vaccinations.data.datasource.RemoteVaccinationDataSourceImpl
import edu.fatec.petwise.features.vaccinations.data.repository.VaccinationRepositoryImpl
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.features.dashboard.domain.usecases.GetCardsStatisticsUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUpcomingConsultasUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUserNameUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUserTypeUseCase
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardViewModel
import edu.fatec.petwise.features.medications.data.datasource.MedicationDataSource
import edu.fatec.petwise.features.medications.data.datasource.RemoteMedicationDataSourceImpl
import edu.fatec.petwise.features.medications.data.repository.MedicationRepositoryImpl
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository

import kotlinx.coroutines.cancel

object DashboardDepedencyContainer {

    private var petRemoteDataSource: RemotePetDataSourceImpl? = null

    private var consultaRemoteDataSource: RemoteConsultaDataSourceImpl? = null

    private var petRepository: PetRepository? = null

    private var medicationRemoteDataSource: MedicationDataSource? = null

    private var medicationRepository: MedicationRepository? = null

    private var vaccinationRemoteDataSource: RemoteVaccinationDataSourceImpl? = null

    private var vaccinationRepository: VaccinationRepository? = null

    private var authRemoteDataSource: RemoteAuthDataSourceImpl? = null

    private var authRepository: AuthRepository? = null

    private var dashboardViewModel: DashboardViewModel? = null


    private fun getPetRemoteDataSource(): RemotePetDataSourceImpl {
        val existing = petRemoteDataSource
        if (existing != null) return existing
        val created = RemotePetDataSourceImpl(
            NetworkModule.petApiService,
            NetworkModule.authApiService
        )
        petRemoteDataSource = created
        return created
    }

    private fun getConsultaRemoteDataSource(): RemoteConsultaDataSourceImpl {
        val existing = consultaRemoteDataSource
        if (existing != null) return existing
        val created = RemoteConsultaDataSourceImpl(NetworkModule.consultaApiService)
        consultaRemoteDataSource = created
        return created
    }

    private fun getPetRepository(): PetRepository {
        val existing = petRepository
        if (existing != null) return existing
        val created = PetRepositoryImpl(getPetRemoteDataSource())
        petRepository = created
        return created
    }

    private fun getVaccinationRemoteDataSource(): RemoteVaccinationDataSourceImpl {
        val existing = vaccinationRemoteDataSource
        if (existing != null) return existing
        val created = RemoteVaccinationDataSourceImpl(NetworkModule.vaccinationApiService)
        vaccinationRemoteDataSource = created
        return created
    }

    private fun getVaccinationRepository(): VaccinationRepository {
        val existing = vaccinationRepository
        if (existing != null) return existing
        val created = VaccinationRepositoryImpl(getVaccinationRemoteDataSource())
        vaccinationRepository = created
        return created
    }

    private fun getMedicationRemoteDataSource(): MedicationDataSource {
        val existing = medicationRemoteDataSource
        if (existing != null) return existing
        val created = RemoteMedicationDataSourceImpl(NetworkModule.medicationApiService)
        medicationRemoteDataSource = created
        return created
    }

    private fun getMedicationRepository(): MedicationRepository {
        val existing = medicationRepository
        if (existing != null) return existing
        val created = MedicationRepositoryImpl(getMedicationRemoteDataSource())
        medicationRepository = created
        return created
    }

    private fun getAuthRemoteDataSource(): RemoteAuthDataSourceImpl {
        val existing = authRemoteDataSource
        if (existing != null) return existing
        val created = RemoteAuthDataSourceImpl(NetworkModule.authApiService)
        authRemoteDataSource = created
        return created
    }

    private fun getAuthRepository(): AuthRepository {
        val existing = authRepository
        if (existing != null) return existing
        val created = AuthRepositoryImpl(
            remoteDataSource = getAuthRemoteDataSource(),
            tokenStorage = AuthDependencyContainer.getTokenStorage()
        )
        authRepository = created
        return created
    }

    private fun buildDashboardViewModel(): DashboardViewModel {
        return DashboardViewModel(
            GetCardsStatisticsUseCase(
                getPetRepository(),
                getConsultaRemoteDataSource(),
                getVaccinationRepository(),
                getMedicationRepository()
            ),
            GetUpcomingConsultasUseCase(getConsultaRemoteDataSource()),
            GetUserNameUseCase(getAuthRepository()),
            GetUserTypeUseCase(getAuthRepository())
        )
    }

    fun provideDashboardViewModel(): DashboardViewModel {
        val existing = dashboardViewModel
        if (existing != null) return existing
        val created = buildDashboardViewModel()
        dashboardViewModel = created
        return created
    }

    fun reset() {
        dashboardViewModel?.viewModelScope?.cancel()
        dashboardViewModel = null

        petRemoteDataSource = null
        consultaRemoteDataSource = null
        petRepository = null
        medicationRemoteDataSource = null
        medicationRepository = null
        vaccinationRemoteDataSource = null
        vaccinationRepository = null
        authRemoteDataSource = null
        authRepository = null
    }
}
