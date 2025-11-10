package edu.fatec.petwise.features.pharmacies.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.pharmacies.data.datasource.PharmacyDataSource
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy
import edu.fatec.petwise.features.pharmacies.domain.models.PharmacyFilterOptions
import edu.fatec.petwise.features.pharmacies.domain.repository.PharmacyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PharmacyRepositoryImpl(
    private val remoteDataSource: PharmacyDataSource
) : PharmacyRepository {

    override fun getAllPharmacies(): Flow<List<Pharmacy>> = flow {
        try {
            println("Repositório: Buscando farmácias via API")
            when (val result = remoteDataSource.getAllPharmacies()) {
                is NetworkResult.Success -> {
                    println("Repositório: ${result.data.size} farmácias encontradas")
                    emit(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar farmácias - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Carregando farmácias...")
                }
            }
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar farmácias - ${e.message}")
            throw e
        }
    }

    override fun getPharmacyById(id: String): Flow<Pharmacy?> = flow {
        try {
            println("Repositório: Buscando farmácia '$id' via API")
            when (val result = remoteDataSource.getPharmacyById(id)) {
                is NetworkResult.Success -> {
                    println("Repositório: Farmácia encontrada")
                    emit(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar farmácia - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Carregando farmácia...")
                }
            }
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar farmácia - ${e.message}")
            throw e
        }
    }

    override fun filterPharmacies(options: PharmacyFilterOptions): Flow<List<Pharmacy>> = flow {
        try {
            println("Repositório: Aplicando filtros nas farmácias")
            val allPharmacies = when (val result = remoteDataSource.getAllPharmacies()) {
                is NetworkResult.Success -> result.data
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar farmácias para filtrar - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> emptyList()
            }

            val filteredPharmacies = allPharmacies.filter { pharmacy ->
                var matches = true

                // Filter by verification status
                if (options.verified != null) {
                    matches = matches && (pharmacy.verified == options.verified)
                }

                // Filter by search query (local search)
                if (options.searchQuery.isNotBlank()) {
                    val query = options.searchQuery.lowercase()
                    matches = matches && (
                        pharmacy.fullName.lowercase().contains(query) ||
                        pharmacy.email.lowercase().contains(query) ||
                        pharmacy.phone?.lowercase()?.contains(query) == true
                    )
                }

                matches
            }

            println("Repositório: Filtros aplicados - ${filteredPharmacies.size} farmácias encontradas")
            emit(filteredPharmacies)
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao filtrar farmácias - ${e.message}")
            throw e
        }
    }

    override fun getVerifiedPharmacies(): Flow<List<Pharmacy>> = flow {
        try {
            println("Repositório: Buscando farmácias verificadas via API")
            val allPharmacies = when (val result = remoteDataSource.getAllPharmacies()) {
                is NetworkResult.Success -> result.data
                is NetworkResult.Error -> {
                    println("Repositório: Erro ao buscar farmácias verificadas - ${result.exception.message}")
                    throw result.exception
                }
                is NetworkResult.Loading -> emptyList()
            }

            val verifiedPharmacies = allPharmacies.filter { it.verified }
            println("Repositório: ${verifiedPharmacies.size} farmácias verificadas encontradas")
            emit(verifiedPharmacies)
        } catch (e: Exception) {
            println("Repositório: Erro inesperado ao buscar farmácias verificadas - ${e.message}")
            throw e
        }
    }
}
