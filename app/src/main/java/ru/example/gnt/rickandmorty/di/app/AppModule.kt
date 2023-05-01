package ru.example.gnt.rickandmorty.di.app

import android.content.Context
import dagger.Binds
import dagger.Module
import ru.example.gnt.rickandmorty.App

@Module
abstract class AppModule {
    @Binds
    abstract fun provideContext(app: App): Context

}
