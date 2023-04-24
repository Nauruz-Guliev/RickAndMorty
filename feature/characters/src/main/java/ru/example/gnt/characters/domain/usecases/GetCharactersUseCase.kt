package ru.example.gnt.characters.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

internal class GetCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    suspend operator fun invoke(filterModel: CharactersFilterModel): Flow<PagingData<CharactersUiModel.Single>> =
        repository.getCharacters(filterModel)
}
