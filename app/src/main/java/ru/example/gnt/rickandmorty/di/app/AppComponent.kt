package ru.example.gnt.rickandmorty.di.app

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.di.CharactersDeps
import ru.example.gnt.common.data.remote.service.CharacterService
import ru.example.gnt.common.di.CommonModuleDeps
import ru.example.gnt.common.di.remote.NetworkModule
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.rickandmorty.App

@Component(modules = [AppModule::class, NetworkModule::class])
@ApplicationScope
interface AppComponent : CharactersDeps, CommonModuleDeps{
    override val rickAndMortyApi: CharacterService
    override val context: Context
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
