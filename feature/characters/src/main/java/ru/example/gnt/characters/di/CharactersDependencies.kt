package ru.example.gnt.characters.di

import android.content.Context
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.data.remote.service.LocationService

interface CharactersDependencies {
    val characterMapper: CharacterEntityResponseMapper
    val characterService: CharacterService
    val locationService: LocationService
    val episodeService: EpisodeService
    val episodesDao: EpisodesDao
    val apiListQueryGenerator: ApiListQueryGenerator
    val locationsDao: LocationsDao
    val urlIdExtractor: UrlIdExtractor
    val charactersDao: CharactersDao
    val context: Context
}
