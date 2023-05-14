package ru.example.gnt.data.remote.service

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.data.remote.model.LocationsResponseModel

interface LocationService {

    @GET(LOCATION_END_POINT)
    suspend fun getLocationsByPageFiltered(
        @Query("page") page: String,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null,
    ): LocationsResponseModel

    @GET("${LOCATION_END_POINT}/{id}")
    fun getLocationById(@Path("id") id: Int): Single<LocationsResponseModel.Result>


    /**@param ids Notice that all ids should be separated by comma
     * example "location/1,2,3,4,5,6,...,800" etc..
     */
    @GET("${LOCATION_END_POINT}/{ids}")
    fun getLocationsInRange(@Path("ids") ids: String): Call<List<LocationsResponseModel.Result>>

    @GET(LOCATION_END_POINT)
    fun getFilteredLocations(
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null,
    ): Call<LocationsResponseModel>

    @GET("${LOCATION_END_POINT}/{id}")
    fun getLocation(@Path("id") id: Int): Call<LocationsResponseModel.Result>

    companion object {
        const val LOCATION_END_POINT: String = "location"
    }
}
