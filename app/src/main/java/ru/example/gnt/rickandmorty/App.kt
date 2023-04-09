package ru.example.gnt.rickandmorty

import android.app.Application
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.rickandmorty.di.app.AppComponent
import ru.example.gnt.rickandmorty.di.app.DaggerAppComponent
import javax.inject.Singleton

@Singleton
class App: Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .application(this)
            .context(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        CharactersDepsStore.deps = appComponent
    }

}
