package ru.example.gnt.common.recyclerview_delegate

import androidx.recyclerview.widget.DiffUtil

class DelegateAdapterItemCallback : DiffUtil.ItemCallback<DelegateItem>() {

    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem.compareToOther(newItem)
}
