package ru.example.gnt.rickandmorty.di.main

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.di.CharactersRouterDeps
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.di.deps.EpisodesRouterDeps
import ru.example.gnt.rickandmorty.MainActivity
import ru.example.gnt.rickandmorty.di.RouterModule

@Component(
    modules = [
        ActivityModule::class,
        RouterModule::class
    ]
)
@ScreenScope
interface ActivityComponent : CharactersRouterDeps, EpisodesRouterDeps {

    override val charactersRouter: CharactersRouter
    override val router: EpisodesRouter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun fragmentManager(fragmentManager: FragmentManager): Builder

        @BindsInstance
        fun context(context: Context) : Builder
        @BindsInstance
        fun mainContainerId(@IdRes containerId: Int): Builder

        fun build(): ActivityComponent
    }

    fun inject(mainActivity: MainActivity)

}
