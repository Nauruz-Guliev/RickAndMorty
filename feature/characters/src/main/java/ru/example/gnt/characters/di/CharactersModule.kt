package ru.example.gnt.characters.di

import dagger.Binds
import dagger.Module
import ru.example.gnt.characters.data.repository.CharactersRepositoryImpl
import ru.example.gnt.characters.domain.repository.CharactersRepository


@Module(
    includes = [
        ViewModelModule::class
    ]
)
abstract class CharactersModule {

    @Binds
    internal abstract fun bindRepository(repositoryImpl: CharactersRepositoryImpl): CharactersRepository
}
