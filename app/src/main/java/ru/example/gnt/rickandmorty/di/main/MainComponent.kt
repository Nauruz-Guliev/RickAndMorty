package ru.example.gnt.rickandmorty.di.main

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.di.NavigatorDeps
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.rickandmorty.MainActivity

@Component(
    modules = [
        MainModule::class
    ]
)
@ScreenScope
interface MainComponent: NavigatorDeps {

    override val charactersRouter: CharactersRouter
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun fragmentManager(fragmentManager: FragmentManager): Builder

        @BindsInstance
        fun mainContainerId(@IdRes containerId: Int): Builder
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): MainComponent
    }

    fun inject(mainActivity: MainActivity)

}
