package edu.fatec.petwise.features.pettags.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult
import edu.fatec.petwise.features.pettags.domain.models.TagScanStatus
import edu.fatec.petwise.features.pettags.domain.usecases.*
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PetTagUiState(
    val scanStatus: TagScanStatus = TagScanStatus.IDLE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    
    val isPairingMode: Boolean = false,
    val selectedPetForPairing: Pet? = null,
    
    
    val pets: List<Pet> = emptyList(),
    val showPetSelectionDialog: Boolean = false,
    
    
    val lastCheckInResult: TagCheckInResult? = null,
    val lastTagRead: TagReadResult? = null,
    
    
    val manualTagUid: String = "",
    val showManualInput: Boolean = false
)

sealed class PetTagUiEvent {
    
    object LoadPets : PetTagUiEvent()
    
    
    object ShowPetSelectionDialog : PetTagUiEvent()
    object HidePetSelectionDialog : PetTagUiEvent()
    data class SelectPetForPairing(val pet: Pet) : PetTagUiEvent()
    object StartPairing : PetTagUiEvent()
    object CancelPairing : PetTagUiEvent()
    
    
    data class SimulateTagScan(val tagUid: String) : PetTagUiEvent()
    object StartPollingLastRead : PetTagUiEvent()
    object StopPollingLastRead : PetTagUiEvent()
    
    
    data class ProcessCheckIn(val tagUid: String) : PetTagUiEvent()
    
    
    data class GetPetByTag(val tagUid: String) : PetTagUiEvent()
    
    
    object ToggleManualInput : PetTagUiEvent()
    data class UpdateManualTagUid(val uid: String) : PetTagUiEvent()
    object SubmitManualTag : PetTagUiEvent()
    
    
    object ClearError : PetTagUiEvent()
    object ClearSuccess : PetTagUiEvent()
    object ResetState : PetTagUiEvent()
}

