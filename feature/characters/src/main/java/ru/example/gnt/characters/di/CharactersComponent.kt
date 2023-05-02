package ru.example.gnt.characters.di

import dagger.Component
import ru.example.gnt.characters.presentation.list.CharactersFragment
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.common.di.scope.ScreenScope


@Component(
    modules = [
        CharactersModule::class
    ],
    dependencies = [CharactersDependencies::class, CharactersRouterDependency::class]
)
@ScreenScope
internal interface CharactersComponent {

    fun inject(fragment: CharactersFragment)
    fun inject(fragment: CharacterDetailsFragment)

    @Component.Builder
    interface Builder {
        fun deps(deps: CharactersDependencies): Builder
        fun navigatorDeps(deps: CharactersRouterDependency): Builder
        fun build(): CharactersComponent
    }
}


