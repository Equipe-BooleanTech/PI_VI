package edu.fatec.petwise.features.food.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.food.data.datasource.RemoteFoodDataSource
import edu.fatec.petwise.features.food.data.datasource.RemoteFoodDataSourceImpl
import edu.fatec.petwise.features.food.data.repository.FoodRepositoryImpl
import edu.fatec.petwise.features.food.domain.repository.FoodRepository
import edu.fatec.petwise.features.food.domain.usecases.*
import edu.fatec.petwise.features.food.presentation.FoodViewModel

object FoodDependencyContainer {
    
    private val remoteDataSource: RemoteFoodDataSource by lazy {
        RemoteFoodDataSourceImpl(NetworkModule.foodApiService)
    }

    private val repository: FoodRepository by lazy {
        FoodRepositoryImpl(remoteDataSource)
    }

    val getFoodUseCase: GetFoodUseCase by lazy {
        GetFoodUseCase(repository)
    }

    val getFoodByIdUseCase: GetFoodByIdUseCase by lazy {
        GetFoodByIdUseCase(repository)
    }

    val addFoodUseCase: AddFoodUseCase by lazy {
        AddFoodUseCase(repository)
    }

    val updateFoodUseCase: UpdateFoodUseCase by lazy {
        UpdateFoodUseCase(repository)
    }

    val deleteFoodUseCase: DeleteFoodUseCase by lazy {
        DeleteFoodUseCase(repository)
    }

    val foodViewModel: FoodViewModel by lazy {
        FoodViewModel()
    }

    fun provideRepository(): FoodRepository = repository
}
