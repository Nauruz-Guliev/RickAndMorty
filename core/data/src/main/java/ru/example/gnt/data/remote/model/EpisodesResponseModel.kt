package ru.example.gnt.data.remote.model


import com.squareup.moshi.Json

data class EpisodesResponseModel(
    val info: Info,
    val results: List<Result>
) {
    data class Info(
        val count: Int, // 51
        val next: String, // https://rickandmortyapi.com/api/episode?page=2
        val pages: Int, // 3
        val prev: Any? // null
    )

    data class Result(
        @Json(name = "air_date")
        val airDate: String, // December 2, 2013
        val characters: List<String>,
        val created: String, // 2017-11-10T12:56:33.798Z
        val episode: String, // S01E01
        val id: Int, // 1
        val name: String, // Pilot
        val url: String // https://rickandmortyapi.com/api/episode/1
    )
}
