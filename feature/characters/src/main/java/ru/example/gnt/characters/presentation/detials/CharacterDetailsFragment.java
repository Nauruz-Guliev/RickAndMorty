package ru.example.gnt.characters.presentation.detials;

import static ru.example.gnt.common.utils.extensions.UiExtensionsKt.showToastShort;
import static ru.example.gnt.common.utils.extensions.UtilityExtensionsKt.isNetworkOn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import ru.example.gnt.characters.R;
import ru.example.gnt.characters.databinding.CharacterDetailsFragmentBinding;
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel;
import ru.example.gnt.characters.presentation.detials.recyclerview.EpisodesAdapter;
import ru.example.gnt.characters.presentation.detials.recyclerview.EpisodesDiffCallback;
import ru.example.gnt.common.base.interfaces.DetailsFragment;
import ru.example.gnt.common.exceptions.ApplicationException;
import ru.example.gnt.common.model.Resource;
import ru.example.gnt.common.model.UiState;


public class CharacterDetailsFragment extends Fragment implements DetailsFragment {
    private CharacterDetailsFragmentBinding binding = null;
    @NotNull
    public static final String CHARACTER_DETAILS_FRAGMENT_TAG = "CHARACTER_DETAILS_FRAGMENT_TAG";
    private static final String CHARACTER_ID_ARG = "CHARACTER_ID_ARG";
    @Inject
    public CharacterDetailsViewModelFactory viewModelFactory;

    private CharacterDetailsViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        new ViewModelProvider(this).get(CharactersComponentViewModel.class)
                .getCharactersComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewModel = viewModelFactory.create(getArguments().getInt(CHARACTER_ID_ARG));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CharacterDetailsFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    private void checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(Network network) {
                super.onLost(network);
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        binding.tvNetwork.tvNetwork.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {
                    }
                });
            }

            ;

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        binding.tvNetwork.tvNetwork.setVisibility(View.GONE);
                    } catch (Exception ex) {
                    }

                });
            }

            ;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isNetworkOn(requireContext()))
            new Handler(Looper.getMainLooper()).post(() -> binding.tvNetwork.tvNetwork.setVisibility(View.VISIBLE));
        observeDataStates();
        checkConnectivity();
        initSwipeRefreshListener();
        observeMotionLayoutStates();
    }

    private void initSwipeRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadData();
            if (!isNetworkOn(requireContext())) {
                showToastShort(requireContext(), getString(ru.example.gnt.ui.R.string.no_internet_connection_error));
            }
        });

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

    private void observeDataStates() {
        final Observer<UiState<?>> observer = value -> {
            if (value instanceof UiState.Loading) {
                binding.swipeRefresh.setRefreshing(true);
                binding.tvNetwork.tvNetwork.setText(getString(ru.example.gnt.ui.R.string.no_internet_connection_error));
            } else if (value instanceof UiState.SuccessRemote) {
                showMainLayout();
                binding.swipeRefresh.setRefreshing(false);
                setValues(((UiState.SuccessRemote<CharacterDetailsModel>) value).getData());
                binding.tvNetwork.tvNetwork.setText(getString(ru.example.gnt.ui.R.string.no_internet_connection_error));
            } else if(value instanceof UiState.SuccessCached) {
                showMainLayout();
                binding.swipeRefresh.setRefreshing(false);
                setValues(((UiState.SuccessCached<CharacterDetailsModel>) value).getData());
                binding.tvNetwork.tvNetwork.setText(getString(ru.example.gnt.ui.R.string.local_data_warning));
            } else if (value instanceof UiState.Empty) {
                binding.swipeRefresh.setRefreshing(false);
                binding.tvNetwork.tvNetwork.setText(getString(ru.example.gnt.ui.R.string.no_internet_connection_error));
            } else if (value instanceof UiState.Error) {
                handleErrors(((UiState.Error) value).getError());
                binding.tvNetwork.tvNetwork.setText(getString(ru.example.gnt.ui.R.string.no_internet_connection_error));
                binding.swipeRefresh.setRefreshing(false);
            }
        };
        viewModel.getState().observe(getViewLifecycleOwner(), observer);
    }

    private void handleErrors(Throwable ex) {
       if (ex instanceof ApplicationException) {
            Resource.String resource = ((ApplicationException) ex).getResource();
            if (resource != null) {
                showToastShort(requireContext(), resource.getValue(requireContext()));
            }
        } else {
            Log.e("ERROR", ex.getLocalizedMessage(), ex);
        }
    }

    private void showMainLayout() {
        binding.mainLayout.setVisibility(View.VISIBLE);
    }


    private void setValues(CharacterDetailsModel item) {
        if (item.getStatus() != null) {
            binding.tvStatus.setVisibility(View.VISIBLE);
            binding.tvStatus.setText(item.getStatus().getValue());
        } else {
            binding.tvStatus.setVisibility(View.GONE);
        }
        if (item.getGender() != null) {
            binding.tvGender.setVisibility(View.VISIBLE);
            binding.tvGender.setText(getString(ru.example.gnt.ui.R.string.gender, item.getGender().getValue()));
        } else {
            binding.tvGender.setVisibility(View.GONE);
        }
        if (item.getOrigin() != null) {
            binding.tvOrigin.setVisibility(ViewGroup.VISIBLE);
            binding.tvOrigin.setText(requireContext().getString(ru.example.gnt.ui.R.string.origin, item.getOrigin().getName()));
            binding.tvOrigin.setOnClickListener((view) -> {
                viewModel.navigateToLocationDetails(item.getOrigin().getId());
            });
        } else {
            binding.tvOrigin.setVisibility(ViewGroup.GONE);
        }
        if (item.getLocation() != null) {
            binding.tvLocation.setVisibility(ViewGroup.VISIBLE);
            binding.tvLocation.setText(requireContext().getString(ru.example.gnt.ui.R.string.location, item.getLocation().getName()));
            binding.tvLocation.setOnClickListener((view) -> {
                viewModel.navigateToLocationDetails(item.getLocation().getId());
            });
        } else {
            binding.tvLocation.setVisibility(ViewGroup.GONE);
        }
        if (item.getType() != null && item.getType().length() > 0) {
            binding.tvType.setVisibility(ViewGroup.VISIBLE);
            binding.tvType.setText(item.getType());
        } else {
            binding.tvType.setVisibility(ViewGroup.GONE);
        }

        try {
            binding.tvStatus.setBackgroundColor(item.getStatus().getColor().getValue(binding.getRoot().getContext()).getDefaultColor());
        } catch (Exception ignored) {

        }
        EpisodesAdapter adapter = new EpisodesAdapter(new EpisodesDiffCallback(), id -> viewModel.navigateToEpisodeDetails(id));
        adapter.submitList(item.getEpisode());
        binding.rvEpisodes.setAdapter(adapter);
        binding.tvName.setText(item.getName());
        binding.tvName.setVisibility(View.VISIBLE);
        if (item.getSpecies().length() > 0) {
            binding.tvRv.setVisibility(View.VISIBLE);
        } else {
            binding.tvRv.setVisibility(View.GONE);
        }
        binding.tvSpecies.setText(item.getSpecies());
        binding.tvSpecies.setVisibility(View.VISIBLE);
        binding.ivAvatar.setVisibility(ViewGroup.VISIBLE);
        Glide.with(requireContext())
                .load(item.getImage())
                .into(binding.ivAvatar);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @NotNull
    public static CharacterDetailsFragment createInstance(int id) {
        CharacterDetailsFragment fragment = new CharacterDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CHARACTER_ID_ARG, id);
        fragment.setArguments(bundle);
        return fragment;
    }
}
