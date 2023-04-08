package ru.example.gnt.rickandmorty

import android.app.Application
import ru.example.gnt.rickandmorty.di.app.AppComponent
import ru.example.gnt.rickandmorty.di.app.DaggerAppComponent
import javax.inject.Singleton

@Singleton
class App: Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .context(this)
            .build()
        appComponent.inject(this)
    }

}
