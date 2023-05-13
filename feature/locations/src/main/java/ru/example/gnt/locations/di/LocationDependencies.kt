package ru.example.gnt.locations.di

import android.content.Context
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.LocationService

interface LocationDependencies {
    val locationService: LocationService
    val locationsDao: LocationsDao
    val characterService: CharacterService
    val charactersDao : CharactersDao
    val locationMapper: LocationEntityResponseMapper
    val context: Context
}
