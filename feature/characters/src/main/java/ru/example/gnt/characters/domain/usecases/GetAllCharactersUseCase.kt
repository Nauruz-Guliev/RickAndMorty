package ru.example.gnt.characters.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository
import javax.inject.Inject

internal class GetAllCharactersUseCase  @Inject constructor(
    private val repository: CharactersRepository
){

    suspend operator fun invoke() : Flow<Result<CharactersUiModel>>{
        return repository.getAllCharacters()
    }
}
