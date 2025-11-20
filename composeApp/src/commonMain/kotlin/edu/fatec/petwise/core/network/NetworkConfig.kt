package edu.fatec.petwise.core.network

object NetworkConfig {
    const val API_URL = "http://localhost:8080" // Para TESTES LOCAIS APENAS!!!
    const val REQUEST_TIMEOUT = 30_000L
    const val CONNECT_TIMEOUT = 15_000L
    const val SOCKET_TIMEOUT = 30_000L
    const val MAX_RETRY_ATTEMPTS = 3
    const val RETRY_DELAY = 1_000L
    const val MAX_RETRY_DELAY = 10_000L
    var enableLogging = true
    const val CACHE_SIZE = 10L * 1024 * 1024
    const val CACHE_VALIDITY = 5L * 60 * 1000
}

object ApiEndpoints {
    const val LOGIN = "/api/auth/login"
    const val REGISTER = "/api/auth/register"
    const val LOGOUT = "/api/auth/logout"
    const val REFRESH_TOKEN = "/api/auth/refresh"
    const val FORGOT_PASSWORD = "/api/auth/forgot-password"
    const val RESET_PASSWORD = "/api/auth/reset-password"
    const val VERIFY_EMAIL = "/api/auth/verify-email"

    const val PETS = "/api/pets"
    fun getPet(id: String) = "$PETS/$id"
    const val PETS_SEARCH = "$PETS/search"
    const val PETS_FAVORITES = "$PETS/favorites"
    fun toggleFavorite(id: String) = "$PETS/$id/favorite"
    fun updateHealth(id: String) = "$PETS/$id/health"

    const val CONSULTAS = "/api/appointments"
    fun getConsulta(id: String) = "$CONSULTAS/$id"
    fun getConsultasByPet(petId: String) = "$CONSULTAS/pet/$petId"
    const val CONSULTAS_SEARCH = "$CONSULTAS/search"
    const val CONSULTAS_UPCOMING = "$CONSULTAS/upcoming"
    fun updateStatus(id: String) = "$CONSULTAS/$id/status"
    fun cancelConsulta(id: String) = "$CONSULTAS/$id/cancel"

    const val VACCINATIONS = "/api/vaccines"
    fun getVaccination(id: String) = "$VACCINATIONS/$id"
    fun getVaccinationsByPet(petId: String) = "$VACCINATIONS/pet/$petId"
    const val VACCINATIONS_UPCOMING = "$VACCINATIONS/upcoming"
    const val VACCINATIONS_OVERDUE = "$VACCINATIONS/overdue"
    fun markVaccinationAsApplied(id: String) = "$VACCINATIONS/$id/apply"
    fun scheduleVaccinationNextDose(id: String) = "$VACCINATIONS/$id/schedule-next"

    const val MEDICATIONS = "/api/medications"
    fun getMedication(id: String) = "$MEDICATIONS/$id"
    fun getMedicationsByPet(petId: String) = "$MEDICATIONS/pet/$petId"
    const val MEDICATIONS_SEARCH = "$MEDICATIONS/search"
    const val MEDICATIONS_UPCOMING = "$MEDICATIONS/upcoming"
    const val MEDICATIONS_EXPIRED = "$MEDICATIONS/expired"
    fun updateMedicationStatus(id: String) = "$MEDICATIONS/$id/status"
    fun completeMedication(id: String) = "$MEDICATIONS/$id/complete"

    const val USER_PROFILE = "/api/auth/profile"
    fun updateUserProfile() = "/api/auth/profile"
    
    const val EXAMS = "/api/exams"
    fun getExam(id: String) = "$EXAMS/$id"
    fun getExamsByPet(petId: String) = "$EXAMS/pet/$petId"
    fun getExamsByVeterinary(veterinaryId: String) = "/api/veterinaries/$veterinaryId/exams"

    const val PRESCRIPTIONS = "/api/vet/prescriptions"
    fun getPrescription(id: String) = "$PRESCRIPTIONS/$id"
    fun getPrescriptionsByPet(petId: String) = "$PRESCRIPTIONS/pet/$petId"
    fun getPrescriptionsByVeterinary(veterinaryId: String) = "/api/veterinaries/$veterinaryId/prescriptions"

    const val LABS = "/api/labs"
    fun getLab(id: String) = "$LABS/$id"

    const val LAB_RESULTS = "/api/labs"
    fun getLabResult(id: String) = "$LAB_RESULTS/$id"
    fun getLabResultsByPet(petId: String) = "/api/pets/$petId/lab-results"
    fun getLabResultsByVeterinary(veterinaryId: String) = "/api/veterinaries/$veterinaryId/lab-results"

    const val FOOD = "/api/foods"
    fun getFood(id: String) = "$FOOD/$id"
    const val FOOD_SEARCH = "$FOOD/search"
    const val FOOD_BY_CATEGORY = "$FOOD/category"

    const val HYGIENE = "/api/hygiene"
    fun getHygieneProduct(id: String) = "$HYGIENE/$id"
    const val HYGIENE_SEARCH = "$HYGIENE/search"
    const val HYGIENE_BY_CATEGORY = "$HYGIENE/category"

    const val TOYS = "/api/toys"
    fun getToy(id: String) = "$TOYS/$id"
    const val TOYS_SEARCH = "$TOYS/search"
    const val TOYS_BY_CATEGORY = "$TOYS/category"

    const val SUPRIMENTOS = "/api/supplies"
    fun getSuprimento(id: String) = "$SUPRIMENTOS/$id"
    fun getSuprimentosByPet(petId: String) = "/api/pets/$petId/supplies"
    const val SUPRIMENTOS_SEARCH = "$SUPRIMENTOS/search"
    const val SUPRIMENTOS_FILTER = "$SUPRIMENTOS/filter"
    const val SUPRIMENTOS_BY_CATEGORY = "$SUPRIMENTOS/category"
    const val SUPRIMENTOS_RECENT = "$SUPRIMENTOS/recent"
    const val SUPRIMENTOS_PRICE_RANGE = "$SUPRIMENTOS/price-range"
    const val SUPRIMENTOS_BY_SHOP = "$SUPRIMENTOS/shop"

}

object PetWiseHttpHeaders {
    const val AUTHORIZATION = "Authorization"
    const val CONTENT_TYPE = "Content-Type"
    const val ACCEPT = "Accept"
    const val USER_AGENT = "User-Agent"
    const val API_KEY = "X-API-Key"
    const val REQUEST_ID = "X-Request-ID"
    const val PLATFORM = "X-Platform"
}

object PetWiseContentTypes {
    const val JSON = "application/json"
    const val FORM_URL_ENCODED = "application/x-www-form-urlencoded"
    const val MULTIPART_FORM_DATA = "multipart/form-data"
}
