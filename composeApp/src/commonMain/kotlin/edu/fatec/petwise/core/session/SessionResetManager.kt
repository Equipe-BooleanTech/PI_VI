package edu.fatec.petwise.core.session

import edu.fatec.petwise.features.consultas.di.ConsultaDependencyContainer
import edu.fatec.petwise.features.dashboard.di.DashboardDepedencyContainer
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.features.vaccinations.di.VaccinationDependencyContainer
import edu.fatec.petwise.navigation.NavigationManager

object SessionResetManager {

    fun resetFeatureContainers() {
        DashboardDepedencyContainer.reset()
        PetDependencyContainer.reset()
        ConsultaDependencyContainer.reset()
        VaccinationDependencyContainer.reset()
    }

    fun resetNavigation(navigationManager: NavigationManager) {
        navigationManager.reset()
    }
}
