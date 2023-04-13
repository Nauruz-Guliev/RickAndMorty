package ru.example.gnt.characters.presentation.characters.paging_recyclerview

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.example.gnt.characters.databinding.CharacterItemBinding
import ru.example.gnt.common.model.ui.CharactersUiModel

class CharactersViewHolder(
    private val binding: CharacterItemBinding
) : ViewHolder(binding.root) {

    fun bind(item: CharactersUiModel.Single?) {
        if (item != null) {
            with(binding) {
                setImage(item)
                tvStatus.width = ivCharacter.maxWidth
                tvName.text = item.name
                tvGender.text = item.gender?.n
                tvSpecies.text = item.species
                tvStatus.text = item.status?.get
                item.status?.color?.getValue(binding.root.context)?.defaultColor?.let {
                    tvStatus.setBackgroundColor(
                        it
                    )
                }
            }
        }
    }

    private fun setImage(item: CharactersUiModel.Single) {
        Glide.with(binding.root.context)
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
        fun createInstance(parent: ViewGroup) =
            CharactersViewHolder(
                CharacterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

    }
}
