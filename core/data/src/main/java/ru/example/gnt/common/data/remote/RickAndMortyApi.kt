package ru.example.gnt.common.data.remote

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.example.gnt.common.data.Characters

interface RickAndMortyApi {


    @GET(CHARACTER_END_POINT)
    fun getAllCharacters(): Call<Characters>

    @GET("$CHARACTER_END_POINT/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<Characters.Result>

    @GET("$CHARACTER_END_POINT/{array}")
    fun getCharacters(@Path("array") array: Array<String>): Call<List<Characters.Result>>

    @GET(CHARACTER_END_POINT)
    fun getFilteredCharacters(
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    )

    companion object {
        const val CHARACTER_END_POINT: String = "character"
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
