package ru.example.gnt.rickandmorty.di.app

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.di.CharactersDeps
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.local.DatabaseModule
import ru.example.gnt.data.di.remote.NetworkModule
import ru.example.gnt.data.local.dao.CharacterDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.di.deps.EpisodesDeps
import ru.example.gnt.rickandmorty.App

@Component(modules = [AppModule::class, NetworkModule::class, DatabaseModule::class])
@ApplicationScope
interface AppComponent : CharactersDeps, EpisodesDeps {
    //characters dependecies
    override val characterMapper: CharacterEntityResponseMapper
    override val characterService: CharacterService
    override var characterDao: CharacterDao

    //episode dependencies
    override val episodeDao: EpisodesDao
    override val episodeMapper: EpisodeEntityResponseMapper
    override val episodeService: EpisodeService

    //common dependencies
    override val urlIdExtractor: UrlIdExtractor
    override val context: Context


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(application: App): Builder
        fun build(): AppComponent
    }


    fun inject(app: App)

}
