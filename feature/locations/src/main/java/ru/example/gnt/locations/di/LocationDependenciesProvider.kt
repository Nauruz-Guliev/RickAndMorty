package ru.example.gnt.locations.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

interface LocationDependenciesProvider {


    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: LocationDependencies

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val locationsRouterDependency: LocationsRouterDependency

    companion object : LocationDependenciesProvider by LocationDependencyStore
}

object LocationDependencyStore : LocationDependenciesProvider {
    override var deps: LocationDependencies by Delegates.notNull()
    override var locationsRouterDependency: LocationsRouterDependency by Delegates.notNull()
}

internal class LocationsComponentViewModel : ViewModel() {
    val locationComponent = DaggerLocationsComponent.builder()
        .dependencies(LocationDependencyStore.deps)
        .routerDependency(LocationDependenciesProvider.locationsRouterDependency)
        .build()
}
