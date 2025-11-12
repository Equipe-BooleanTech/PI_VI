package edu.fatec.petwise.features.hygiene.data.repository

import edu.fatec.petwise.core.config.AppConfig
import edu.fatec.petwise.core.data.MockDataProvider
import edu.fatec.petwise.features.hygiene.data.datasource.RemoteHygieneDataSource
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct
import edu.fatec.petwise.features.hygiene.domain.repository.HygieneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HygieneRepositoryImpl(
    private val remoteDataSource: RemoteHygieneDataSource
) : HygieneRepository {

    override fun getAllHygieneProducts(): Flow<List<HygieneProduct>> = flow {
        try {
            println("Repositório: Buscando todos os produtos de higiene via API")
            val products = remoteDataSource.getAllHygieneProducts()
            println("Repositório: ${products.size} produtos carregados com sucesso da API")
            emit(products)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar produtos da API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockProducts = MockDataProvider.getMockHygieneProducts()
                    emit(mockProducts)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar produtos da API - ${e.message}")
                throw e
            }
        }
    }

    override fun getHygieneProductById(id: String): Flow<HygieneProduct?> = flow {
        try {
            println("Repositório: Buscando produto de higiene por ID '$id' via API")
            val product = remoteDataSource.getHygieneProductById(id)
            if (product != null) {
                println("Repositório: Produto '${product.name}' encontrado com sucesso")
            } else {
                println("Repositório: Produto com ID '$id' não encontrado")
            }
            emit(product)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar produto por ID '$id' - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockProduct = MockDataProvider.getMockHygieneProducts().find { it.id == id }
                    emit(mockProduct)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar produto por ID '$id' - ${e.message}")
                throw e
            }
        }
    }

    override fun searchHygieneProducts(query: String): Flow<List<HygieneProduct>> = flow {
        try {
            println("Repositório: Iniciando busca de produtos com consulta '$query'")
            val products = remoteDataSource.searchHygieneProducts(query)
            println("Repositório: Busca concluída - ${products.size} produtos encontrados")
            emit(products)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar produtos na API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockProducts = MockDataProvider.getMockHygieneProducts()
                        .filter { it.name.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true) }
                    emit(mockProducts)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar produtos na API - ${e.message}")
                throw e
            }
        }
    }

    override fun getHygieneProductsByCategory(category: String): Flow<List<HygieneProduct>> = flow {
        try {
            println("Repositório: Buscando produtos da categoria '$category' via API")
            val products = remoteDataSource.getHygieneProductsByCategory(category)
            println("Repositório: ${products.size} produtos encontrados")
            emit(products)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar produtos da categoria - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockProducts = MockDataProvider.getMockHygieneProducts()
                        .filter { it.category.equals(category, ignoreCase = true) }
                    emit(mockProducts)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar produtos da categoria - ${e.message}")
                throw e
            }
        }
    }

    override suspend fun addHygieneProduct(product: HygieneProduct): Result<HygieneProduct> {
        return try {
            println("Repositório: Adicionando novo produto '${product.name}' via API")
            val createdProduct = remoteDataSource.createHygieneProduct(product)
            println("Repositório: Produto '${createdProduct.name}' criado com sucesso - ID: ${createdProduct.id}")
            Result.success(createdProduct)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar produto '${product.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateHygieneProduct(product: HygieneProduct): Result<HygieneProduct> {
        return try {
            println("Repositório: Atualizando produto '${product.name}' (ID: ${product.id}) via API")
            val updatedProduct = remoteDataSource.updateHygieneProduct(product)
            println("Repositório: Produto '${updatedProduct.name}' atualizado com sucesso")
            Result.success(updatedProduct)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar produto '${product.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteHygieneProduct(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo produto com ID '$id' via API")
            remoteDataSource.deleteHygieneProduct(id)
            println("Repositório: Produto excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir produto com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}
