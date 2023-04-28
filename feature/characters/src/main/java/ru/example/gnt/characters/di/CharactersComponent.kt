package ru.example.gnt.characters.di

import dagger.Component
import ru.example.gnt.characters.presentation.list.CharactersFragment
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.common.di.scope.ScreenScope


@Component(
    modules = [
        CharactersModule::class
    ],
    dependencies = [CharactersDeps::class, CharactersRouterDeps::class]
)
@ScreenScope
internal interface CharactersComponent {

    fun inject(fragment: CharactersFragment)
    fun inject(fragment: CharacterDetailsFragment)

    @Component.Builder
    interface Builder {
        fun deps(deps: CharactersDeps): Builder
        fun navigatorDeps(deps: CharactersRouterDeps): Builder
        fun build(): CharactersComponent
    }
}


