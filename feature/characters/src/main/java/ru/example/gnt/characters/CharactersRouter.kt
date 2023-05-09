package ru.example.gnt.characters

import android.widget.ImageView


interface CharactersRouter {
    fun openCharactersScreen()
    fun navigateToCharacterDetails(id: Int)
    fun navigateToLocationDetails(id: Int?)
    fun navigateToEpisodeDetails(id: Int?)
    fun navigateBackToCharacters()
}
