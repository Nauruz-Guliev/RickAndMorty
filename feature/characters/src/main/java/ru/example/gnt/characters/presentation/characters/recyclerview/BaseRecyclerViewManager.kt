package ru.example.gnt.characters.presentation.characters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import ru.example.gnt.common.base.BaseClass

class BaseRecyclerViewManager<T : BaseClass, B>(
    private val onBindAction: (B, T) -> Unit,
    private val actionListener: RecyclerViewAction,
    @LayoutRes private val itemContainerId: Int,
    private val differs: (() -> Pair<Boolean, Boolean>)? = null,
) where B : ViewBinding {

    val adapter: RecyclerViewAdapter = RecyclerViewAdapter()


    inner class RecyclerViewAdapter :
        ListAdapter<BaseClass, BaseViewHolder>(DiffCallBack()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    itemContainerId,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.onBind(currentList[position])
        }

    }

    inner class BaseViewHolder(private val binding: ViewBinding) :
        ViewHolder(binding.root) {
        fun onBind(item: BaseClass) {
            binding.root.setOnClickListener {
                actionListener.onItemClicked(item.id)
            }
            onBindAction.invoke(binding as B, item as T)
        }
    }

    private inner class DiffCallBack : DiffUtil.ItemCallback<BaseClass>() {
        override fun areItemsTheSame(oldItem: BaseClass, newItem: BaseClass): Boolean {
            return (differs?.invoke()?.first) ?: (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: BaseClass, newItem: BaseClass): Boolean {
            return (differs?.invoke()?.second) ?: (oldItem == newItem)
        }
    }
}



