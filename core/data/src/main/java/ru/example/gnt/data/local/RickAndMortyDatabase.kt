package ru.example.gnt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.example.gnt.data.local.converters.Converters
import ru.example.gnt.data.local.dao.CharacterDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.local.entity.CharacterEntity
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.local.entity.LocationEntity

@Database(
    version = 2,
    entities = [
        CharacterEntity::class,
        EpisodeEntity::class,
        LocationEntity::class
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEpisodeDao(): EpisodesDao
    abstract fun getLocationDao(): LocationsDao
}
