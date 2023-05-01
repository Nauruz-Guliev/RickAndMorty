package ru.example.gnt.episodes.di.modules

import androidx.paging.ExperimentalPagingApi
import dagger.Binds
import dagger.Module
import ru.example.gnt.episodes.data.EpisodeDetailsRepositoryImpl
import ru.example.gnt.episodes.data.EpisodeListRepositoryImpl
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import ru.example.gnt.episodes.domain.repository.EpisodeListRepository

@Module
internal abstract class RepositoryModule {
    @OptIn(ExperimentalPagingApi::class)
    @Binds
    internal abstract fun bindEpisodeListRepository(repository: EpisodeListRepositoryImpl): EpisodeListRepository

    @Binds
    internal abstract fun bindEpisodeDetailsRepository(repository: EpisodeDetailsRepositoryImpl): EpisodeDetailsRepository
}
