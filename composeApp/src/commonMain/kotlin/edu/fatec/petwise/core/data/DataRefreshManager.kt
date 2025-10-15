package edu.fatec.petwise.core.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class DataRefreshEvent {
    object PetsUpdated : DataRefreshEvent()
    object ConsultasUpdated : DataRefreshEvent()
    object VaccinationsUpdated : DataRefreshEvent()
    object AllDataUpdated : DataRefreshEvent()
    data class PetUpdated(val petId: String) : DataRefreshEvent()
    data class ConsultaUpdated(val consultaId: String) : DataRefreshEvent()
    data class VaccinationUpdated(val vaccinationId: String) : DataRefreshEvent()
}

object DataRefreshManager {
    private val _refreshEvents = MutableSharedFlow<DataRefreshEvent>()
    val refreshEvents: SharedFlow<DataRefreshEvent> = _refreshEvents.asSharedFlow()

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

    fun notifyVaccinationsUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.VaccinationsUpdated)
        if (!emitted) {
            println("DataRefreshManager: Falha ao emitir evento VaccinationsUpdated")
        }
    }

    fun notifyAllDataUpdated() {
        val emitted = _refreshEvents.tryEmit(DataRefreshEvent.AllDataUpdated)
        if (!emitted) {
            println("DataRefreshManager: Nenhum observador ativo para AllDataUpdated (normal durante logout)")
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
}