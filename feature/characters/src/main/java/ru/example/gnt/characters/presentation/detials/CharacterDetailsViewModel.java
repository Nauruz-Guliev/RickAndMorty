package ru.example.gnt.characters.presentation.detials;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
import ru.example.gnt.characters.CharactersRouter;
import ru.example.gnt.characters.domain.usecases.GetCharacterById;
import ru.example.gnt.common.model.UiState;
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier;


public class CharacterDetailsViewModel extends ViewModel {

    private final MutableLiveData<UiState<?>> state = new MutableLiveData<UiState<?>>(UiState.Empty.INSTANCE);

    private Disposable disposable;
    private final CharactersRouter router;
    private final GetCharacterById getCharacterById;
    private final int characterId;
    private final Scheduler scheduler;


    @AssistedInject
    public CharacterDetailsViewModel(@RxIOSchedulerQualifier
                                     Scheduler scheduler,
                                     @Assisted
                                     int id,
                                     GetCharacterById characterById,
                                     CharactersRouter router) {
        state.setValue(UiState.Loading.INSTANCE);
        this.router = router;
        this.scheduler = scheduler;
        this.getCharacterById = characterById;
        this.characterId = id;
        loadData();
    }

    public void loadData() {
        disposable = getCharacterById.invoke(characterId)
                .subscribeOn(scheduler)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characterDetailsModel -> {
                            Log.d("RESPONSE_MODEL", characterDetailsModel.toString());
                            state.setValue(new UiState.Success<>(characterDetailsModel));
                        },
                        throwable -> {// onError
                            if (throwable instanceof CompositeException) {
                                ((CompositeException) throwable).getExceptions().forEach((error) -> {
                                    Log.e("ERROR", "onCompositeError: " + error.getLocalizedMessage() + " " + error.getClass());
                                });
                            }
                            Log.e("ERROR", "onError: " + throwable.getLocalizedMessage() + " " + throwable);
                            state.setValue(new UiState.Error(throwable));
                        }
                )
        ;
    }


    public void navigateToEpisodeDetails(int id) {
        router.navigateToEpisodeDetails(id);
    }

    public void navigateToLocationDetails(int id) {
        router.navigateToLocationDetails(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public MutableLiveData<UiState<?>> getState() {
        return state;
    }
}

