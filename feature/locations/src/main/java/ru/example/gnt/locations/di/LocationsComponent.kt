package ru.example.gnt.locations.di

import dagger.Component
import ru.example.gnt.locations.presentation.list.LocationListFragment

@Component(
    modules = [
        ViewModelModule::class,
        RepositoryModule::class
    ],
    dependencies = [LocationDependencies::class, LocationsRouterDependency::class]
)
internal interface LocationsComponent {
    fun inject(locationListFragment: LocationListFragment)

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: LocationDependencies): Builder
        fun routerDependency(routerDependency: LocationsRouterDependency): Builder
        fun build(): LocationsComponent
    }
}
