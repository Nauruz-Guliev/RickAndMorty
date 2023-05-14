package ru.example.gnt.episodes.presentation.list.paging_rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.utils.extensions.divideTwoTabsEqually
import ru.example.gnt.episodes.databinding.EpisodeItemBinding


class EpisodeListViewHolder(
    private val binding: EpisodeItemBinding,
    private val onItemClicked: (id: Int?) -> Unit
) : ViewHolder(
    binding.root
) {

    fun bind(item: EpisodeListItem?) = with(binding) {
        tvEpisode.text = root.context.getString(ru.example.gnt.ui.R.string.episode, item?.episode.toString())
        tvName.text = item?.name
        tvAirDate.text = root.context.getString(ru.example.gnt.ui.R.string.air_date, item?.airDate.toString())

        root.setOnClickListener {
            onItemClicked.invoke(item?.id)
        }

        when (bindingAdapterPosition % 3) {
            0 -> root.setBackgroundColor(
                Resource.Color(ru.example.gnt.ui.R.color.yellow_rm)
                    .getValue(root.context).defaultColor
            )
            1 -> root.setBackgroundColor(
                Resource.Color(ru.example.gnt.ui.R.color.blue_rm)
                    .getValue(root.context).defaultColor
            )
            2 -> root.setBackgroundColor(
                Resource.Color(ru.example.gnt.ui.R.color.red_rm).getValue(root.context).defaultColor
            )
        }
    }

    companion object {
        operator fun invoke(
            parent: ViewGroup,
            onItemClicked: (id: Int?) -> Unit
        ): EpisodeListViewHolder {
            return EpisodeListViewHolder(
                binding = EpisodeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClicked = onItemClicked
            ).divideTwoTabsEqually()
        }
    }
}
