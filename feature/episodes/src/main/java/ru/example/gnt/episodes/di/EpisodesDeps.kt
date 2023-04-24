package ru.example.gnt.episodes.di

import android.content.Context
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.remote.service.EpisodeService

interface EpisodesDeps {
    val service: EpisodeService
    val dao: EpisodesDao
    val context: Context
}
