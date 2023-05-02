package ru.example.gnt.locations.presentation.list.rv_paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.locations.databinding.LocationItemBinding

class EpisodeListViewHolder(
    private val binding: LocationItemBinding,
    private val onItemClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {
    fun bind(item: LocationListItem?) = with(binding) {
        tvDimension.text = item?.dimension
        tvType.text = item?.type
        tvName.text = item?.name
        root.setOnClickListener {
            onItemClicked.invoke(item?.id)
        }
    }

    companion object {
        operator fun invoke(
            parent: ViewGroup,
            onItemClicked: (id: Int?) -> Unit
        ): EpisodeListViewHolder {
            val holder = EpisodeListViewHolder(
                binding = LocationItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClicked = onItemClicked
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
