package ru.example.gnt.locations.presentation.details.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.RequestManager;

import ru.example.gnt.common.model.characters.CharacterListItem;
import ru.example.gnt.ui.databinding.CharacterItemBinding;

public class CharacterListAdapter extends ListAdapter<CharacterListItem, CharacterViewHolder> {

    private CharacterViewHolderEventListener listener;
    private RequestManager glide;

    public CharacterListAdapter(@NonNull DiffUtil.ItemCallback<CharacterListItem> diffCallback, CharacterViewHolderEventListener listener, RequestManager glide) {
        super(diffCallback);
        this.listener = listener;
        this.glide = glide;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterViewHolder viewHolder = new CharacterViewHolder(
                CharacterItemBinding.inflate(
                        LayoutInflater.from(parent.getContext())
                        , parent,
                        false
                ),
                listener,
                glide
        );

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        params.width = parent.getContext().getResources().getDisplayMetrics().widthPixels / 2;
        viewHolder.binding.getRoot().setLayoutParams(params);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        holder.onBind(getCurrentList().get(position));
    }
}
