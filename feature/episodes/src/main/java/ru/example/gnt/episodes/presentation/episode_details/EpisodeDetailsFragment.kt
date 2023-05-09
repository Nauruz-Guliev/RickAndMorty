package ru.example.gnt.episodes.presentation.episode_details

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.interfaces.DetailsFragment
import ru.example.gnt.common.model.UiState
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.episodes.databinding.EpisodeDetailsFragmentBinding
import ru.example.gnt.episodes.di.deps.EpisodesComponentViewModel
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.presentation.episode_details.recyclerview.CharacterListAdapter
import javax.inject.Inject

class EpisodeDetailsFragment : BaseFragment<
        EpisodeDetailsFragmentBinding>(
    EpisodeDetailsFragmentBinding::inflate
), DetailsFragment {

    @Inject
    lateinit var viewModelFactory: EpisodeDetailsViewModel.EpisodeDetailsViewModelFactory
    private var viewModel: EpisodeDetailsViewModel? = null

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<EpisodesComponentViewModel>()
            .episodesComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.getInt(EPISODE_ID_ARG)
        if (id != null) {
            Log.d("NAVIGATION", id.toString())
            viewModel = viewModelFactory.create(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStates()
    }

    private fun observeStates() {
        lifecycleScope.launch {
            viewModel?.state?.flowWithLifecycle(lifecycle)?.distinctUntilChanged()
                ?.collectLatest { state ->
                    with(binding) {
                        when (state) {
                            is UiState.Success -> initViews(state.data)
                            is UiState.Loading -> {
                                mainLayout.isVisible = false
                            }
                            is UiState.Error -> {
                                mainLayout.isVisible = false
                                context.showToastShort(state.message)
                            }
                            is UiState.Empty -> {
                                mainLayout.isVisible = false
                                progressBar.isVisible = false
                            }
                        }
                    }
                }
        }
    }

    private fun initViews(episodeDetailsItem: EpisodeDetailsItem) {
        with(binding) {
            mainLayout.isVisible = true
            progressBar.isVisible = false
            tvAirDate.text = episodeDetailsItem.airDate
            tvCreated.text = episodeDetailsItem.created
            tvEpisodeCode.text = episodeDetailsItem.episode
            tvName.text = episodeDetailsItem.name

            CharacterListAdapter(::onItemClicked, Glide.with(binding.root.context))
        }
    }

    private fun onItemClicked(id: Int) {
        viewModel?.navigateToCharacterDetails(id)
    }


    companion object {
        const val EPISODE_ID_ARG = "EPISODE_ID_ARG"
        const val EPISODE_DETAILS_TAG = "EPISODE_DETAILS_TAG"
        fun createInstance(id: Int?) = EpisodeDetailsFragment().apply {
            arguments = bundleOf(EPISODE_ID_ARG to id)
        }
    }
}
