package ru.example.gnt.common.model.episodes

import ru.example.gnt.common.base.BaseClass

data class EpisodeListItem(
    override val id: Int,
    override val name: String,
    val episode: String,
    val airDate: String,
) : BaseClass(id, name)

/*
 Название эпизода
(name), номер эпизода (episode) и дату релиза (air_date).
 */
