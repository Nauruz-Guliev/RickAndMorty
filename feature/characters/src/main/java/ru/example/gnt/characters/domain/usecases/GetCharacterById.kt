package ru.example.gnt.characters.domain.usecases

import ru.example.gnt.characters.domain.repository.CharactersRepository
import javax.inject.Inject

internal class GetCharacterById @Inject constructor(
    private val repository: CharactersRepository
) {
    /*
    suspend operator fun invoke(id: Int): Flow<Result<CharactersUiModel.Single>> {
        return repository.getCharacterById(id)
    }

     */
}
