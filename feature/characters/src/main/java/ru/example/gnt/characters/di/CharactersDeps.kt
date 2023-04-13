package ru.example.gnt.characters.di

import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.remote.service.CharacterService

interface CharactersDeps {
    val characterService: CharacterService
    var characterDao: CharacterDao
}
