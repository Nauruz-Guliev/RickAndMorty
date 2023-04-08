package ru.example.gnt.characters.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.domain.model.CharactersUiModel

internal interface CharactersRepository {
    fun getAllCharacters() : Flow<Result<CharactersUiModel>>
    fun getCharacterById(id : Int) : Flow<Result<CharactersUiModel.Single>>
    fun getMultipleCharacters(ids: Array<Int>): Flow<Result<List<CharactersUiModel.Single>>>
}
