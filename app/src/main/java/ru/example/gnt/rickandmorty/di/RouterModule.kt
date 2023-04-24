package ru.example.gnt.rickandmorty.di

import dagger.Binds
import dagger.Module
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.rickandmorty.navigation.MainRouter

@Module
abstract class RouterModule {
    @Binds
    abstract fun provideCharacterRouter(mainRouter: MainRouter) : CharactersRouter
}