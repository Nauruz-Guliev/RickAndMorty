package ru.example.gnt.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.data.local.entity.CharacterEntity

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
    fun getCharacters(): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM character LIMIT :pageSize OFFSET :pageIndex")
    suspend fun getCharactersInRage(pageSize: Int, pageIndex: Int): List<CharacterEntity>


    @Query(
        "DELETE FROM character " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:species IS NULL OR species LIKE '%' || :species  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:gender IS NULL OR gender LIKE :gender) " +
                "AND (:status IS NULL OR status LIKE :status) "
    )
    suspend fun clear(
        name: String?,
        species: String?,
        type: String?,
        status: String?,
        gender: String?
    )

    @Transaction
    suspend fun refresh(
        characters: List<CharacterEntity>,
        name: String?,
        species: String?,
        type: String?,
        gender: String?,
        status: String?,
    ) {
        clear(name, species, type, status = status, gender = gender)
        saveCharacters(characters)
    }

    @Query(
        "SELECT * FROM character " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:species IS NULL OR species LIKE '%' || :species  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:gender IS NULL OR gender LIKE :gender) " +
                "AND (:status IS NULL OR status LIKE :status) " +
                "ORDER BY id ASC"
    )
    fun getCharactersFilteredPaged(
        name: String?,
        species: String?,
        type: String?,
        status: String?,
        gender: String?
    ): PagingSource<Int, CharacterEntity>


    @Query(
        "SELECT * FROM character " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:species IS NULL OR species LIKE '%' || :species  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:gender IS NULL OR gender LIKE :gender) " +
                "AND (:status IS NULL OR status LIKE :status) " +
                "ORDER BY id ASC " +
                "LIMIT :limit " +
                "OFFSET :offset "

    )
    fun getCharactersFiltered(
        name: String?,
        species: String?,
        type: String?,
        gender: String?,
        status: String?,
        limit: Int?,
        offset: Int?
    ): List<CharacterEntity>


    suspend fun save(character: CharacterEntity) {
        saveCharacters(listOf(character))
    }
}
