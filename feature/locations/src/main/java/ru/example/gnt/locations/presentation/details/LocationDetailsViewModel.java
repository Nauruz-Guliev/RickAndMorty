package ru.example.gnt.locations.presentation.details;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.example.gnt.common.exceptions.ApplicationException;
import ru.example.gnt.common.model.UiState;
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier;
import ru.example.gnt.locations.LocationsRouter;
import ru.example.gnt.locations.domain.usecases.GetLocationDetailsUseCase;

public class LocationDetailsViewModel extends ViewModel {

    private final MutableLiveData<UiState<?>> state = new MutableLiveData<UiState<?>>(UiState.Empty.INSTANCE);

    private Disposable disposable;
    private LocationsRouter router;
    private GetLocationDetailsUseCase locationDetailsUseCase;
    private int locationID;
    private Scheduler scheduler;

    @AssistedInject
    public LocationDetailsViewModel(@RxIOSchedulerQualifier
                                     Scheduler scheduler,
                                     @Assisted
                                     int id,
                                    GetLocationDetailsUseCase locationById,
                                    LocationsRouter router) {
        state.setValue(UiState.Loading.INSTANCE);
        this.router = router;
        this.scheduler = scheduler;
        this.locationDetailsUseCase = locationById;
        this.locationID = id;
        loadData();
    }
    public void loadData() {
        disposable = locationDetailsUseCase.invoke(locationID)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> disposable.dispose())
                .doOnError(throwable -> {
                    state.setValue(new UiState.Error(throwable));
                })
                .doOnNext(locationDetailsModel -> {
                    state.setValue(new UiState.SuccessRemote<>(locationDetailsModel));
                })
                .subscribe(
                        locationDetailsModel -> {
                            state.setValue(new UiState.SuccessRemote<>(locationDetailsModel));
                        },
                        throwable -> {
                            Log.e("ONERROR_LOCATION", throwable.getLocalizedMessage(), throwable);
                            if(throwable instanceof ApplicationException.LocalDataException) {
                                state.setValue(new UiState.SuccessCached<>(((ApplicationException.LocalDataException) throwable).getData()));
                            } else  {
                                state.setValue(new UiState.Error(throwable));
                            }
                        }
                )
        ;
    }

    public MutableLiveData<UiState<?>> getState() {
        return state;
    }

    public void navigateToCharacterDetails(int id) {
        router.navigateToCharacterDetails(id);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
