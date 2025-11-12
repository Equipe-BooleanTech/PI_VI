package edu.fatec.petwise.core.data

import edu.fatec.petwise.features.pets.domain.models.*
import edu.fatec.petwise.features.consultas.domain.models.*
import edu.fatec.petwise.features.vaccinations.domain.models.*
import edu.fatec.petwise.features.medications.domain.models.*
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy
import edu.fatec.petwise.features.suprimentos.domain.models.Suprimento
import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.food.domain.models.Food
import edu.fatec.petwise.features.toys.domain.models.Toy
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct

object MockDataProvider {

    fun getMockPets(): List<Pet> = listOf(
        Pet(
            id = "mock-pet-1",
            name = "Rex",
            breed = "Labrador",
            species = PetSpecies.DOG,
            gender = PetGender.MALE,
            age = 5,
            weight = 25.5f,
            healthStatus = HealthStatus.EXCELLENT,
            ownerName = "Mock Owner 1",
            ownerPhone = "(11) 98765-4321",
            healthHistory = "Nenhum histórico médico registrado",
            profileImageUrl = null,
            isFavorite = false,
            nextAppointment = "2025-12-01",
            createdAt = "2020-05-15T10:00:00",
            updatedAt = "2025-11-12T10:00:00"
        ),
        Pet(
            id = "mock-pet-2",
            name = "Luna",
            breed = "Siamês",
            species = PetSpecies.CAT,
            gender = PetGender.FEMALE,
            age = 4,
            weight = 4.2f,
            healthStatus = HealthStatus.GOOD,
            ownerName = "Mock Owner 1",
            ownerPhone = "(11) 98765-4321",
            healthHistory = "Vacinação em dia",
            profileImageUrl = null,
            isFavorite = true,
            nextAppointment = "2025-11-20",
            createdAt = "2021-03-20T10:00:00",
            updatedAt = "2025-11-12T10:00:00"
        )
    )

    fun getMockConsultas(): List<Consulta> = listOf(
        Consulta(
            id = "mock-consulta-1",
            petId = "mock-pet-1",
            petName = "Rex",
            veterinarianName = "Dr. João Silva",
            consultaType = ConsultaType.ROUTINE,
            consultaDate = "2025-11-15",
            consultaTime = "10:00",
            status = ConsultaStatus.COMPLETED,
            symptoms = "Check-up de rotina",
            diagnosis = "Saúde normal",
            treatment = "Nenhum tratamento necessário",
            prescriptions = "",
            notes = "Pet apresenta boa saúde",
            nextAppointment = "2026-05-15",
            price = 150.0f,
            isPaid = true,
            createdAt = "2025-11-10T09:00:00",
            updatedAt = "2025-11-15T11:00:00"
        ),
        Consulta(
            id = "mock-consulta-2",
            petId = "mock-pet-2",
            petName = "Luna",
            veterinarianName = "Dr. João Silva",
            consultaType = ConsultaType.VACCINATION,
            consultaDate = "2025-11-20",
            consultaTime = "14:00",
            status = ConsultaStatus.SCHEDULED,
            symptoms = "",
            diagnosis = "",
            treatment = "",
            prescriptions = "",
            notes = "",
            nextAppointment = null,
            price = 80.0f,
            isPaid = false,
            createdAt = "2025-11-12T10:00:00",
            updatedAt = "2025-11-12T10:00:00"
        )
    )

    fun getMockVaccinations(): List<Vaccination> = listOf(
        Vaccination(
            id = "mock-vac-1",
            petId = "mock-pet-1",
            veterinarianId = "mock-vet-1",
            vaccineType = VaccineType.ANTIRABICA,
            vaccinationDate = "2025-06-01",
            nextDoseDate = "2026-06-01",
            totalDoses = 1,
            manufacturer = "LabVet",
            observations = "Aplicada sem intercorrências",
            status = VaccinationStatus.APLICADA,
            createdAt = "2025-06-01T10:00:00",
            updatedAt = "2025-06-01T10:00:00"
        ),
        Vaccination(
            id = "mock-vac-2",
            petId = "mock-pet-1",
            veterinarianId = "mock-vet-1",
            vaccineType = VaccineType.V10,
            vaccinationDate = "2025-07-15",
            nextDoseDate = "2026-07-15",
            totalDoses = 1,
            manufacturer = "PetVaccine",
            observations = "",
            status = VaccinationStatus.APLICADA,
            createdAt = "2025-07-15T14:00:00",
            updatedAt = "2025-07-15T14:00:00"
        )
    )

