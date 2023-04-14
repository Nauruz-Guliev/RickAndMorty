package ru.example.gnt.common.model.ui.characters

import ru.example.gnt.common.recyclerview_delegate.DelegateItem

class CharactersUiModelDelegate(
    val id: Int,
    private val value: CharactersUiModel
) : DelegateItem {
    override fun content(): Any = value

    override fun id(): Int = id

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as CharactersUiModelDelegate).value == content()
}
