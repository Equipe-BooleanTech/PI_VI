package edu.fatec.petwise.features.toys.data.datasource

import edu.fatec.petwise.features.toys.domain.models.Toy

class RemoteToyDataSourceImpl : RemoteToyDataSource {

    override suspend fun getAllToys(): List<Toy> {
        println("API: Buscando todos os brinquedos")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getToyById(id: String): Toy? {
        println("API: Buscando brinquedo por ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createToy(toy: Toy): Toy {
        println("API: Criando novo brinquedo - ${toy.name}")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updateToy(toy: Toy): Toy {
        println("API: Atualizando brinquedo - ${toy.name} (ID: ${toy.id})")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deleteToy(id: String) {
        println("API: Excluindo brinquedo com ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchToys(query: String): List<Toy> {
        println("API: Buscando brinquedos com query: '$query'")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getToysByCategory(category: String): List<Toy> {
        println("API: Buscando brinquedos da categoria: $category")
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
