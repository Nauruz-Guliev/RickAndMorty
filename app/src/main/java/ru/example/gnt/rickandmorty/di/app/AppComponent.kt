package ru.example.gnt.rickandmorty.di.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.di.CharactersDependencies
import ru.example.gnt.common.di.deps.CommonModuleDeps
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.common.utils.*
import ru.example.gnt.data.di.local.DatabaseModule
import ru.example.gnt.data.di.remote.NetworkModule
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.data.remote.service.LocationService
import ru.example.gnt.episodes.di.deps.EpisodesDeps
import ru.example.gnt.locations.di.LocationDependencies
import ru.example.gnt.rickandmorty.App

@Component(modules = [UtilityModule::class, AppModule::class, NetworkModule::class, DatabaseModule::class])
@ApplicationScope
interface AppComponent : CommonModuleDeps, CharactersDependencies, EpisodesDeps, LocationDependencies {
    override val context: Context
    override val charactersDao: CharactersDao
    override val characterMapper: CharacterEntityResponseMapper
    override val episodesDao: EpisodesDao
    override val episodeService: EpisodeService
    override val characterService: CharacterService
    override val episodeMapper: EpisodeEntityResponseMapper
    override val urlIdExtractor: UrlIdExtractor
    override val locationMapper: LocationEntityResponseMapper
    override val locationService: LocationService
    override val locationsDao: LocationsDao
    override val apiListQueryGenerator: ApiListQueryGenerator
    override val commonUi: CommonUi
    override val errorHandler: ErrorHandler
    override val logger: AppLogger

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(application: App): Builder
        fun build(): AppComponent
    }


    fun inject(app: App)

}
