package ru.example.gnt.episodes.di.modules

import androidx.paging.ExperimentalPagingApi
import dagger.Binds
import dagger.Module
import ru.example.gnt.episodes.data.EpisodesRepositoryImpl
import ru.example.gnt.episodes.domain.repository.EpisodesRepository

@Module
internal abstract class RepositoryModule {
    @OptIn(ExperimentalPagingApi::class)
    @Binds
    internal abstract fun bindRepository(repository: EpisodesRepositoryImpl): EpisodesRepository
}
