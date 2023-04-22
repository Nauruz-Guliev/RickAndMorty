package ru.example.gnt.characters.di

import android.content.Context
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.remote.service.CharacterService

interface CharactersDeps {
    val characterService: CharacterService
    val characterDao: CharacterDao
    val context: Context
}
