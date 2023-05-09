package ru.example.gnt.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.data.local.entity.CharacterEntity
import ru.example.gnt.data.local.entity.EpisodeEntity

@Dao
interface EpisodesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEpisodes(episodes: List<EpisodeEntity>)

    @Query("SELECT * FROM episode WHERE id= :id")
    suspend fun getEpisodeById(id: Int): EpisodeEntity?

    @Query("DELETE FROM episode WHERE id=:id")
    suspend fun deleteEpisodeById(id: Int): Int

    @Query("SELECT * FROM episode")
    fun getAllEpisodes(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episode")
    fun getEpisodesPaged(): PagingSource<Int, EpisodeEntity>

    @Query("SELECT * FROM episode WHERE id IN (:ids)")
    suspend fun getEpisodes(ids: List<String>): List<EpisodeEntity>

    @Query("SELECT * FROM episode LIMIT :pageSize OFFSET :pageIndex")
    suspend fun getEpisodesInRage(pageSize: Int, pageIndex: Int): List<EpisodeEntity>


    @Query(
        "DELETE FROM episode " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:episode IS NULL OR episode LIKE '%' || :episode  || '%')"
    )
    suspend fun clear(
        name: String?,
        episode: String?,
    )

    @Transaction
    suspend fun refresh(
        episodes: List<EpisodeEntity>,
        name: String?,
        episode: String?,
    ) {
        clear(name, episode)
        saveEpisodes(episodes)
    }

    @Query(
        "SELECT * FROM episode " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:episode IS NULL OR episode LIKE '%' || :episode  || '%') " +
                "ORDER BY id ASC"
    )
    fun getEpisodesFilteredAndPaged(
        name: String?,
        episode: String?,
    ): PagingSource<Int, EpisodeEntity>


    @Query(
        "SELECT * FROM episode " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:episode IS NULL OR episode LIKE '%' || :episode  || '%') " +
                "ORDER BY id ASC " +
                "LIMIT :limit " +
                "OFFSET :offset "

    )
    fun getEpisodesFiltered(
        name: String?,
        episode: String?,
        limit: Int?,
        offset: Int?
    ): List<EpisodeEntity>


    suspend fun save(episode: EpisodeEntity) {
        saveEpisodes(listOf(episode))
    }
}
