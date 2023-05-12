package ru.example.gnt.characters.presentation.detials.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import ru.example.gnt.characters.databinding.EpisodeListItemBinding;
import ru.example.gnt.common.model.episodes.EpisodeListItem;

public class EpisodesAdapter extends ListAdapter<EpisodeListItem, EpisodesViewHolder> {

    private EpisodeViewHolderEventListener listener;
    public EpisodesAdapter(@NonNull DiffUtil.ItemCallback<EpisodeListItem> diffCallback, EpisodeViewHolderEventListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodesViewHolder(
                EpisodeListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext())
                        , parent,
                        false
                ),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.onBind(getCurrentList().get(position));
    }
}
