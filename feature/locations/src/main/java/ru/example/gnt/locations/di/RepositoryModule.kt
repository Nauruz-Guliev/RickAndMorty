package ru.example.gnt.locations.di

import androidx.paging.ExperimentalPagingApi
import dagger.Binds
import dagger.Module
import ru.example.gnt.locations.data.repository.LocationListRepositoryImpl
import ru.example.gnt.locations.domain.repository.LocationListRepository

@Module()
internal abstract class RepositoryModule {

    @OptIn(ExperimentalPagingApi::class)
    @Binds
    internal abstract fun bindRepository(repositoryImpl: LocationListRepositoryImpl): LocationListRepository
}
