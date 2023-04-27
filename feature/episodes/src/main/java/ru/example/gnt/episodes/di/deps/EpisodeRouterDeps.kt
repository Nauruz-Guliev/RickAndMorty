package ru.example.gnt.episodes.di.deps

import android.content.Context
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.EpisodesRouter

interface EpisodeRouterDeps {
    val router: EpisodesRouter
}
