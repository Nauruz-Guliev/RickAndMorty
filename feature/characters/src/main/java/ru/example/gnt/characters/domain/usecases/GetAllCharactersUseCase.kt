package ru.example.gnt.characters.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.characters.CharactersFilterModel
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

internal class GetAllCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    suspend operator fun invoke(filterModel: CharactersFilterModel): Flow<PagingData<CharactersUiModel.Single>> =
        repository.getCharacters(filterModel)

}
