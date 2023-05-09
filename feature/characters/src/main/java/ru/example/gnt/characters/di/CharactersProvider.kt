package ru.example.gnt.characters.di.provider

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import ru.example.gnt.characters.di.CharactersDependencies
import ru.example.gnt.characters.di.DaggerCharactersComponent
import ru.example.gnt.characters.di.CharactersRouterDependency
import kotlin.properties.Delegates

interface CharactersDepsProvider {

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: CharactersDependencies

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val charactersRouterDependency: CharactersRouterDependency

    companion object : CharactersDepsProvider by CharactersDepsStore
}

object CharactersDepsStore : CharactersDepsProvider {
    override var deps: CharactersDependencies by Delegates.notNull()
    override var charactersRouterDependency: CharactersRouterDependency by Delegates.notNull()
}

internal class CharactersComponentViewModel : ViewModel() {
    val charactersComponent = DaggerCharactersComponent.builder()
        .deps(CharactersDepsProvider.deps)
        .navigatorDeps(CharactersDepsProvider.charactersRouterDependency)
        .build()
}
