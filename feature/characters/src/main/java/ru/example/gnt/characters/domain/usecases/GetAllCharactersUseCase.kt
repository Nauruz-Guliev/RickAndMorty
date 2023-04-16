package ru.example.gnt.characters.domain.usecases

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.characters.CharactersFilterModel
import ru.example.gnt.common.model.filter.CharacterFilterModel
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

internal class GetAllCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    operator fun invoke(filterModel: CharactersFilterModel?): Flow<PagingData<CharactersUiModel.Single>> {
        return repository.getCharacters(filterModel)
    }
}
