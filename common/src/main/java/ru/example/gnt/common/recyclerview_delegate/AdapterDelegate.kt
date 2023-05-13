package ru.example.gnt.common.recyclerview_delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface AdapterDelegate {
    fun onCreateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
    fun onBindViewHolder(holder: ViewHolder, item: DelegateItem?, position: Int)
    fun isOfViewType(item: DelegateItem?): Boolean
}
