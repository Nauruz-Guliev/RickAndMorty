package ru.example.gnt.characters.presentation.characters.paging_recyclerview

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.example.gnt.common.model.ui.CharactersUiModel

class CharactersAdapter
    : PagingDataAdapter<CharactersUiModel.Single, CharactersViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder =
        CharactersViewHolder.createInstance(parent)

    object DiffCallback : DiffUtil.ItemCallback<CharactersUiModel.Single>() {

        override fun areItemsTheSame(
            oldItem: CharactersUiModel.Single,
            newItem: CharactersUiModel.Single
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CharactersUiModel.Single,
            newItem: CharactersUiModel.Single
        ): Boolean {
            return oldItem == newItem
        }
    }
}
