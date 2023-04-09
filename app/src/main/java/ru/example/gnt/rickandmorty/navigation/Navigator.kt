package ru.example.gnt.rickandmorty.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.presentation.characters.CharactersFragment
import ru.example.gnt.characters.presentation.characters.detials.CharacterDetailsFragment
import ru.example.gnt.common.di.scope.ScreenScope
import javax.inject.Inject


@ScreenScope
class Navigator @Inject constructor(
    private val fragmentManager: FragmentManager,
    @IdRes private val mainContainerId: Int,
) : CharactersRouter {

    override fun openCharactersScreen() {
        fragmentManager.beginTransaction().replace(
            mainContainerId,
            CharactersFragment.createInstance(),
            CharactersFragment.CHARACTERS_FRAGMENT_TAG
        ).addToBackStack(CharactersFragment.CHARACTERS_FRAGMENT_TAG)
            .commit()
    }

    override fun openCharacterDetails(characterId: Int) {
        fragmentManager.beginTransaction().replace(
            mainContainerId,
            CharacterDetailsFragment.createInstance(characterId),
            CharacterDetailsFragment.CHARACTER_DETAILS_FRAGMENT_TAG,
        ).addToBackStack(CharacterDetailsFragment.CHARACTER_DETAILS_FRAGMENT_TAG)
            .commit()
    }

    override fun navigateBackToCharacters() {
        fragmentManager.popBackStack(
            CharactersFragment.CHARACTERS_FRAGMENT_TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}
