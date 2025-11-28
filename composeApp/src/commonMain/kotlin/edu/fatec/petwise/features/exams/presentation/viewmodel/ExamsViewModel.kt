package edu.fatec.petwise.features.exams.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.usecases.*
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ExamsUiState(
    val exams: List<Exam> = emptyList(),
    val filteredExams: List<Exam> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedExam: Exam? = null,
    val searchQuery: String = "",
    val filterStatus: String? = null,
    val petNames: Map<String, String> = emptyMap(),
    val veterinaryName: String = ""
)

sealed class ExamsUiEvent {
    object LoadExams : ExamsUiEvent()
    data class LoadExamsByPet(val petId: String) : ExamsUiEvent()
    data class DeleteExam(val id: String) : ExamsUiEvent()
    data class SelectExam(val exam: Exam?) : ExamsUiEvent()
    data class SearchExams(val query: String) : ExamsUiEvent()
    data class FilterByStatus(val status: String?) : ExamsUiEvent()
    object ClearError : ExamsUiEvent()
}

class ExamsViewModel(
    private val getExamsUseCase: GetExamsUseCase,
    private val deleteExamUseCase: DeleteExamUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamsUiState())
    val uiState: StateFlow<ExamsUiState> = _uiState.asStateFlow()

    init {
        observeDataRefresh()
        loadPetNames()
        loadVeterinaryName()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.ExamsUpdated -> loadExams()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = ExamsUiState()
                        println("ExamsViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: ExamsUiEvent) {
        when (event) {
            is ExamsUiEvent.LoadExams -> loadExams()
            is ExamsUiEvent.LoadExamsByPet -> loadExamsByPet(event.petId)
            is ExamsUiEvent.DeleteExam -> deleteExam(event.id)
            is ExamsUiEvent.SelectExam -> selectExam(event.exam)
            is ExamsUiEvent.SearchExams -> searchExams(event.query)
            is ExamsUiEvent.FilterByStatus -> filterByStatus(event.status)
            is ExamsUiEvent.ClearError -> clearError()
            is DataRefreshEvent.UserLoggedOut -> {
                println("ExamsViewModel: Usuário deslogou — limpando estado")
                _uiState.value = ExamsUiState()
            }
        }
    }

    private fun loadExams() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getExamsUseCase().collect { exams ->
                    val filteredExams = applyFilters(exams, _uiState.value.searchQuery, _uiState.value.filterStatus)
                    _uiState.value = _uiState.value.copy(
                        exams = exams,
                        filteredExams = filteredExams,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar exames"
                )
            }
        }
    }

    private fun loadExamsByPet(petId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getExamsUseCase.getExamsByPetId(petId).collect { exams ->
                    val filteredExams = applyFilters(exams, _uiState.value.searchQuery, _uiState.value.filterStatus)
                    _uiState.value = _uiState.value.copy(
                        exams = exams,
                        filteredExams = filteredExams,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar exames do pet"
                )
            }
        }
    }

    private fun deleteExam(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                deleteExamUseCase(id).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadExams()
                        DataRefreshManager.notifyExamsUpdated()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao deletar exame"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao deletar exame"
                )
            }
        }
    }

    private fun selectExam(exam: Exam?) {
        _uiState.value = _uiState.value.copy(selectedExam = exam)
    }

    private fun searchExams(query: String) {
        val filteredExams = applyFilters(_uiState.value.exams, query, _uiState.value.filterStatus)
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredExams = filteredExams
        )
    }

    private fun filterByStatus(status: String?) {
        val filteredExams = applyFilters(_uiState.value.exams, _uiState.value.searchQuery, status)
        _uiState.value = _uiState.value.copy(
            filterStatus = status,
            filteredExams = filteredExams
        )
    }

    private fun applyFilters(exams: List<Exam>, query: String, status: String?): List<Exam> {
        return exams.filter { exam ->
            val matchesQuery = query.isEmpty() ||
                exam.examType.contains(query, ignoreCase = true) ||
                exam.status.contains(query, ignoreCase = true)

            val matchesStatus = status == null || exam.status == status

            matchesQuery && matchesStatus
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun loadPetNames() {
        viewModelScope.launch {
            try {
                getPetsUseCase().collect { pets ->
                    val petNamesMap = pets.associate { it.id to it.name }
                    _uiState.value = _uiState.value.copy(petNames = petNamesMap)
                }
            } catch (e: Exception) {
                println("ExamsViewModel: Erro ao carregar nomes dos pets: ${e.message}")
            }
        }
    }

    private fun loadVeterinaryName() {
        viewModelScope.launch {
            try {
                val result = getUserProfileUseCase.execute()
                result.fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(veterinaryName = profile.fullName)
                    },
                    onFailure = { error ->
                        println("ExamsViewModel: Erro ao carregar nome do veterinário: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                println("ExamsViewModel: Erro ao carregar nome do veterinário: ${e.message}")
            }
        }
    }
}