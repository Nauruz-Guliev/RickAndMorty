package ru.example.gnt.characters.di

import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.common.data.remote.RickAndMortyApi

interface CharactersDeps {
    val rickAndMortyApi: RickAndMortyApi
}
