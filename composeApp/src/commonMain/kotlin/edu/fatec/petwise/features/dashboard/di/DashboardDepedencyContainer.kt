package edu.fatec.petwise.features.dashboard.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSourceImpl
import edu.fatec.petwise.features.auth.data.repository.AuthRepositoryImpl
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.consultas.domain.usecases.UpdateConsultaStatusUseCase
import edu.fatec.petwise.features.consultas.data.repository.ConsultaRepositoryImpl
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
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
import edu.fatec.petwise.features.dashboard.domain.usecases.GetPrescriptionsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetExamsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetLabsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetFoodCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetHygieneCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetToysCountUseCase
import edu.fatec.petwise.features.dashboard.presentation.viewmodel.DashboardViewModel
import edu.fatec.petwise.features.medications.data.datasource.MedicationDataSource
import edu.fatec.petwise.features.medications.data.datasource.RemoteMedicationDataSourceImpl
import edu.fatec.petwise.features.medications.data.repository.MedicationRepositoryImpl
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import edu.fatec.petwise.features.prescriptions.data.repository.PrescriptionRepositoryImpl
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import edu.fatec.petwise.features.exams.data.repository.ExamRepositoryImpl
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import edu.fatec.petwise.features.labs.data.repository.LabRepositoryImpl
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import edu.fatec.petwise.features.food.data.repository.FoodRepositoryImpl
import edu.fatec.petwise.features.food.domain.repository.FoodRepository
import edu.fatec.petwise.features.hygiene.data.repository.HygieneRepositoryImpl
import edu.fatec.petwise.features.hygiene.domain.repository.HygieneRepository
import edu.fatec.petwise.features.toys.data.repository.ToyRepositoryImpl
import edu.fatec.petwise.features.toys.domain.repository.ToyRepository
import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSourceImpl
import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSourceImpl
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSourceImpl
import edu.fatec.petwise.features.food.data.datasource.RemoteFoodDataSourceImpl
import edu.fatec.petwise.features.hygiene.data.datasource.RemoteHygieneDataSourceImpl
import edu.fatec.petwise.features.toys.data.datasource.RemoteToyDataSourceImpl
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl

import kotlinx.coroutines.cancel

object DashboardDepedencyContainer {

    private var petRemoteDataSource: RemotePetDataSourceImpl? = null

    private var consultaRemoteDataSource: RemoteConsultaDataSourceImpl? = null

    private var consultaRepository: ConsultaRepository? = null

    private var petRepository: PetRepository? = null

    private var medicationRemoteDataSource: MedicationDataSource? = null

    private var medicationRepository: MedicationRepository? = null

    private var vaccinationRemoteDataSource: RemoteVaccinationDataSourceImpl? = null

    private var vaccinationRepository: VaccinationRepository? = null

    private var authRemoteDataSource: RemoteAuthDataSourceImpl? = null

    private var authRepository: AuthRepository? = null

    private var prescriptionRepository: PrescriptionRepository? = null

    private var examRepository: ExamRepository? = null

    private var labRepository: LabRepository? = null

    private var foodRepository: FoodRepository? = null

    private var hygieneRepository: HygieneRepository? = null

    private var toyRepository: ToyRepository? = null

    private var prescriptionDataSource: RemotePrescriptionDataSourceImpl? = null

    private var examDataSource: RemoteExamDataSourceImpl? = null

    private var labDataSource: RemoteLabDataSourceImpl? = null

    private var foodDataSource: RemoteFoodDataSourceImpl? = null

    private var hygieneDataSource: RemoteHygieneDataSourceImpl? = null

    private var toyDataSource: RemoteToyDataSourceImpl? = null

    private var dashboardViewModel: DashboardViewModel? = null


