package ru.example.gnt.characters.presentation.detials.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.example.gnt.characters.databinding.EpisodeListItemBinding;
import ru.example.gnt.common.model.Resource;
import ru.example.gnt.common.model.episodes.EpisodeListItem;

public class EpisodesViewHolder extends RecyclerView.ViewHolder {

    protected EpisodeListItemBinding binding;
    private final EpisodeViewHolderEventListener listener;

    public EpisodesViewHolder(@NonNull EpisodeListItemBinding binding, EpisodeViewHolderEventListener listener) {
        super(binding.getRoot());
        this.listener = listener;
        this.binding = binding;
    }

    public void onBind(EpisodeListItem item) {
        binding.tvEpisode.setText(item.getEpisode());
        binding.tvName.setText(item.getName());
        binding.tvAirDate.setText(item.getAirDate());
        binding.getRoot().setOnClickListener(v -> {
            listener.onItemClicked(item.getId());
        });
        switch (getBindingAdapterPosition() % 3) {
            case 0: {
                binding.getRoot().setBackgroundColor(
                        new Resource.Color(ru.example.gnt.ui.R.color.yellow_rm)
                                .getValue(binding.getRoot().getContext()).getDefaultColor());
                break;
            }
            case 1: {
                binding.getRoot().setBackgroundColor(
                        new Resource.Color(ru.example.gnt.ui.R.color.blue_rm)
                                .getValue(binding.getRoot().getContext()).getDefaultColor());
                break;
            }
            case 2: {
                binding.getRoot().setBackgroundColor(
                        new Resource.Color(ru.example.gnt.ui.R.color.red_rm)
                                .getValue(binding.getRoot().getContext()).getDefaultColor());
                break;
            }
        }

    }

}
