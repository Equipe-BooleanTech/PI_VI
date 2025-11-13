package edu.fatec.petwise.features.exams.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSource
import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSourceImpl
import edu.fatec.petwise.features.exams.data.repository.ExamRepositoryImpl
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import edu.fatec.petwise.features.exams.domain.usecases.*

object ExamDependencyContainer {
    
    private val remoteDataSource: RemoteExamDataSource by lazy {
        RemoteExamDataSourceImpl(NetworkModule.examApiService)
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
}
