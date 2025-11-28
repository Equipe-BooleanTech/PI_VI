package edu.fatec.petwise.features.exams.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSource
import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSourceImpl
import edu.fatec.petwise.features.exams.data.repository.ExamRepositoryImpl
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import edu.fatec.petwise.features.exams.domain.usecases.*
import edu.fatec.petwise.features.exams.presentation.viewmodel.AddExamViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.ExamsViewModel
import edu.fatec.petwise.features.exams.presentation.viewmodel.UpdateExamViewModel

object ExamDependencyContainer {
    
    private val remoteDataSource: RemoteExamDataSource by lazy {
        RemoteExamDataSourceImpl(
            NetworkModule.examApiService,
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    private val repository: ExamRepository by lazy {
        ExamRepositoryImpl(remoteDataSource)
    }

    val getExamsUseCase: GetExamsUseCase by lazy {
        GetExamsUseCase(repository)
    }

    val getExamByIdUseCase: GetExamByIdUseCase by lazy {
        GetExamByIdUseCase(repository)
    }

    val addExamUseCase: AddExamUseCase by lazy {
        AddExamUseCase(repository)
    }

    val updateExamUseCase: UpdateExamUseCase by lazy {
        UpdateExamUseCase(repository)
    }

    val deleteExamUseCase: DeleteExamUseCase by lazy {
        DeleteExamUseCase(repository)
    }

    val addExamViewModel: AddExamViewModel by lazy {
        AddExamViewModel(addExamUseCase, AuthDependencyContainer.provideGetUserProfileUseCase())
    }

    val examsViewModel: ExamsViewModel by lazy {
        ExamsViewModel(
            getExamsUseCase,
            deleteExamUseCase,
            PetDependencyContainer.provideGetPetsUseCase(),
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    val updateExamViewModel: UpdateExamViewModel by lazy {
        UpdateExamViewModel(updateExamUseCase, getExamByIdUseCase, AuthDependencyContainer.provideGetUserProfileUseCase())
    }

    fun provideExamRepository(): ExamRepository {
        return repository
    }
}
