package ru.example.gnt.common.recyclerview_delegate

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView

class BasePagingDelegateAdapter :
    PagingDataAdapter<DelegateItem, RecyclerView.ViewHolder>(DelegateAdapterItemCallback()) {
    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position), position)

    fun addDelegate(delegate: AdapterDelegate) =
        delegates.add(delegate)


    override fun getItemViewType(position: Int): Int = delegates.indexOfFirst {
        it.isOfViewType(getItem(position))
    }

}
