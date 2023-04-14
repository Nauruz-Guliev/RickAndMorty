package ru.example.gnt.characters.presentation.characters.paging_recyclerview

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.example.gnt.ui.databinding.LoadStateLayoutBinding

typealias TryAgainAction = () -> Unit


class CustomLoadStateAdapter(
    private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<CustomLoadStateAdapter.LoadStateViewHolder>() {


    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

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
        val swipeRefreshLayout: SwipeRefreshLayout? = null,
        val tryAgainAction: TryAgainAction? = null
    ) : ViewHolder(binding.root) {

        init {
            binding.tryAgainButton.setOnClickListener { tryAgainAction?.invoke() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            messageTextView.isVisible = loadState is LoadState.Error
            tryAgainButton.isVisible = loadState is LoadState.Error
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                progressBar.isVisible = false
            } else {
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }
}
