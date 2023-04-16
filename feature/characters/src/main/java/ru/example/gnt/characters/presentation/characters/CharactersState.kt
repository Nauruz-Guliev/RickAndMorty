package ru.example.gnt.characters.presentation.characters

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import ru.example.gnt.common.recyclerview_delegate.AdapterDelegate

data class CharactersState(
    val filter: CharactersFilterModel = CharactersFilterModel(),
    val search: String? = null,
    val charactersFlow: Flow<PagingData<CharactersUiModel.Single>>? = null
)
