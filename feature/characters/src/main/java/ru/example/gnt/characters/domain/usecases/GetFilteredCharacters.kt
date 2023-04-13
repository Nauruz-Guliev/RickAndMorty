package ru.example.gnt.characters.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.ui.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository
import javax.inject.Inject

internal class GetFilteredCharacters @Inject constructor(
    private val repository: CharactersRepository
) {
    /*
    suspend operator fun invoke(status: String?, gender: String?): Flow<Result<CharactersUiModel>> {
       // return repository.getFilteredCharacters(status, gender)
    }

     */
}
