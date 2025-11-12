package edu.fatec.petwise.features.prescriptions.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription

class RemotePrescriptionDataSourceImpl : RemotePrescriptionDataSource {

    override suspend fun getAllPrescriptions(): List<Prescription> {
        println("API: Buscando todas as prescrições")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getPrescriptionById(id: String): Prescription? {
        println("API: Buscando prescrição por ID: $id")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createPrescription(prescription: Prescription): Prescription {
        println("API: Criando nova prescrição - ${prescription.medicationName}")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updatePrescription(prescription: Prescription): Prescription {
        println("API: Atualizando prescrição - ${prescription.medicationName} (ID: ${prescription.id})")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deletePrescription(id: String) {
        println("API: Excluindo prescrição com ID: $id")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchPrescriptions(query: String): List<Prescription> {
        println("API: Buscando prescrições com query: '$query'")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getPrescriptionsByPetId(petId: String): List<Prescription> {
        println("API: Buscando prescrições do pet: $petId")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getPrescriptionsByVeterinaryId(veterinaryId: String): List<Prescription> {
        println("API: Buscando prescrições do veterinário: $veterinaryId")
        // TODO: Implement API call when backend is ready
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
