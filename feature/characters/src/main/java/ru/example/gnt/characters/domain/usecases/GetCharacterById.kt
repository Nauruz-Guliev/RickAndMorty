package ru.example.gnt.characters.domain.usecases

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import ru.example.gnt.characters.domain.repository.CharacterDetailsRepository
import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier
import javax.inject.Inject

internal class GetCharacterById @Inject constructor(
    private val repository: CharacterDetailsRepository,
    @RxIOSchedulerQualifier
    private val scheduler: Scheduler
) {
    operator fun invoke(id: Int): Observable<CharacterDetailsModel> {
        return repository.getCharacterById(id).subscribeOn(scheduler)
    }

}
