package ru.example.gnt.locations.presentation.list.rv_paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.common.utils.extensions.divideTwoTabsEqually
import ru.example.gnt.locations.databinding.LocationItemBinding

class EpisodeListViewHolder(
    private val binding: LocationItemBinding,
    private val onItemClicked: (id: Int) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {
    fun bind(item: LocationListItem?) = with(binding) {
        tvDimension.text = item?.dimension
        tvType.text = item?.type
        tvName.text = item?.name
        root.setOnClickListener {
            onItemClicked.invoke(item!!.id)
        }
    }

    companion object {
        operator fun invoke(
            parent: ViewGroup,
            onItemClicked: (id: Int) -> Unit
        ) = EpisodeListViewHolder(
            binding = LocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked = onItemClicked
        ).divideTwoTabsEqually()
    }
}
