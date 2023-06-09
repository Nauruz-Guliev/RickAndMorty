package ru.example.gnt.characters.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.model.characters.CharacterListItem

interface CharactersRepository {
    /*
    suspend fun getAllCharacters(): Flow<Result<CharactersUiModel>>
    suspend fun getCharacterById(id: Int): Flow<Result<CharactersUiModel.Single>>
    suspend fun getMultipleCharacters(ids: Array<Int>): Flow<Result<List<CharactersUiModel.Single>>>
    suspend fun getFilteredCharacters(status: String?, gender: String?):  Flow<Result<CharactersUiModel>>

    */
    suspend fun getCharacters(filterModel: CharactersFilterModel) :  Flow<PagingData<CharacterListItem>>

}
