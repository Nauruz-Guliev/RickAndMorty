package ru.example.gnt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val created: String?,
    val dimension: String,
    val name: String,
    val residents: List<String>?,
    val url: String?,
    val type: String,
)
