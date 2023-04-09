package ru.example.gnt.characters.presentation.characters.detials

import android.os.Bundle
import androidx.core.os.bundleOf
import ru.example.gnt.characters.databinding.CharacterDetailsFragmentBinding
import ru.example.gnt.common.base.BaseFragment

class CharacterDetailsFragment : BaseFragment<CharacterDetailsFragmentBinding>(
    CharacterDetailsFragmentBinding::inflate
) {
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getInt(CHARACTER_ID_ARG)
    }


    companion object {
        const val CHARACTER_DETAILS_FRAGMENT_TAG = "CHARACTER_DETAILS_FRAGMENT_TAG"
        const val CHARACTER_ID_ARG = "CHARACTER_ID_ARG"
        fun createInstance(id: Int) = CharacterDetailsFragment().apply {
            arguments = bundleOf(
                CHARACTER_ID_ARG to id,
            )
        }
    }
}
