package ru.example.gnt.locations.presentation.details

data class LocationDetailsModel(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>?,
    val url: String?,
    val created: String?,
)