    fun getMockMedications(): List<Medication> = listOf(
        Medication(
            id = "mock-med-1",
            userId = "mock-owner-1",
            petId = "mock-pet-1",
            veterinarianId = "mock-vet-1",
            prescriptionId = "mock-prescription-1",
            medicationName = "Antibiótico XYZ",
            dosage = "500mg",
            frequency = "2x ao dia",
            durationDays = 10,
            startDate = "2025-11-01",
            endDate = "2025-11-10",
            sideEffects = "",
            createdAt = "2025-11-01T09:00:00",
            updatedAt = "2025-11-10T09:00:00"
        ),
        Medication(
            id = "mock-med-2",
            userId = "mock-owner-1",
            petId = "mock-pet-2",
            veterinarianId = "mock-vet-1",
            prescriptionId = "mock-prescription-2",
            medicationName = "Anti-inflamatório ABC",
            dosage = "50mg",
            frequency = "1x ao dia",
            durationDays = 10,
            startDate = "2025-11-12",
            endDate = "2025-11-22",
            sideEffects = "",
            createdAt = "2025-11-12T10:00:00",
            updatedAt = "2025-11-12T10:00:00"
        )
    )

    fun getMockVeterinaries(): List<Veterinary> = listOf(
        Veterinary(
            id = "mock-vet-1",
            fullName = "Dr. João Silva",
            email = "joao.silva@vetclinic.com",
            phone = "(11) 98765-4321",
            userType = "VETERINARY",
            profileImageUrl = null,
            verified = true,
            createdAt = "2024-01-15T10:00:00",
            updatedAt = "2024-01-15T10:00:00"
        ),
        Veterinary(
            id = "mock-vet-2",
            fullName = "Dra. Maria Santos",
            email = "maria.santos@vetclinic.com",
            phone = "(11) 98765-1234",
            userType = "VETERINARY",
            profileImageUrl = null,
            verified = true,
            createdAt = "2024-02-20T11:00:00",
            updatedAt = "2024-02-20T11:00:00"
        )
    )

    fun getMockPharmacies(): List<Pharmacy> = listOf(
        Pharmacy(
            id = "mock-pharm-1",
            fullName = "Farmácia PetCare",
            email = "contato@petcarepharm.com",
            phone = "(11) 3456-7890",
            userType = "PHARMACY",
            profileImageUrl = null,
            verified = true,
            createdAt = "2024-03-10T09:00:00",
            updatedAt = "2024-03-10T09:00:00"
        ),
        Pharmacy(
            id = "mock-pharm-2",
            fullName = "Vet Pharma Express",
            email = "vendas@vetpharmaexpress.com",
            phone = "(11) 3456-1234",
            userType = "PHARMACY",
            profileImageUrl = null,
            verified = true,
            createdAt = "2024-04-05T10:00:00",
            updatedAt = "2024-04-05T10:00:00"
        )
    )

    fun getMockSuprimentos(): List<Suprimento> = listOf(
        Suprimento(
            id = "mock-sup-1",
            petId = "mock-pet-1",
            description = "Ração Premium Adulto 15kg",
            category = SuprimentCategory.FOOD,
            price = 150.00f,
            orderDate = "2025-01-10",
            shopName = "PetShop Central",
            createdAt = "2025-01-10T10:00:00",
            updatedAt = "2025-01-10T10:00:00"
        ),
        Suprimento(
            id = "mock-sup-2",
            petId = "mock-pet-2",
            description = "Shampoo Neutro para Gatos",
            category = SuprimentCategory.HYGIENE,
            price = 25.00f,
            orderDate = "2025-02-15",
            shopName = "Mundo Pet",
            createdAt = "2025-02-15T11:00:00",
            updatedAt = "2025-02-15T11:00:00"
        ),
        Suprimento(
            id = "mock-sup-3",
            petId = "mock-pet-1",
            description = "Bola de Borracha Resistente",
            category = SuprimentCategory.TOY,
            price = 15.00f,
            orderDate = "2025-03-01",
            shopName = "PetShop Central",
            createdAt = "2025-03-01T09:00:00",
            updatedAt = "2025-03-01T09:00:00"
        )
    )

    fun getMockPrescriptions(): List<Prescription> = listOf(
        Prescription(
            id = "mock-presc-1",
            petId = "mock-pet-1",
            veterinaryId = "mock-vet-1",
            medicationName = "Amoxicilina",
            dosage = "500mg",
            frequency = "2x ao dia",
            duration = "10 dias",
            instructions = "Administrar com alimento",
            startDate = "2025-11-10",
            endDate = "2025-11-20",
            status = "Ativa",
            notes = "Observar possíveis reações alérgicas",
            createdAt = "2025-11-10T09:00:00",
            updatedAt = "2025-11-10T09:00:00"
        ),
        Prescription(
            id = "mock-presc-2",
            petId = "mock-pet-2",
            veterinaryId = "mock-vet-1",
            medicationName = "Anti-inflamatório",
            dosage = "50mg",
            frequency = "1x ao dia",
            duration = "7 dias",
            instructions = "Após refeições",
            startDate = "2025-11-12",
            endDate = "2025-11-19",
            status = "Ativa",
            notes = "Administrar após refeições",
            createdAt = "2025-11-12T10:00:00",
            updatedAt = "2025-11-12T10:00:00"
        )
    )

