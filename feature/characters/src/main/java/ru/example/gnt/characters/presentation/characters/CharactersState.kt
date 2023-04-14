package ru.example.gnt.characters.presentation.characters

data class CharactersState (
   val filter: CharactersFilterModel? = null,
   val search: String? = null
)