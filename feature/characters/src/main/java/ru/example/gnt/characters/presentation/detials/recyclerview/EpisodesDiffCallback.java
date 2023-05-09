package ru.example.gnt.characters.presentation.detials.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import ru.example.gnt.common.model.episodes.EpisodeListItem;

public class EpisodesDiffCallback extends DiffUtil.ItemCallback<EpisodeListItem> {
    @Override
    public boolean areItemsTheSame(@NonNull EpisodeListItem oldItem, @NonNull EpisodeListItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull EpisodeListItem oldItem, @NonNull EpisodeListItem newItem) {
        return oldItem.equals(newItem);
    }
}
