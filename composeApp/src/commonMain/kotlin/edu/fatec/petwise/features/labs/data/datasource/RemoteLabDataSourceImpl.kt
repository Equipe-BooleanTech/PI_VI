package edu.fatec.petwise.features.labs.data.datasource

import edu.fatec.petwise.features.labs.domain.models.Lab

class RemoteLabDataSourceImpl : RemoteLabDataSource {

    override suspend fun getAllLabs(): List<Lab> {
        println("API: Buscando todos os resultados laboratoriais")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getLabById(id: String): Lab? {
        println("API: Buscando resultado laboratorial por ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createLab(lab: Lab): Lab {
        println("API: Criando novo resultado laboratorial - ${lab.testType}")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updateLab(lab: Lab): Lab {
        println("API: Atualizando resultado laboratorial - ${lab.testType} (ID: ${lab.id})")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deleteLab(id: String) {
        println("API: Excluindo resultado laboratorial com ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchLabs(query: String): List<Lab> {
        println("API: Buscando resultados laboratoriais com query: '$query'")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getLabsByVeterinaryId(veterinaryId: String): List<Lab> {
        println("API: Buscando resultados laboratoriais do veterinário: $veterinaryId")
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
