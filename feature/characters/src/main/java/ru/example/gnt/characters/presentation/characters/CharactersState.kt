package ru.example.gnt.characters.presentation.characters

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.ui.characters.CharactersUiModel

data class CharactersState(
    var filter: CharactersFilterModel = CharactersFilterModel(),
    val search: String? = null,
    val charactersFlow: Flow<PagingData<CharactersUiModel.Single>>? = null
)
