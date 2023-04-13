package ru.example.gnt.characters.di

import ru.example.gnt.common.data.remote.service.CharacterService

interface CharactersDeps {
    val rickAndMortyApi: CharacterService
}
