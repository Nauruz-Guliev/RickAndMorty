package ru.example.gnt.common.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.example.gnt.common.data.remote.model.Characters

interface CharacterService {
    @GET(CHARACTER_END_POINT)
    fun getCharactersByPageFiltered(
        @Query("page") page: String,
        @Query("name") name: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null,
        ): Call<Characters>

    @GET("$CHARACTER_END_POINT/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<Characters.Result>

    /**@param ids Notice that all ids should be separated by comma
     * example "character/1,2,3,4,5,6,...,800" etc..
     */
    @GET("$CHARACTER_END_POINT/{ids}")
    fun getCharactersInRange(@Path("ids") ids: String): Call<List<Characters.Result>>

    @GET(CHARACTER_END_POINT)
    fun getFilteredCharacters(
        @Query("name") name: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
    ) :  Call<Characters>

    companion object {
        const val CHARACTER_END_POINT: String = "character"
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
