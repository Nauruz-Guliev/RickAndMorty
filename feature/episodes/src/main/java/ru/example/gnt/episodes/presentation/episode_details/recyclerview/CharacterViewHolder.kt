package ru.example.gnt.episodes.presentation.episode_details.recyclerview

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.episodes.databinding.CharacterItemBinding

class CharacterViewHolder(
    private val binding: CharacterItemBinding,
    private val onItemClicked: (id: Int) -> Unit,
    private val glide: RequestManager
) : ViewHolder(binding.root) {

    fun onBind(item: CharacterListItem?) {
        if (item != null) {
            with(binding) {
                setImage(item)
                tvStatus.width = ivCharacter.maxWidth
                tvName.text = item.name
                tvGender.text = item.gender?.value
                tvSpecies.text = item.species
                tvStatus.text = item.status?.value
                item.status?.color?.getValue(binding.root.context)?.defaultColor?.let {
                    tvStatus.setBackgroundColor(
                        it
                    )
                }
                root.setOnClickListener {
                    onItemClicked.invoke(item.id)
                }
            }
        }
    }

    private fun setImage(item: CharacterListItem) {
        glide
            .load(item.image)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(binding.ivCharacter)
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
