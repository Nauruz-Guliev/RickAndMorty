package ru.example.gnt.rickandmorty.di.activity

import dagger.Binds
import dagger.Module
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.locations.LocationsRouter
import ru.example.gnt.rickandmorty.navigation.MainRouter

@Module
abstract class RouterModule {
    @Binds
    abstract fun bindCharacterRouter(mainRouter: MainRouter): CharactersRouter

    @Binds
    abstract fun bindEpisodeRouter(mainRouter: MainRouter): EpisodesRouter

    @Binds
    abstract fun bindLocationRouter(mainRouter: MainRouter): LocationsRouter
}
