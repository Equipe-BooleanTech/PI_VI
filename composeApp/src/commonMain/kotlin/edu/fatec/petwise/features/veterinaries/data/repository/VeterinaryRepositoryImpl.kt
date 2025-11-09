package edu.fatec.petwise.features.veterinaries.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.veterinaries.data.datasource.VeterinaryDataSource
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
import edu.fatec.petwise.features.veterinaries.domain.models.VeterinaryFilterOptions
import edu.fatec.petwise.features.veterinaries.domain.repository.VeterinaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VeterinaryRepositoryImpl(
    private val remoteDataSource: VeterinaryDataSource
) : VeterinaryRepository {

    override fun getAllVeterinaries(): Flow<List<Veterinary>> = flow {
        try {
            println("Repositório: Buscando veterinários via API")
            when (val result = remoteDataSource.getAllVeterinaries()) {
                is NetworkResult.Success -> {
                    println("Repositório: ${result.data.size} veterinários encontrados")
                    emit(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar veterinários - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Carregando veterinários...")
                }
            }
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar veterinários - ${e.message}")
            throw e
        }
    }

    override fun getVeterinaryById(id: String): Flow<Veterinary?> = flow {
        try {
            println("Repositório: Buscando veterinário '$id' via API")
            when (val result = remoteDataSource.getVeterinaryById(id)) {
                is NetworkResult.Success -> {
                    println("Repositório: Veterinário encontrado")
                    emit(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar veterinário - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Carregando veterinário...")
                }
            }
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar veterinário - ${e.message}")
            throw e
        }
    }

    override fun searchVeterinaries(query: String): Flow<List<Veterinary>> = flow {
        try {
            println("Repositório: Pesquisando veterinários com query '$query' via API")
            when (val result = remoteDataSource.searchVeterinaries(query)) {
                is NetworkResult.Success -> {
                    println("Repositório: ${result.data.size} veterinários encontrados na pesquisa")
                    emit(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao pesquisar veterinários - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Carregando pesquisa de veterinários...")
                }
            }
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao pesquisar veterinários - ${e.message}")
            throw e
        }
    }

    override fun filterVeterinaries(options: VeterinaryFilterOptions): Flow<List<Veterinary>> = flow {
        try {
            println("Repositório: Aplicando filtros nos veterinários via API")
            val allVeterinaries = when (val result = remoteDataSource.getAllVeterinaries()) {
                is NetworkResult.Success -> result.data
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar veterinários para filtrar - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> emptyList()
            }

            val filteredVeterinaries = allVeterinaries.filter { veterinary ->
                var matches = true

                // Filter by verification status
                if (options.verified != null) {
                    matches = matches && (veterinary.verified == options.verified)
                }

                // Filter by search query
                if (options.searchQuery.isNotBlank()) {
                    val query = options.searchQuery.lowercase()
                    matches = matches && (
                        veterinary.fullName.lowercase().contains(query) ||
                        veterinary.email.lowercase().contains(query)
                    )
                }

                matches
            }

            println("Repositório: Filtros aplicados - ${filteredVeterinaries.size} veterinários encontrados")
            emit(filteredVeterinaries)
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao filtrar veterinários - ${e.message}")
            throw e
        }
    }

    override fun getVerifiedVeterinaries(): Flow<List<Veterinary>> = flow {
        try {
            println("Repositório: Buscando veterinários verificados via API")
            val allVeterinaries = when (val result = remoteDataSource.getAllVeterinaries()) {
                is NetworkResult.Success -> result.data
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar veterinários verificados - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> emptyList()
            }

            val verifiedVeterinaries = allVeterinaries.filter { it.verified }
            println("Repositório: ${verifiedVeterinaries.size} veterinários verificados encontrados")
            emit(verifiedVeterinaries)
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar veterinários verificados - ${e.message}")
            throw e
        }
    }
}