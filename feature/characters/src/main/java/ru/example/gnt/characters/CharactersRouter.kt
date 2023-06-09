package ru.example.gnt.characters

import android.widget.ImageView


interface CharactersRouter {
    fun navigateToCharacterDetails(id: Int)
    fun navigateToLocationDetails(id: Int)
    fun navigateToEpisodeDetails(id: Int?)
}
