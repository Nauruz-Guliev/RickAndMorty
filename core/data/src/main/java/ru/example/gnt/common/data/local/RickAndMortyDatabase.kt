package ru.example.gnt.common.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.example.gnt.common.data.local.converters.Converters
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.local.entity.CharacterEntity

@Database(
    version = 1,
    entities = [
        CharacterEntity::class
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun getCharacterDao(): CharacterDao
}
