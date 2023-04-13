package ru.example.gnt.rickandmorty.di.app

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.example.gnt.characters.di.CharactersDeps
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.remote.service.CharacterService
import ru.example.gnt.common.di.local.DatabaseModule
import ru.example.gnt.common.di.remote.NetworkModule
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.rickandmorty.App

@Component(modules = [AppModule::class, NetworkModule::class, DatabaseModule::class])
@ApplicationScope
interface AppComponent : CharactersDeps {
    override val characterService: CharacterService
    override var characterDao: CharacterDao


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }


    fun inject(app: App)

}
