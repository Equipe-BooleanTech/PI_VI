package edu.fatec.petwise.core.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class DataRefreshEvent {
    object PetsUpdated : DataRefreshEvent()
    object ConsultasUpdated : DataRefreshEvent()
    object VaccinationsUpdated : DataRefreshEvent()
    object PrescriptionsUpdated : DataRefreshEvent()
    object LabResultsUpdated : DataRefreshEvent()
    object ExamsUpdated : DataRefreshEvent()
    object UserLoggedIn : DataRefreshEvent()
    object UserLoggedOut : DataRefreshEvent()
    object AllDataUpdated : DataRefreshEvent()
    data class PetUpdated(val petId: String) : DataRefreshEvent()
    data class ConsultaUpdated(val consultaId: String) : DataRefreshEvent()
    data class VaccinationUpdated(val vaccinationId: String) : DataRefreshEvent()
    data class PrescriptionUpdated(val prescriptionId: String) : DataRefreshEvent()
    data class LabResultUpdated(val labResultId: String) : DataRefreshEvent()
    data class ExamUpdated(val examId: String) : DataRefreshEvent()
}

object DataRefreshManager {
    private val _refreshEvents = MutableSharedFlow<DataRefreshEvent>(replay = 1)
    val refreshEvents: SharedFlow<DataRefreshEvent> = _refreshEvents.asSharedFlow()
    
    private val scope = CoroutineScope(Dispatchers.Default)

    fun notifyPetsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.PetsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento PetsUpdated")
        }
    }

    fun notifyConsultasUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.ConsultasUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento ConsultasUpdated")
        }
    }

     fun notifyUserLoggedOut() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.UserLoggedOut)
        if (!emitted) {
            println("DataRefreshManager: Nenhum observador ativo para UserLoggedOut (normal durante logout)")
        }
    }

    fun notifyVaccinationsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.VaccinationsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento VaccinationsUpdated")
        }
    }

    fun notifyPrescriptionsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.PrescriptionsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento PrescriptionsUpdated")
        }
    }

    fun notifyUserLoggedIn() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.UserLoggedIn)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento UserLoggedIn")
        }
    }

    fun notifyAllDataUpdated() {
        println("DataRefreshManager: Emitindo evento AllDataUpdated")
        scope.launch {
            _refreshEvents.emit(DataRefreshEvent.AllDataUpdated)
            println("DataRefreshManager: AllDataUpdated emitido com sucesso")
        }
    }

    fun notifyPetUpdated(petId: String) {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.PetUpdated(petId))
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento PetUpdated para '$petId'")
        }
    }

    fun notifyConsultaUpdated(consultaId: String) {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.ConsultaUpdated(consultaId))
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento ConsultaUpdated para '$consultaId'")
        }
    }

    fun notifyVaccinationUpdated(vaccinationId: String) {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.VaccinationUpdated(vaccinationId))
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento VaccinationUpdated para '$vaccinationId'")
        }
    }

    fun notifyLabResultsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.LabResultsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento LabResultsUpdated")
        }
    }

    fun notifyLabResultUpdated(labResultId: String) {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.LabResultUpdated(labResultId))
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento LabResultUpdated para '$labResultId'")
        }
    }

    fun notifyExamsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.ExamsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento ExamsUpdated")
        }
    }

    fun notifyExamUpdated(examId: String) {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.ExamUpdated(examId))
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento ExamUpdated para '$examId'")
        }
    }
}