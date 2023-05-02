package ru.example.gnt.rickandmorty.di.activity

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.di.CharactersRouterDependency
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.di.deps.EpisodesRouterDependency
import ru.example.gnt.locations.LocationsRouter
import ru.example.gnt.locations.di.LocationsRouterDependency
import ru.example.gnt.rickandmorty.MainActivity

@Component(
    modules = [
        RouterModule::class
    ]
)
@ScreenScope
interface ActivityComponent : LocationsRouterDependency, CharactersRouterDependency,
    EpisodesRouterDependency {
    override val locationsRouter: LocationsRouter
    override val charactersRouter: CharactersRouter
    override val episodesRouter: EpisodesRouter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun fragmentManager(fragmentManager: FragmentManager): Builder

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun mainContainerId(@IdRes containerId: Int): Builder

        fun build(): ActivityComponent
    }

    fun inject(mainActivity: MainActivity)

}
