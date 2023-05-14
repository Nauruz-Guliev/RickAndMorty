package ru.example.gnt.data.remote.model


import com.squareup.moshi.Json
data class EpisodesResponseModel(
    @Json(name = "info")
    val info: Info? = null,
    @Json(name = "results")
    var results: List<Result>
)  {
    data class Info(
        @Json(name = "count")
        val count: Int?, // 51
        @Json(name = "next")
        val next: String?, // https://rickandmortyapi.com/api/episode?page=2
        @Json(name = "pages")
        val pages: Int?, // 3
        @Json(name = "prev")
        val prev: String? // null
    )

    data class Result(
        @Json(name = "air_date")
        val airDate: String, // December 2, 2013
        @Json(name = "characters")
        val characters: List<String>? = null,
        @Json(name = "created")
        val created: String? = null, // 2017-11-10T12:56:33.798Z
        @Json(name = "episode")
        val episode: String, // S01E01
        @Json(name = "id")
        val id: Int, // 1
        @Json(name = "name")
        val name: String, // Pilot
        @Json(name = "url")
        val url: String? = null  // https://rickandmortyapi.com/api/episode/1
    )
}
