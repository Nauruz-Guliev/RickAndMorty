package ru.example.gnt.characters.domain.repository

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.common.model.episodes.EpisodeListItem

interface CharacterDetailsRepository {

    fun getCharacterById(id: Int) : Single<CharacterDetailsModel>

    fun getEpisodeList(ids: List<String>) : List<EpisodeListItem>

}
