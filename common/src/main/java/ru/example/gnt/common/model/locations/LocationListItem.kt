package ru.example.gnt.common.model.locations

import ru.example.gnt.common.base.BaseClass

data class LocationListItem(
    override val id: Int,
    override val name: String,
    val type: String,
    val dimension: String
) : BaseClass(id, name)
