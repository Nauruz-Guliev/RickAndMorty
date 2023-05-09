package ru.example.gnt.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.data.local.entity.LocationEntity

@Dao
interface LocationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocations(locations: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocationBlocking(locations: List<LocationEntity>)

    @Query("SELECT * FROM location WHERE id= :id")
    fun getLocationById(id: Int): Single<LocationEntity>

    @Query("DELETE FROM location WHERE id=:id")
    suspend fun deleteLocationById(id: Int): Int

    @Query("SELECT * FROM location")
    fun getAllLocations(): Flow<List<LocationEntity>>
    @Query("SELECT * FROM location WHERE id IN (:ids)")
    fun getLocations(ids: List<String>): List<LocationEntity>

    @Query("SELECT * FROM location")
    fun getLocationsPaged(): PagingSource<Int, LocationEntity>

    @Query("SELECT * FROM location LIMIT :pageSize OFFSET :pageIndex")
    suspend fun getLocationsInRange(pageSize: Int, pageIndex: Int): List<LocationEntity>


    @Query(
        "DELETE FROM location " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:dimension IS NULL OR dimension LIKE '%' || :dimension  || '%') "
    )
    suspend fun clear(
        name: String?,
        type: String?,
        dimension: String?,
    )

    @Transaction
    suspend fun refresh(
        locations: List<LocationEntity>,
        name: String?,
        type: String?,
        dimension: String?,
    ) {
        clear(
            name, type, dimension
        )
        saveLocations(locations)
    }

    @Query(
        "SELECT * FROM location " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:dimension IS NULL OR dimension LIKE '%' || :dimension  || '%') " +
                "ORDER BY id ASC"
    )
    fun getLocationsFilteredAndPaged(
        name: String?,
        type: String?,
        dimension: String?,
    ): PagingSource<Int, LocationEntity>


    @Query(
        "SELECT * FROM location " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:dimension IS NULL OR dimension LIKE '%' || :dimension  || '%') " +
                "ORDER BY id ASC " +
                "LIMIT :limit " +
                "OFFSET :offset "

    )
    fun getLocationsFiltered(
        name: String?,
        type: String?,
        dimension: String?,
        limit: Int?,
        offset: Int?
    ): List<LocationEntity>


    suspend fun save(location: LocationEntity) {
        saveLocations(listOf(location))
    }
}
