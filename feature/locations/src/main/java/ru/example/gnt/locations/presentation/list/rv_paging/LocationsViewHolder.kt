package ru.example.gnt.locations.presentation.list.rv_paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.common.utils.extensions.divideTwoTabsEqually
import ru.example.gnt.locations.databinding.LocationItemBinding

class EpisodeListViewHolder(
    private val binding: LocationItemBinding,
    private val onItemClicked: (id: Int) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {
    init {
        (itemView.rootView as? MaterialCardView)?.strokeWidth = 4
    }
    fun bind(item: LocationListItem?) = with(binding) {
        tvDimension.text = item?.dimension
        tvType.text = item?.type
        tvName.text = item?.name
        root.setOnClickListener {
            onItemClicked.invoke(item!!.id)
        }
        with(root) {
            when (bindingAdapterPosition % 3) {
                0 -> strokeColor = Resource.Color(ru.example.gnt.ui.R.color.yellow_rm).getValue(root.context).defaultColor
                1 -> strokeColor = Resource.Color(ru.example.gnt.ui.R.color.blue_rm).getValue(root.context).defaultColor
                2 -> strokeColor = Resource.Color(ru.example.gnt.ui.R.color.red_rm).getValue(root.context).defaultColor
            }
        }
    }

    companion object {
        operator fun invoke(
            parent: ViewGroup,
            onItemClicked: (id: Int) -> Unit
        ): EpisodeListViewHolder = EpisodeListViewHolder(
            binding = LocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked = onItemClicked
        ).divideTwoTabsEqually()

    }
}
