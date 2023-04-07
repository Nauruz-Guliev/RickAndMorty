package ru.example.gnt.characters


interface CharactersRouter {
    fun openCharactersScreen()
    fun openCharacterDetails(characterId: Int)
    fun navigateBackToCharacters()
}
