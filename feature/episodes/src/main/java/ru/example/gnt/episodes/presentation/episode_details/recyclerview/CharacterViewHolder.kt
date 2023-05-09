package ru.example.gnt.episodes.presentation.episode_details.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.episodes.databinding.CharacterItemBinding

class CharacterViewHolder(
    private val binding: CharacterItemBinding,
    private val onItemClicked: (id: Int) -> Unit,
    private val glide: RequestManager
) : ViewHolder(binding.root) {

    fun onBind(characterListItem: CharacterListItem) = with(binding) {
        tvGender.text = characterListItem.gender?.value
        tvName.text = characterListItem.name
        tvSpecies.text = characterListItem.species
        glide.load(characterListItem.image)
            .into(ivCharacter)
        root.setOnClickListener {
            onItemClicked(characterListItem.id)
        }
    }

    companion object {
        operator fun invoke(
            parent: ViewGroup,
            onItemClicked: (id: Int) -> Unit,
            glide: RequestManager
        ) : CharacterViewHolder {
            val holder =   CharacterViewHolder(
                binding = CharacterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClicked = onItemClicked,
                glide = glide
            )
            (holder.binding.root.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                width = parent.context.resources.displayMetrics.widthPixels / 2
            }.also {
                holder.binding.root.layoutParams = it
            }
            return holder
        }
    }
}
