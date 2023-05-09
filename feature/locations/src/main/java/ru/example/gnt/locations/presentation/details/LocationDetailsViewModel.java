package ru.example.gnt.locations.presentation.details;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
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
                .subscribe(
                        locationDetailsModel -> {
                            Log.d("RESPONSE_MODEL", locationDetailsModel.toString());
                            state.setValue(new UiState.Success<>(locationDetailsModel));
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
