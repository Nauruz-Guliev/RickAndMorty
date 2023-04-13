package ru.example.gnt.characters.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.ui.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository
import javax.inject.Inject

internal class GetAllCharactersUseCase  @Inject constructor(
    private val repository: CharactersRepository
){
    operator fun invoke() : Flow<PagingData<CharactersUiModel.Single>>{
        return repository.getCharacters()
    }
}
