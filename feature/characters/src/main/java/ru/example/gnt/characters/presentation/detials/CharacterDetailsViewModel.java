package ru.example.gnt.characters.presentation.detials;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.example.gnt.characters.CharactersRouter;
import ru.example.gnt.characters.domain.usecases.GetCharacterById;
import ru.example.gnt.common.model.UiState;
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier;


public class CharacterDetailsViewModel extends ViewModel {

    private final MutableLiveData<UiState<?>> state = new MutableLiveData<UiState<?>>(UiState.Empty.INSTANCE);

    private Disposable disposable;
    private CharactersRouter router;

    @AssistedInject
    public CharacterDetailsViewModel(@RxIOSchedulerQualifier
                                     Scheduler scheduler,
                                     @Assisted
                                     int id,
                                     GetCharacterById characterById,
                                     CharactersRouter router) {
        state.setValue(UiState.Loading.INSTANCE);
        this.router = router;
        disposable = characterById.invoke(id)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characterDetailsModel -> {
                            Log.d("RESPONSE_MODEL", characterDetailsModel.toString());
                            state.setValue(new UiState.Success<>(characterDetailsModel));
                        },
                        throwable -> {// onError
                            Log.e("ERROR", "onError: " + throwable);
                            state.setValue(new UiState.Error(throwable));
                        }
                )
        ;
    }

    public void itemClicked(int id) {
        router.navigateToEpisodeDetails(id);
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

