package ru.example.gnt.locations.presentation.details.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import ru.example.gnt.common.model.characters.CharacterListItem;
import ru.example.gnt.ui.databinding.CharacterItemBinding;

public class CharacterViewHolder extends RecyclerView.ViewHolder {

    protected CharacterItemBinding binding;
    private final CharacterViewHolderEventListener listener;
    private final RequestManager glide;

    public CharacterViewHolder(@NonNull CharacterItemBinding binding, CharacterViewHolderEventListener listener, RequestManager glide) {
        super(binding.getRoot());
        this.listener = listener;
        this.binding = binding;
        this.glide = glide;
    }

    public void onBind(CharacterListItem item) {
        try {
            binding.tvGender.setText(item.getGender().getValue());
            binding.tvStatus.setText(item.getStatus().getValue());
            binding.tvStatus.setBackgroundColor(item.getStatus().getColor().getValue(binding.getRoot().getContext()).getDefaultColor());
        } catch (Exception ignored) {}
        binding.tvName.setText(item.getName());
        binding.tvSpecies.setText(item.getSpecies());
        binding.tvStatus.setWidth(binding.ivCharacter.getMaxWidth());
        binding.getRoot().setOnClickListener(v -> {
            listener.onItemClicked(item.getId());
        });
        glide.load(item.getImage()).into(binding.ivCharacter);

    }

}
