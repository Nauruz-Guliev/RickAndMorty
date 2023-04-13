package ru.example.gnt.common.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.local.entity.CharacterEntity

@Database(
    version = 1,
    entities = [
        CharacterEntity::class
    ],
    exportSchema = false
)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun getCharacterDao(): CharacterDao
}
