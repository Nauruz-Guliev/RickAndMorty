package ru.example.gnt.characters.domain.usecases

import io.reactivex.rxjava3.core.Observable
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

internal class GetCharacterById @Inject constructor(
    private val repository: CharactersRepository
) {

    operator fun invoke(id: Int): Observable<CharactersUiModel.Single> {
        return repository.getCharacterById(id)
    }

}
