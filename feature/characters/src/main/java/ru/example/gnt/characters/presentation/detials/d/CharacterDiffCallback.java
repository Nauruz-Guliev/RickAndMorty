package ru.example.gnt.characters.presentation.detials.d;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import ru.example.gnt.common.model.characters.CharacterListItem;

public class CharacterDiffCallback extends DiffUtil.ItemCallback<CharacterListItem> {
    @Override
    public boolean areItemsTheSame(@NonNull CharacterListItem oldItem, @NonNull CharacterListItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull CharacterListItem oldItem, @NonNull CharacterListItem newItem) {
        return oldItem.equals(newItem);
    }
}
