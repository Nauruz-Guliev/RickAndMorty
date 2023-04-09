package ru.example.gnt.rickandmorty.di.app

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import ru.example.gnt.characters.di.CharactersDeps
import ru.example.gnt.common.data.remote.RickAndMortyApi
import ru.example.gnt.common.di.NetworkModule
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.rickandmorty.App
import ru.example.gnt.rickandmorty.di.main.MainModule
import javax.inject.Inject

@Component(modules = [AppModule::class, NetworkModule::class])
@ApplicationScope
interface AppComponent : CharactersDeps{
    override val rickAndMortyApi: RickAndMortyApi

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(application: Context): Builder
        fun build(): AppComponent
    }

    fun inject(app: App)

}
