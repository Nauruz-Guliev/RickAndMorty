package ru.example.gnt.rickandmorty.di.main

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.rickandmorty.di.qualifiers.MainContainerId
import ru.example.gnt.rickandmorty.navigation.Navigator


@Module
class MainModule {
    @Provides
    @ScreenScope
    fun provideCharacterRouter(navigator: Navigator) : CharactersRouter {
        return navigator
    }
}
