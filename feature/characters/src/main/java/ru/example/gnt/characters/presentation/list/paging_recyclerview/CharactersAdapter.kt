package ru.example.gnt.characters.presentation.list.paging_recyclerview

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.example.gnt.common.model.characters.CharacterListItem

class CharactersAdapter(private val onItemClicked: ((id: Int) -> Unit)) :
    PagingDataAdapter<CharacterListItem, CharactersViewHolder>(DiffCallback) {


    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder =
        CharactersViewHolder.createInstance(parent = parent, onItemClicked = onItemClicked)

    object DiffCallback : DiffUtil.ItemCallback<CharacterListItem>() {

        override fun areItemsTheSame(
            oldItem: CharacterListItem,
            newItem: CharacterListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CharacterListItem,
            newItem: CharacterListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
