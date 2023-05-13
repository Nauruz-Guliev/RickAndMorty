package ru.example.gnt.episodes.presentation.episode_list.paging_rv

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.example.gnt.common.model.episodes.EpisodeListItem

class EpisodeListAdapter(
    private val onItemClicked: (id: Int?) -> Unit
) : PagingDataAdapter<EpisodeListItem, EpisodeListViewHolder>(DiffCallback) {


    override fun onBindViewHolder(holder: EpisodeListViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeListViewHolder =
        EpisodeListViewHolder(parent, onItemClicked)

    object DiffCallback : DiffUtil.ItemCallback<EpisodeListItem>() {

        override fun areItemsTheSame(
            oldItem: EpisodeListItem,
            newItem: EpisodeListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }


        override fun areContentsTheSame(
            oldItem: EpisodeListItem,
            newItem: EpisodeListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
