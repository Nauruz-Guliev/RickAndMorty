package ru.example.gnt.characters.presentation.characters.paging_recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.ui.databinding.LoadStateLayoutBinding

typealias TryAgainAction = () -> Unit
typealias LoadStateCallback = (LoadState) -> Unit



class CustomLoadStateAdapter(
    private val tryAgainAction: TryAgainAction,
) : LoadStateAdapter<CustomLoadStateAdapter.LoadStateViewHolder>() {


    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
            isFullSpan = true
            holder.itemView.layoutParams = this
        }
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder =
        LoadStateViewHolder(
            binding = LoadStateLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            tryAgainAction = tryAgainAction
        )


    class LoadStateViewHolder(
        val binding: LoadStateLayoutBinding,
        val tryAgainAction: TryAgainAction? = null,
    ) : ViewHolder(binding.root) {

        init {
            binding.tryAgainButton.setOnClickListener { tryAgainAction?.invoke() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            val isInternetOn = root.context.isNetworkOn()
            messageTextView.isVisible = loadState is LoadState.Error && isInternetOn
            tryAgainButton.isVisible = loadState is LoadState.Error && isInternetOn
            progressBar.isVisible = loadState is LoadState.Loading && isInternetOn
        }
    }
}

