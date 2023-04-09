package ru.example.gnt.characters.presentation.characters.detials

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.characters.databinding.CharacterDetailsFragmentBinding
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.common.DetailsFragmentLabel
import ru.example.gnt.common.UiState
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.flowWithLifecycle
import javax.inject.Inject

class CharacterDetailsFragment : BaseFragment<CharacterDetailsFragmentBinding>(
    CharacterDetailsFragmentBinding::inflate
), DetailsFragmentLabel {
    private var id: Int? = null

    @Inject
    internal lateinit var viewModel: CharacterDetailsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ViewModelProvider(this).get<CharactersComponentViewModel>()
            .charactersComponent.inject(this)
        id = arguments?.getInt(CHARACTER_ID_ARG)
        viewModel.setCharacterId(id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStateChanges()
    }

    private fun makeTextViewsVisible() {
        binding.layoutAdditionalInfo.visibility = ViewGroup.VISIBLE
        binding.layoutMainInfo.visibility = ViewGroup.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressIndicator.visibility = ViewGroup.GONE
    }

    private fun initViews(item: CharactersUiModel.Single) {
        with(binding) {
            tvName.text = item.name
            tvSpecies.text = item.species
            tvStatus.text = item.status.get
            tvStatus.setTextColor(item.status.color.getValue(root.context).defaultColor)
            tvGender.text = item.gender.n
            tvOrigin.text = item.origin.name
            tvType.text = item.type
        }
    }


    private fun observeStateChanges() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest {
                when (val state = viewModel.state.value) {
                    is UiState.Success -> {
                        Glide.with(binding.root)
                            .load(state.data.image)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                            })
                            .into(binding.ivAvatar)
                        makeTextViewsVisible()
                        initViews(item = state.data)
                        hideProgressBar()
                    }
                    is UiState.Error -> {

                    }
                    is UiState.Empty -> {

                    }
                    is UiState.Loading -> {

                    }
                }
            }
        }
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