    private fun getPetRemoteDataSource(): RemotePetDataSourceImpl {
        val existing = petRemoteDataSource
        if (existing != null) return existing
        val created = RemotePetDataSourceImpl(
            NetworkModule.petApiService,
            AuthDependencyContainer.provideGetUserProfileUseCase()
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

    private fun getConsultaRepository(): ConsultaRepository {
        val existing = consultaRepository
        if (existing != null) return existing
        val created = ConsultaRepositoryImpl(getConsultaRemoteDataSource())
        consultaRepository = created
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

    private fun getPrescriptionRepository(): PrescriptionRepository {
        val existing = prescriptionRepository
        if (existing != null) return existing
        val created = PrescriptionRepositoryImpl(getPrescriptionDataSource())
        prescriptionRepository = created
        return created
    }

    private fun getExamRepository(): ExamRepository {
        val existing = examRepository
        if (existing != null) return existing
        val created = ExamRepositoryImpl(getExamDataSource())
        examRepository = created
        return created
    }

    private fun getLabRepository(): LabRepository {
        val existing = labRepository
        if (existing != null) return existing
        val created = LabRepositoryImpl(getLabDataSource())
        labRepository = created
        return created
    }

    private fun getFoodRepository(): FoodRepository {
        val existing = foodRepository
        if (existing != null) return existing
        val created = FoodRepositoryImpl(getFoodDataSource())
        foodRepository = created
        return created
    }

    private fun getHygieneRepository(): HygieneRepository {
        val existing = hygieneRepository
        if (existing != null) return existing
        val created = HygieneRepositoryImpl(getHygieneDataSource())
        hygieneRepository = created
        return created
    }

    private fun getToyRepository(): ToyRepository {
        val existing = toyRepository
        if (existing != null) return existing
        val created = ToyRepositoryImpl(getToyDataSource())
        toyRepository = created
        return created
    }

    private fun getPrescriptionDataSource(): RemotePrescriptionDataSourceImpl {
        val existing = prescriptionDataSource
        if (existing != null) return existing
        val created = RemotePrescriptionDataSourceImpl(
            NetworkModule.prescriptionApiService,
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
        prescriptionDataSource = created
        return created
    }

    private fun getExamDataSource(): RemoteExamDataSourceImpl {
        val existing = examDataSource
        if (existing != null) return existing
        val created = RemoteExamDataSourceImpl(
            NetworkModule.examApiService,
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
        examDataSource = created
        return created
    }

    private fun getLabDataSource(): RemoteLabDataSourceImpl {
        val existing = labDataSource
        if (existing != null) return existing
        val created = RemoteLabDataSourceImpl(NetworkModule.labApiService)
        labDataSource = created
        return created
    }

    private fun getFoodDataSource(): RemoteFoodDataSourceImpl {
        val existing = foodDataSource
        if (existing != null) return existing
        val created = RemoteFoodDataSourceImpl(NetworkModule.foodApiService)
        foodDataSource = created
        return created
    }

    private fun getHygieneDataSource(): RemoteHygieneDataSourceImpl {
        val existing = hygieneDataSource
        if (existing != null) return existing
        val created = RemoteHygieneDataSourceImpl(NetworkModule.hygieneApiService)
        hygieneDataSource = created
        return created
    }

    private fun getToyDataSource(): RemoteToyDataSourceImpl {
        val existing = toyDataSource
        if (existing != null) return existing
        val created = RemoteToyDataSourceImpl(NetworkModule.toyApiService)
        toyDataSource = created
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
            GetUserTypeUseCase(getAuthRepository()),
            GetPrescriptionsCountUseCase(
                getPrescriptionRepository(),
                getPetRepository(),
                AuthDependencyContainer.provideGetUserProfileUseCase()
            ),
            GetExamsCountUseCase(
                getExamRepository(),
                getPetRepository(),
                AuthDependencyContainer.provideGetUserProfileUseCase()
            ),
            GetLabsCountUseCase(getLabRepository()),
            GetFoodCountUseCase(getFoodRepository()),
            GetHygieneCountUseCase(getHygieneRepository()),
            GetToysCountUseCase(getToyRepository()),
            UpdateConsultaStatusUseCase(getConsultaRepository())
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
        consultaRepository = null
        petRepository = null
        medicationRemoteDataSource = null
        medicationRepository = null
        vaccinationRemoteDataSource = null
        vaccinationRepository = null
        authRemoteDataSource = null
        authRepository = null
        prescriptionRepository = null
        examRepository = null
        labRepository = null
        foodRepository = null
        hygieneRepository = null
        toyRepository = null
        prescriptionDataSource = null
        examDataSource = null
        labDataSource = null
        foodDataSource = null
        hygieneDataSource = null
        toyDataSource = null
    }
}
