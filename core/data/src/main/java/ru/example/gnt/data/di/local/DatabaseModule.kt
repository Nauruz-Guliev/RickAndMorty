package ru.example.gnt.data.di.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.example.gnt.data.local.RickAndMortyDatabase
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao

@Module
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideDatabase(context: Context): RickAndMortyDatabase {
        return Room.databaseBuilder(
            context,
            RickAndMortyDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @ApplicationScope
    fun provideLaunchesDao(database: RickAndMortyDatabase): CharactersDao {
        return database.getCharacterDao()
    }

    @Provides
    @ApplicationScope
    fun provideEpisodesDao(database: RickAndMortyDatabase) : EpisodesDao {
        return database.getEpisodeDao()
    }

    @Provides
    @ApplicationScope
    fun provideLocationsDao(database: RickAndMortyDatabase) : LocationsDao {
        return database.getLocationDao()
    }

    private companion object {
        const val DB_NAME = "rick_and_morty.db"
    }
}
