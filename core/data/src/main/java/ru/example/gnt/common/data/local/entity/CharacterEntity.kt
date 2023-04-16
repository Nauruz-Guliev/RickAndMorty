package ru.example.gnt.common.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey
import ru.example.gnt.common.data.remote.model.Characters


@Entity(tableName = "character")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val created: String,
    val gender: String,
    val image: String,
    val name: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)