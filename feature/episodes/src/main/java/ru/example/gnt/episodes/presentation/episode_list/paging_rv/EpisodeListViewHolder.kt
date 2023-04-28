package ru.example.gnt.episodes.presentation.episode_list.paging_rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.episodes.databinding.EpisodeItemBinding

class EpisodeListViewHolder(
    private val binding: EpisodeItemBinding
) : ViewHolder(
    binding.root
) {
    fun bind(item: EpisodeListItem?) = with(binding) {
        tvEpisode.text = item?.episode
        tvName.text = item?.name
        tvAirDate.text = item?.airDate
    }

    companion object {
        operator fun invoke(parent: ViewGroup): EpisodeListViewHolder =
            EpisodeListViewHolder(
                binding = EpisodeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }
}
