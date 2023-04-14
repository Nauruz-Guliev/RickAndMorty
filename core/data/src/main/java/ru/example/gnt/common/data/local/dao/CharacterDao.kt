package ru.example.gnt.common.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.data.local.entity.CharacterEntity

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacters(characters: List<CharacterEntity>)

    @Query("SELECT * FROM character WHERE id= :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("DELETE FROM character WHERE id=:id")
    suspend fun deleteCharacterById(id: Int): Int

    @Query("SELECT * FROM character")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM character")
    fun getCharacters() : PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM character LIMIT :pageSize OFFSET :pageIndex")
    suspend fun getCharactersInRage(pageSize: Int, pageIndex: Int) : List<CharacterEntity>

    @Query("DELETE FROM character")
    suspend fun clear()

    @Transaction
    suspend fun refresh(characters: List<CharacterEntity>) {
        saveCharacters(characters)
    }

    suspend fun save(character: CharacterEntity) {
        saveCharacters(listOf(character))
    }
}
