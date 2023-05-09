package ru.example.gnt.episodes.presentation.episode_details.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import ru.example.gnt.common.model.characters.CharacterListItem

class CharacterListAdapter(private val onItemClicked: (Int)-> Unit, private val glide: RequestManager): ListAdapter<CharacterListItem, CharacterViewHolder>(object:
    DiffUtil.ItemCallback<CharacterListItem>(){
    override fun areItemsTheSame(oldItem: CharacterListItem, newItem: CharacterListItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(
        oldItem: CharacterListItem,
        newItem: CharacterListItem
    ): Boolean = oldItem == newItem

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder =
        CharacterViewHolder(parent = parent, onItemClicked = onItemClicked, glide = glide)

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) =
        holder.onBind(currentList[position])
}
