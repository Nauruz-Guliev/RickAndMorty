package ru.example.gnt.common.recyclerview_delegate

interface DelegateItem {
    fun content() : Any
    fun id(): Int
    fun compareToOther(other: DelegateItem): Boolean
}
