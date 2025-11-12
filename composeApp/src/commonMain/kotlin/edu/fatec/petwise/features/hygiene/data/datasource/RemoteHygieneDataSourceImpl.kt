package edu.fatec.petwise.features.hygiene.data.datasource

import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct

class RemoteHygieneDataSourceImpl : RemoteHygieneDataSource {

    override suspend fun getAllHygieneProducts(): List<HygieneProduct> {
        println("API: Buscando todos os produtos de higiene")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getHygieneProductById(id: String): HygieneProduct? {
        println("API: Buscando produto de higiene por ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createHygieneProduct(product: HygieneProduct): HygieneProduct {
        println("API: Criando novo produto de higiene - ${product.name}")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updateHygieneProduct(product: HygieneProduct): HygieneProduct {
        println("API: Atualizando produto de higiene - ${product.name} (ID: ${product.id})")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deleteHygieneProduct(id: String) {
        println("API: Excluindo produto de higiene com ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchHygieneProducts(query: String): List<HygieneProduct> {
        println("API: Buscando produtos de higiene com query: '$query'")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getHygieneProductsByCategory(category: String): List<HygieneProduct> {
        println("API: Buscando produtos de higiene da categoria: $category")
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
