package ru.example.gnt.characters.presentation.detials;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import ru.example.gnt.characters.databinding.CharacterDetailsFragmentBinding;
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel;
import ru.example.gnt.common.model.UiState;
import ru.example.gnt.characters.presentation.list.model.CharactersUiModel;
import ru.example.gnt.common.base.interfaces.DetailsFragment;


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
        observeStates();
        return binding.getRoot();
    }

    private void observeStates() {

        final Observer<UiState> observer = (Observer<UiState>) value -> {
            if(value instanceof UiState.Loading) {

            } else if(value instanceof  UiState.Success) {
                hideLoading();
                showMainLayout();
                setValues(((UiState.Success<CharactersUiModel.Single>) value).getData());
            } else if (value instanceof UiState.Empty) {
                hideLoading();
            } else if (value instanceof UiState.Error) {
                hideLoading();
            }
        };

        viewModel.getState().observe(getViewLifecycleOwner(), observer);
    }

    private void showMainLayout() {
        binding.mainLayout.setVisibility(View.VISIBLE);
    }
    private void hideLoading() {
        binding.progressIndicator.setVisibility(View.GONE);
    }

    private void setValues(CharactersUiModel.Single item) {
        binding.tvStatus.setText(item.getStatus().getGet());
        binding.tvGender.setText(item.getGender().getN());
        binding.tvName.setText(item.getName());
        binding.tvOrigin.setText(item.getOrigin().getName());
        binding.tvSpecies.setText(item.getSpecies());
        binding.tvType.setText(item.getType());
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
