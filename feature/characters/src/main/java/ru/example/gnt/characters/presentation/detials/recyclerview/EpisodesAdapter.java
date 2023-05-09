package ru.example.gnt.characters.presentation.detials.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
        EpisodesViewHolder viewHolder = new EpisodesViewHolder(
                EpisodeListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext())
                        , parent,
                        false
                ),
                listener
        );

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        params.width = parent.getContext().getResources().getDisplayMetrics().widthPixels / 2;
        viewHolder.binding.getRoot().setLayoutParams(params);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.onBind(getCurrentList().get(position));
    }
}
