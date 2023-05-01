package ru.example.gnt.characters.presentation.detials;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.example.gnt.characters.domain.usecases.GetCharacterById;
import ru.example.gnt.characters.presentation.list.model.CharactersUiModel;
import ru.example.gnt.common.model.UiState;


public class CharacterDetailsViewModel extends ViewModel {
    private final int id;
    private final GetCharacterById characterById;

    private final MutableLiveData<UiState<?>> state = new MutableLiveData(UiState.Empty.INSTANCE);


    @AssistedInject
    public CharacterDetailsViewModel(@Assisted int id, GetCharacterById characterById) {
        this.id = id;
        this.characterById = characterById;
        state.setValue(UiState.Loading.INSTANCE);
        characterById.invoke(id)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getCharacterObserver());
    }

    private Observer<CharactersUiModel.Single> getCharacterObserver() {
        return new Observer<>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}

            @Override
            public void onNext(CharactersUiModel.@NonNull Single single) {
                state.setValue(new UiState.Success(single));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                state.setValue(new UiState.Error(e));
            }

            @Override
            public void onComplete() {

            }
        };
    }


    public MutableLiveData<UiState<?>> getState() {
        return state;
    }
}

