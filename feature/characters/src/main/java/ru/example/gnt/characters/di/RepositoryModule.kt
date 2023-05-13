package ru.example.gnt.characters.di

import androidx.paging.ExperimentalPagingApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.example.gnt.characters.data.CharacterDetailsRepositoryImpl
import ru.example.gnt.characters.data.CharacterListRepositoryImpl
import ru.example.gnt.characters.domain.repository.CharacterDetailsRepository
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier


@Module
abstract class RepositoryModule {

    @OptIn(ExperimentalPagingApi::class)
    @Binds
    internal abstract fun bindListRepository(repositoryImpl: CharacterListRepositoryImpl): CharactersRepository


    @Binds
    internal abstract fun bindDetailsRepository(repositoryImpl: CharacterDetailsRepositoryImpl): CharacterDetailsRepository

}
