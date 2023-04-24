package ru.example.gnt.characters

import android.widget.ImageView


interface CharactersRouter {
    fun openCharactersScreen()
    fun openCharacterDetails(id: Int)
    fun navigateBackToCharacters()
}
