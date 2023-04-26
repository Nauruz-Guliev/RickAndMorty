package ru.example.gnt.data.remote.model


import com.squareup.moshi.Json

data class LocationsResponseModel(
    @Json(name = "info")
    val info: Info,
    @Json(name = "results")
    val results: List<Result?>
) {
    data class Info(
        @Json(name = "count")
        val count: Int, // 126
        @Json(name = "next")
        val next: String?, // https://rickandmortyapi.com/api/location?page=2
        @Json(name = "pages")
        val pages: Int, // 7
        @Json(name = "prev")
        val prev: Any? // null
    )

    data class Result(
        @Json(name = "created")
        val created: String, // 2017-11-10T12:42:04.162Z
        @Json(name = "dimension")
        val dimension: String, // Dimension C-137
        @Json(name = "id")
        val id: Int, // 1
        @Json(name = "name")
        val name: String, // Earth (C-137)
        @Json(name = "residents")
        val residents: List<String>,
        @Json(name = "type")
        val type: String, // Planet
        @Json(name = "url")
        val url: String // https://rickandmortyapi.com/api/location/1
    )
}
