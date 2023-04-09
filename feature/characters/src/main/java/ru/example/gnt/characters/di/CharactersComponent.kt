package ru.example.gnt.characters.di

import dagger.Component
import dagger.Subcomponent
import ru.example.gnt.characters.presentation.characters.CharactersFragment
import ru.example.gnt.characters.presentation.characters.detials.CharacterDetailsFragment
import ru.example.gnt.common.di.scope.ScreenScope


@Component(
    modules = [
        CharactersModule::class
    ],
    dependencies = [CharactersDeps::class, NavigatorDeps::class]
)
@ScreenScope
internal interface CharactersComponent {

    fun inject(fragment: CharactersFragment)
    fun inject(fragment: CharacterDetailsFragment)

    @Component.Builder
    interface Builder {
        fun deps(deps: CharactersDeps): Builder
        fun navigatorDeps(deps: NavigatorDeps): Builder
        fun build(): CharactersComponent
    }
}