    fun getMockExams(): List<Exam> = listOf(
        Exam(
            id = "mock-exam-1",
            petId = "mock-pet-1",
            veterinaryId = "mock-vet-1",
            examType = "Exame de Sangue",
            examDate = "2025-11-05",
            results = "Hemograma normal, função renal e hepática dentro dos padrões",
            status = "Concluído",
            notes = "Pet em ótimas condições de saúde",
            attachmentUrl = null,
            createdAt = "2025-11-05T14:00:00",
            updatedAt = "2025-11-06T09:00:00"
        ),
        Exam(
            id = "mock-exam-2",
            petId = "mock-pet-2",
            veterinaryId = "mock-vet-1",
            examType = "Ultrassom Abdominal",
            examDate = "2025-11-08",
            results = "Órgãos abdominais sem alterações",
            status = "Concluído",
            notes = "Exame preventivo",
            attachmentUrl = null,
            createdAt = "2025-11-08T10:00:00",
            updatedAt = "2025-11-08T16:00:00"
        )
    )

    fun getMockLabs(): List<Lab> = listOf(
        Lab(
            id = "mock-lab-1",
            veterinaryId = "mock-vet-1",
            labName = "Laboratório VetAnalysis",
            testType = "Análise de Urina",
            testDate = "2025-11-03",
            results = "pH 6.5, densidade 1.030, sem presença de cristais ou bactérias",
            status = "Finalizado",
            notes = "Resultado dentro dos parâmetros normais",
            createdAt = "2025-11-03T11:00:00",
            updatedAt = "2025-11-04T09:00:00"
        ),
        Lab(
            id = "mock-lab-2",
            veterinaryId = "mock-vet-2",
            labName = "LabVet Central",
            testType = "Cultura Bacteriana",
            testDate = "2025-11-07",
            results = "Cultura negativa para crescimento bacteriano",
            status = "Finalizado",
            notes = "Amostra colhida em condições adequadas",
            createdAt = "2025-11-07T08:00:00",
            updatedAt = "2025-11-09T10:00:00"
        )
    )

    fun getMockFood(): List<Food> = listOf(
        Food(
            id = "mock-food-1",
            name = "Ração Premium Adulto",
            brand = "NutriPet",
            category = "Ração Seca",
            description = "Ração completa para cães adultos",
            price = 150.00,
            stock = 50,
            unit = "kg",
            expiryDate = "2026-05-30",
            imageUrl = null,
            active = true,
            createdAt = "2025-01-10T10:00:00",
            updatedAt = "2025-01-10T10:00:00"
        ),
        Food(
            id = "mock-food-2",
            name = "Petiscos Naturais",
            brand = "PetSnacks",
            category = "Petiscos",
            description = "Petiscos 100% naturais para gatos",
            price = 35.00,
            stock = 100,
            unit = "unidades",
            expiryDate = "2025-12-31",
            imageUrl = null,
            active = true,
            createdAt = "2025-02-15T11:00:00",
            updatedAt = "2025-02-15T11:00:00"
        )
    )

    fun getMockHygieneProducts(): List<HygieneProduct> = listOf(
        HygieneProduct(
            id = "mock-hygiene-1",
            name = "Shampoo Neutro",
            brand = "PetClean",
            category = "Higiene",
            description = "Shampoo neutro para pets sensíveis",
            price = 25.00,
            stock = 30,
            unit = "unidades",
            expiryDate = "2027-12-31",
            imageUrl = null,
            active = true,
            createdAt = "2025-02-15T11:00:00",
            updatedAt = "2025-02-15T11:00:00"
        ),
        HygieneProduct(
            id = "mock-hygiene-2",
            name = "Escova Dental para Pets",
            brand = "DentalPet",
            category = "Higiene Dental",
            description = "Escova especial para higiene bucal",
            price = 18.00,
            stock = 45,
            unit = "unidades",
            expiryDate = null,
            imageUrl = null,
            active = true,
            createdAt = "2025-03-10T09:00:00",
            updatedAt = "2025-03-10T09:00:00"
        )
    )

    fun getMockToys(): List<Toy> = listOf(
        Toy(
            id = "mock-toy-1",
            name = "Bola de Borracha Resistente",
            brand = "PlayPet",
            category = "Brinquedos",
            description = "Bola super resistente para cães",
            price = 15.00,
            stock = 100,
            unit = "unidades",
            material = "Borracha TPR",
            ageRecommendation = "Todos os tamanhos",
            imageUrl = null,
            active = true,
            createdAt = "2025-03-01T09:00:00",
            updatedAt = "2025-03-01T09:00:00"
        ),
        Toy(
            id = "mock-toy-2",
            name = "Arranhador para Gatos",
            brand = "CatPlay",
            category = "Brinquedos para Gatos",
            description = "Arranhador vertical com sisal",
            price = 85.00,
            stock = 20,
            unit = "unidades",
            material = "Sisal e MDF",
            ageRecommendation = "Gatos adultos",
            imageUrl = null,
            active = true,
            createdAt = "2025-03-05T10:00:00",
            updatedAt = "2025-03-05T10:00:00"
        )
    )
}
