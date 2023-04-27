package ru.example.gnt.episodes.di.modules

import dagger.Binds
import dagger.Module
import ru.example.gnt.episodes.data.EpisodesRepositoryImpl
import ru.example.gnt.episodes.domain.repository.EpisodesRepository

@Module
abstract class RepositoryModule {
    @Binds
    internal abstract fun bindRepository(repository: EpisodesRepositoryImpl): EpisodesRepository
}