class PetTagViewModel(
    private val startPairingUseCase: StartPairingUseCase,
    private val checkInUseCase: CheckInUseCase,
    private val getPetByTagUseCase: GetPetByTagUseCase,
    private val getLastReadUseCase: GetLastReadUseCase,
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetTagUiState())
    val uiState: StateFlow<PetTagUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.PetsUpdated -> loadPets()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = PetTagUiState()
                        println("PetTagViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: PetTagUiEvent) {
        when (event) {
            is PetTagUiEvent.LoadPets -> loadPets()
            is PetTagUiEvent.ShowPetSelectionDialog -> showPetSelectionDialog()
            is PetTagUiEvent.HidePetSelectionDialog -> hidePetSelectionDialog()
            is PetTagUiEvent.SelectPetForPairing -> selectPetForPairing(event.pet)
            is PetTagUiEvent.StartPairing -> startPairing()
            is PetTagUiEvent.CancelPairing -> cancelPairing()
            is PetTagUiEvent.SimulateTagScan -> simulateTagScan(event.tagUid)
            is PetTagUiEvent.StartPollingLastRead -> startPollingLastRead()
            is PetTagUiEvent.StopPollingLastRead -> stopPollingLastRead()
            is PetTagUiEvent.ProcessCheckIn -> processCheckIn(event.tagUid)
            is PetTagUiEvent.GetPetByTag -> getPetByTag(event.tagUid)
            is PetTagUiEvent.ToggleManualInput -> toggleManualInput()
            is PetTagUiEvent.UpdateManualTagUid -> updateManualTagUid(event.uid)
            is PetTagUiEvent.SubmitManualTag -> submitManualTag()
            is PetTagUiEvent.ClearError -> clearError()
            is PetTagUiEvent.ClearSuccess -> clearSuccess()
            is PetTagUiEvent.ResetState -> resetState()
        }
    }

    private fun loadPets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getPetsUseCase().collect { pets ->
                    println("PetTagViewModel: ${pets.size} pets carregados para seleção")
                    _uiState.value = _uiState.value.copy(
                        pets = pets,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("PetTagViewModel: Erro ao carregar pets - ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar lista de pets: ${e.message}"
                )
            }
        }
    }

    private fun showPetSelectionDialog() {
        _uiState.value = _uiState.value.copy(showPetSelectionDialog = true)
        if (_uiState.value.pets.isEmpty()) {
            loadPets()
        }
    }

    private fun hidePetSelectionDialog() {
        _uiState.value = _uiState.value.copy(showPetSelectionDialog = false)
    }

    private fun selectPetForPairing(pet: Pet) {
        _uiState.value = _uiState.value.copy(
            selectedPetForPairing = pet,
            showPetSelectionDialog = false
        )
    }

    private fun startPairing() {
        val pet = _uiState.value.selectedPetForPairing
        if (pet == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Selecione um pet para associar à tag"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                scanStatus = TagScanStatus.PAIRING
            )

            startPairingUseCase(pet.id).fold(
                onSuccess = { message ->
                    println("PetTagViewModel: Pareamento iniciado - $message")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPairingMode = true,
                        successMessage = "Modo de pareamento ativado para ${pet.name}. Aproxime a tag NFC.",
                        scanStatus = TagScanStatus.PAIRING
                    )
                    
                    startPollingLastRead()
                },
                onFailure = { error ->
                    println("PetTagViewModel: Erro ao iniciar pareamento - ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPairingMode = false,
                        errorMessage = "Erro ao iniciar pareamento: ${error.message}",
                        scanStatus = TagScanStatus.ERROR
                    )
                }
            )
        }
    }

    private fun cancelPairing() {
        stopPollingLastRead()
        _uiState.value = _uiState.value.copy(
            isPairingMode = false,
            selectedPetForPairing = null,
            scanStatus = TagScanStatus.IDLE
        )
    }

    private fun simulateTagScan(tagUid: String) {
        if (_uiState.value.isPairingMode) {
            
            processCheckIn(tagUid)
        } else {
            
            getPetByTag(tagUid)
        }
    }

    private fun startPollingLastRead() {
        stopPollingLastRead() 
        
        pollingJob = viewModelScope.launch {
            while (isActive) {
                getLastReadUseCase().fold(
                    onSuccess = { result ->
                        if (result.tagUid != null && result.tagUid != _uiState.value.lastTagRead?.tagUid) {
                            println("PetTagViewModel: Nova tag detectada - ${result.tagUid}")
                            _uiState.value = _uiState.value.copy(
                                lastTagRead = result,
                                scanStatus = TagScanStatus.TAG_FOUND
                            )
                            
                            
                            if (_uiState.value.isPairingMode) {
                                processCheckIn(result.tagUid)
                            }
                        }
                    },
                    onFailure = { error ->
                        println("PetTagViewModel: Erro no polling - ${error.message}")
                    }
                )
                delay(2000) 
            }
        }
    }

    private fun stopPollingLastRead() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun processCheckIn(tagUid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                scanStatus = TagScanStatus.SCANNING
            )

            checkInUseCase(tagUid).fold(
                onSuccess = { result ->
                    println("PetTagViewModel: Check-in realizado - ${result.petName}")
                    
                    val wasInPairingMode = _uiState.value.isPairingMode
                    stopPollingLastRead()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastCheckInResult = result,
                        isPairingMode = false,
                        selectedPetForPairing = null,
                        scanStatus = if (wasInPairingMode) TagScanStatus.TAG_REGISTERED else TagScanStatus.PET_FOUND,
                        successMessage = if (wasInPairingMode) {
                            "Tag associada com sucesso ao pet ${result.petName}!"
                        } else {
                            "Check-in realizado: ${result.petName}"
                        }
                    )
                },
                onFailure = { error ->
                    println("PetTagViewModel: Erro no check-in - ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro no check-in: ${error.message}",
                        scanStatus = TagScanStatus.ERROR
                    )
                }
            )
        }
    }

    private fun getPetByTag(tagUid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                scanStatus = TagScanStatus.SCANNING
            )

            getPetByTagUseCase(tagUid).fold(
                onSuccess = { result ->
                    println("PetTagViewModel: Pet encontrado - ${result.petName}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastCheckInResult = result,
                        scanStatus = TagScanStatus.PET_FOUND,
                        successMessage = "Pet encontrado: ${result.petName}"
                    )
                },
                onFailure = { error ->
                    println("PetTagViewModel: Erro ao buscar pet - ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tag não encontrada ou não associada a nenhum pet",
                        scanStatus = TagScanStatus.ERROR
                    )
                }
            )
        }
    }

    private fun toggleManualInput() {
        _uiState.value = _uiState.value.copy(
            showManualInput = !_uiState.value.showManualInput
        )
    }

    private fun updateManualTagUid(uid: String) {
        _uiState.value = _uiState.value.copy(manualTagUid = uid)
    }

    private fun submitManualTag() {
        val tagUid = _uiState.value.manualTagUid.trim()
        if (tagUid.isNotBlank()) {
            simulateTagScan(tagUid)
            _uiState.value = _uiState.value.copy(
                manualTagUid = "",
                showManualInput = false
            )
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    private fun resetState() {
        stopPollingLastRead()
        _uiState.value = PetTagUiState()
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingLastRead()
    }
}
