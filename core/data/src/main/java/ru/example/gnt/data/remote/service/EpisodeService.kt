package ru.example.gnt.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.example.gnt.data.remote.model.CharactersResponseModel
import ru.example.gnt.data.remote.model.EpisodesResponseModel

interface EpisodeService {

    @GET(EPISODE_END_POINT)
    fun getEpisodesByPageFiltered(
        @Query("page") page: String,
        @Query("name") name: String? = null,
        @Query("episode") species: String? = null,
    ): Call<EpisodesResponseModel>

    @GET("${EPISODE_END_POINT}/{id}")
    fun getEpisodeById(@Path("id") id: Int): Call<EpisodesResponseModel.Result>

    /**@param ids Notice that all ids should be separated by comma
     * example "episode/1,2,3,4,5,6,...,800" etc..
     */
    @GET("${EPISODE_END_POINT}/{ids}")
    fun getEpisodesInRange(@Path("ids") ids: String): Call<List<EpisodesResponseModel.Result>>

    @GET(EPISODE_END_POINT)
    fun getFilteredEpisodes(
        @Query("name") name: String? = null,
        @Query("episode") code: String? = null,
    ): Call<EpisodesResponseModel>

    companion object {
        const val EPISODE_END_POINT: String = "episode"
    }
}
