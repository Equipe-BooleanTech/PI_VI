package edu.fatec.petwise.features.consultas.data.datasource

import edu.fatec.petwise.features.consultas.domain.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LocalConsultaDataSourceImpl : LocalConsultaDataSource {
    private val _consultas = MutableStateFlow(generateMockConsultas())

    override fun getAllConsultas(): Flow<List<Consulta>> = _consultas

    override fun getConsultaById(id: String): Flow<Consulta?> = _consultas.map { consultas ->
        consultas.find { it.id == id }
    }

    override suspend fun insertConsulta(consulta: Consulta) {
        _consultas.value = _consultas.value + consulta
    }

    override suspend fun updateConsulta(consulta: Consulta) {
        _consultas.value = _consultas.value.map { existingConsulta ->
            if (existingConsulta.id == consulta.id) consulta else existingConsulta
        }
    }

    override suspend fun deleteConsulta(id: String) {
        _consultas.value = _consultas.value.filter { it.id != id }
    }

    private fun generateMockConsultas(): List<Consulta> {
        return listOf(
            Consulta(
                id = "1",
                petId = "1",
                petName = "Max",
                veterinarianName = "Dr. Carlos Mendes",
                consultaType = ConsultaType.ROUTINE,
                consultaDate = "2024-11-15",
                consultaTime = "14:00",
                status = ConsultaStatus.SCHEDULED,
                symptoms = "Consulta de rotina anual",
                diagnosis = "",
                treatment = "",
                prescriptions = "",
                notes = "Vacinas em dia. Agendar retorno em 6 meses.",
                nextAppointment = "2025-05-15",
                price = 150.0f,
                isPaid = false,
                ownerName = "João Silva",
                ownerPhone = "(11) 99999-1234",
                createdAt = "2024-10-08T10:00:00Z",
                updatedAt = "2024-10-08T10:00:00Z"
            ),
            Consulta(
                id = "2",
                petId = "2",
                petName = "Luna",
                veterinarianName = "Dra. Ana Paula",
                consultaType = ConsultaType.VACCINATION,
                consultaDate = "2024-11-12",
                consultaTime = "10:30",
                status = ConsultaStatus.COMPLETED,
                symptoms = "Aplicação de vacina antirrábica",
                diagnosis = "Saudável",
                treatment = "Vacina antirrábica aplicada",
                prescriptions = "",
                notes = "Próxima dose em 1 ano",
                nextAppointment = "2025-11-12",
                price = 80.0f,
                isPaid = true,
                ownerName = "Maria Santos",
                ownerPhone = "(11) 99999-5678",
                createdAt = "2024-09-15T14:30:00Z",
                updatedAt = "2024-11-12T10:45:00Z"
            ),
            Consulta(
                id = "3",
                petId = "3",
                petName = "Buddy",
                veterinarianName = "Dr. Roberto Lima",
                consultaType = ConsultaType.EMERGENCY,
                consultaDate = "2024-11-10",
                consultaTime = "16:45",
                status = ConsultaStatus.COMPLETED,
                symptoms = "Dificuldade respiratória, tosse persistente",
                diagnosis = "Bronquite aguda",
                treatment = "Medicação broncodilatadora, repouso",
                prescriptions = "Prednisolona 5mg - 1 comprimido 2x ao dia por 7 dias\nBromexina xarope - 5ml 3x ao dia por 10 dias",
                notes = "Retorno em 1 semana para reavaliação",
                nextAppointment = "2024-11-17",
                price = 280.0f,
                isPaid = true,
                ownerName = "Carlos Oliveira",
                ownerPhone = "(11) 99999-9012",
                createdAt = "2024-11-10T15:00:00Z",
                updatedAt = "2024-11-10T17:30:00Z"
            ),
            Consulta(
                id = "4",
                petId = "4",
                petName = "Bella",
                veterinarianName = "Dra. Fernanda Costa",
                consultaDate = "2024-11-20",
                consultaTime = "09:00",
                consultaType = ConsultaType.FOLLOW_UP,
                status = ConsultaStatus.SCHEDULED,
                symptoms = "Acompanhamento de artrite",
                diagnosis = "",
                treatment = "",
                prescriptions = "",
                notes = "Verificar evolução do tratamento para artrite",
                nextAppointment = null,
                price = 120.0f,
                isPaid = false,
                ownerName = "Ana Costa",
                ownerPhone = "(11) 99999-3456",
                createdAt = "2024-10-20T16:45:00Z",
                updatedAt = "2024-10-20T16:45:00Z"
            ),
            Consulta(
                id = "5",
                petId = "1",
                petName = "Max",
                veterinarianName = "Dr. Pedro Santos",
                consultaType = ConsultaType.DENTAL,
                consultaDate = "2024-11-18",
                consultaTime = "11:00",
                status = ConsultaStatus.SCHEDULED,
                symptoms = "Limpeza dental preventiva",
                diagnosis = "",
                treatment = "",
                prescriptions = "",
                notes = "Procedimento sob sedação leve",
                nextAppointment = null,
                price = 350.0f,
                isPaid = false,
                ownerName = "João Silva",
                ownerPhone = "(11) 99999-1234",
                createdAt = "2024-10-25T11:00:00Z",
                updatedAt = "2024-10-25T11:00:00Z"
            ),
            Consulta(
                id = "6",
                petId = "2",
                petName = "Luna",
                veterinarianName = "Dra. Juliana Alves",
                consultaType = ConsultaType.EXAM,
                consultaDate = "2024-11-08",
                consultaTime = "15:30",
                status = ConsultaStatus.COMPLETED,
                symptoms = "Exame de sangue de rotina",
                diagnosis = "Todos os parâmetros dentro da normalidade",
                treatment = "",
                prescriptions = "",
                notes = "Resultados excelentes. Animal saudável.",
                nextAppointment = null,
                price = 180.0f,
                isPaid = true,
                ownerName = "Maria Santos",
                ownerPhone = "(11) 99999-5678",
                createdAt = "2024-11-01T10:00:00Z",
                updatedAt = "2024-11-08T16:00:00Z"
            )
        )
    }
}
