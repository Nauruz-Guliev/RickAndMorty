package ru.example.gnt.characters.di.provider

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import ru.example.gnt.characters.di.CharactersDeps
import ru.example.gnt.characters.di.DaggerCharactersComponent
import ru.example.gnt.characters.di.NavigatorDeps
import kotlin.properties.Delegates

interface CharactersDepsProvider {

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: CharactersDeps

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val navigatorDeps: NavigatorDeps

    companion object : CharactersDepsProvider by CharactersDepsStore
}

object CharactersDepsStore : CharactersDepsProvider {
    override var deps: CharactersDeps by Delegates.notNull()
    override var navigatorDeps: NavigatorDeps by Delegates.notNull()
}

internal class CharactersComponentViewModel : ViewModel() {
    val charactersComponent = DaggerCharactersComponent.builder()
        .deps(CharactersDepsProvider.deps)
        .navigatorDeps(CharactersDepsProvider.navigatorDeps)
        .build()
}
