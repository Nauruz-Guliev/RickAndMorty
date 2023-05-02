package ru.example.gnt.locations.presentation.list.rv_paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.example.gnt.common.model.locations.LocationListItem

class LocationListAdapter(
    private val onItemClicked: (id: Int?) -> Unit
) : PagingDataAdapter<LocationListItem, EpisodeListViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: EpisodeListViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeListViewHolder =
        EpisodeListViewHolder(parent, onItemClicked)

    object DiffCallback : DiffUtil.ItemCallback<LocationListItem>() {
        override fun areItemsTheSame(
            oldItem: LocationListItem,
            newItem: LocationListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LocationListItem,
            newItem: LocationListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
