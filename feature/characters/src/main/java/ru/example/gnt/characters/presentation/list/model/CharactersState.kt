package ru.example.gnt.characters.presentation.list.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.characters.CharacterListItem

data class CharactersState(
    var filter: CharactersFilterModel = CharactersFilterModel.builder().build(),
    val search: String? = null,
    val charactersFlow: Flow<PagingData<CharacterListItem>>? = null
)
