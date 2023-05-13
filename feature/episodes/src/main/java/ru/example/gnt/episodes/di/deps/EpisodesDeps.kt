package ru.example.gnt.episodes.di.deps

import android.content.Context
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService

interface EpisodesDeps {
    val urlIdExtractor: UrlIdExtractor
    val episodeMapper: EpisodeEntityResponseMapper
    val episodeService: EpisodeService
    val episodesDao: EpisodesDao
    val characterService: CharacterService
    val charactersDao: CharactersDao
    val context: Context
}
