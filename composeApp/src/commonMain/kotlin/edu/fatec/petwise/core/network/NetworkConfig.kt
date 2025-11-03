package edu.fatec.petwise.core.network

object NetworkConfig {
    const val API_URL = "http://localhost:8080/api" // Para TESTES LOCAIS APENAS!!!
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
    const val CONSULTAS_SEARCH = "$CONSULTAS/search"
    const val CONSULTAS_UPCOMING = "$CONSULTAS/upcoming"
    fun updateStatus(id: String) = "$CONSULTAS/$id/status"
    fun cancelConsulta(id: String) = "$CONSULTAS/$id/cancel"

    const val VACCINATIONS = "/api/vaccines"
    fun getVaccination(id: String) = "$VACCINATIONS/$id"
    fun getVaccinationsByPet(petId: String) = "/api/pets/$petId/vaccines"
    const val VACCINATIONS_UPCOMING = "$VACCINATIONS/upcoming"
    const val VACCINATIONS_OVERDUE = "$VACCINATIONS/overdue"
    fun markVaccinationAsApplied(id: String) = "$VACCINATIONS/$id/apply"
    fun scheduleVaccinationNextDose(id: String) = "$VACCINATIONS/$id/schedule-next"

    const val USER_PROFILE = "/api/auth/profile"

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
