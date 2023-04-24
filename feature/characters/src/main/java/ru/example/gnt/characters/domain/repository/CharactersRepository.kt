package ru.example.gnt.characters.domain.repository

import androidx.paging.PagingData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.characters.presentation.list.model.CharactersUiModel

interface CharactersRepository {
    /*
    suspend fun getAllCharacters(): Flow<Result<CharactersUiModel>>
    suspend fun getCharacterById(id: Int): Flow<Result<CharactersUiModel.Single>>
    suspend fun getMultipleCharacters(ids: Array<Int>): Flow<Result<List<CharactersUiModel.Single>>>
    suspend fun getFilteredCharacters(status: String?, gender: String?):  Flow<Result<CharactersUiModel>>

    */
    fun getCharacterById(id: Int) : Observable<CharactersUiModel.Single>
    suspend fun getCharacters(filterModel: CharactersFilterModel) :  Flow<PagingData<CharactersUiModel.Single>>
}
