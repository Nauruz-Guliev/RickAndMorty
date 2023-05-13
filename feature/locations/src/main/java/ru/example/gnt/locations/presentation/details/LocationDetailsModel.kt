package ru.example.gnt.locations.presentation.details

import ru.example.gnt.common.model.characters.CharacterListItem

data class LocationDetailsModel(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    var residents: List<CharacterListItem>?,
    val url: String?,
    val created: String?,
)
