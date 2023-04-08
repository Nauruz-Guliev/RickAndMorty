package ru.example.gnt.characters.presentation.characters

import android.os.Bundle
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
import ru.example.gnt.common.base.BaseFragment

class CharactersFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
) {

    companion object {
        const val CHARACTERS_FRAGMENT_TAG: String = "CHARACTERS_FRAGMENT_TAG"
        fun createInstance() = CharactersFragment().apply {
            //пока сюда передавать нечего
            arguments = Bundle()
        }
    }
}
