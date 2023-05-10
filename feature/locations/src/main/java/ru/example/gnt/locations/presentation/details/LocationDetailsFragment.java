package ru.example.gnt.locations.presentation.details;

import static ru.example.gnt.common.UtilityExtensionsKt.isNetworkOn;
import static ru.example.gnt.common.utils.extensions.UiExtensionsKt.showToastShort;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import ru.example.gnt.common.base.interfaces.DetailsFragment;
import ru.example.gnt.common.model.UiState;
import ru.example.gnt.locations.R;
import ru.example.gnt.locations.databinding.LocationDetailsFragmentBinding;
import ru.example.gnt.locations.di.LocationsComponentViewModel;
import ru.example.gnt.locations.presentation.details.recyclerview.CharacterDiffCallback;
import ru.example.gnt.locations.presentation.details.recyclerview.CharacterListAdapter;

public class LocationDetailsFragment extends Fragment implements DetailsFragment {

    private LocationDetailsFragmentBinding binding = null;

    @NotNull
    public static final String LOCATION_DETAILS_FRAGMENT_TAG = "LOCATION_DETAILS_FRAGMENT_TAG";

    private static final String LOCATION_ID_ARG = "CHARACTER_ID_ARG";

    @Inject
    public LocationDetailsViewModelFactory viewModelFactory;

    private LocationDetailsViewModel viewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        new ViewModelProvider(this).get(LocationsComponentViewModel.class)
                .getLocationComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewModel = viewModelFactory.create(getArguments().getInt(LOCATION_ID_ARG));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LocationDetailsFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeDataStates();
        initSwipeRefreshListener();
        observeMotionLayoutStates();
    }

    private void observeDataStates() {
        final Observer<UiState> observer = (Observer<UiState>) value -> {
            if (value instanceof UiState.Loading) {
                binding.swipeRefresh.setRefreshing(true);
            } else if (value instanceof UiState.Success) {
                showMainLayout();
                binding.swipeRefresh.setRefreshing(false);
                setValues(((UiState.Success<LocationDetailsModel>) value).getData());
            } else if (value instanceof UiState.Empty) {
                binding.swipeRefresh.setRefreshing(false);
            } else if (value instanceof UiState.Error) {
                binding.swipeRefresh.setRefreshing(false);
            }
        };

        viewModel.getState().observe(getViewLifecycleOwner(), observer);
    }

    private void showMainLayout() {
        binding.mainLayout.setVisibility(View.VISIBLE);
    }


    private void initSwipeRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadData();
            if (!isNetworkOn(requireContext())) {
                showToastShort(requireContext(), getString(ru.example.gnt.common.R.string.no_internet_connection_error));
            }
        });
    }


    private void setValues(LocationDetailsModel item) {
        item.getName();
        binding.tvName.setVisibility(View.VISIBLE);
        binding.tvName.setText(item.getName());
        if (item.getCreated() != null) {
            binding.tvCreated.setVisibility(View.VISIBLE);
            binding.tvCreated.setText(item.getCreated());
        } else {
            binding.tvCreated.setVisibility(View.GONE);
        }
        binding.tvType.setVisibility(ViewGroup.VISIBLE);

        CharacterListAdapter adapter = new CharacterListAdapter(new CharacterDiffCallback(), id -> {
            viewModel.navigateToCharacterDetails(id);
        }, Glide.with(binding.getRoot()));
        adapter.submitList(item.getResidents());
        binding.rvCharacters.setAdapter(adapter);
    }

    private void observeMotionLayoutStates() {
        binding.motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
                                                       @Override
                                                       public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                                                       }

                                                       @Override
                                                       public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                                                       }

                                                       @Override
                                                       public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                                                           if (motionLayout != null) {
                                                               binding.swipeRefresh.setEnabled(motionLayout.getCurrentState() == R.id.start);
                                                           }
                                                       }

                                                       @Override
                                                       public void onTransitionTrigger(MotionLayout motionLayout, int triggerId,
                                                                                       boolean positive, float progress) {
                                                       }
                                                   }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @NotNull
    public static LocationDetailsFragment createInstance(int id) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LOCATION_ID_ARG, id);
        fragment.setArguments(bundle);
        return fragment;
    }


}