package ru.example.gnt.characters.di

import android.content.Context
import ru.example.gnt.data.local.dao.CharacterDao
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService

interface CharactersDeps {
    val characterMapper: CharacterEntityResponseMapper
    val characterService: CharacterService
    val characterDao: CharacterDao
    val context: Context
}
