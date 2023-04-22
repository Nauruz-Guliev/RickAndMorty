package ru.example.gnt.common.di.local

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.example.gnt.common.data.local.RickAndMortyDatabase
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.di.scope.ApplicationScope

@Module
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideDatabase(application: Application): RickAndMortyDatabase {
        return Room.databaseBuilder(
            application,
            RickAndMortyDatabase::class.java,
            DB_NAME
        ).build()
    }


    @Provides
    @ApplicationScope
    fun provideLaunchesDao(database: RickAndMortyDatabase): CharacterDao {
        return database.getCharacterDao()
    }

    @Provides
    @ApplicationScope
    fun provideContext(app: Application): Context {
        return app
    }

    private companion object {
        const val DB_NAME = "rick_and_morty.db"
    }
}
