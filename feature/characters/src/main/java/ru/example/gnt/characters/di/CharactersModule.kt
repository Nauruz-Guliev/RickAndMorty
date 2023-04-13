package ru.example.gnt.characters.di

import androidx.paging.ExperimentalPagingApi
import dagger.Binds
import dagger.Module
import ru.example.gnt.characters.data.CharactersRepositoryImpl
import ru.example.gnt.characters.domain.repository.CharactersRepository


@Module(
    includes = [
        ViewModelModule::class
    ]
)
abstract class CharactersModule {

    @OptIn(ExperimentalPagingApi::class)
    @Binds
    internal abstract fun bindRepository(repositoryImpl: CharactersRepositoryImpl): CharactersRepository
}
