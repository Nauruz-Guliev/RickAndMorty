package ru.example.gnt.characters.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.example.gnt.characters.domain.repository.CharacterDetailsRepository
import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import javax.inject.Inject

internal class GetCharacterById @Inject constructor(
    private val repository: CharacterDetailsRepository
) {
    operator fun invoke(id: Int): Single<CharacterDetailsModel> {
        return repository.getCharacterById(id)
    }

}
