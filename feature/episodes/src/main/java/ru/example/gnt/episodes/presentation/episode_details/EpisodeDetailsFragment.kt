package ru.example.gnt.episodes.presentation.episode_details

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
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
import ru.example.gnt.common.utils.extensions.isNetworkOn
import ru.example.gnt.episodes.R
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
            viewModel = viewModelFactory.create(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStates()
        refreshListener()
        observeMotionLayoutStates()
        observeConnectivity()
    }

    override fun onResume() {
        super.onResume()
        binding.tvNetwork.tvNetwork.isVisible = !requireContext().isNetworkOn()
    }

    private fun observeConnectivity() {
        lifecycleScope.launch {
            networkState.flowWithLifecycle(lifecycle).collectLatest {
                binding.tvNetwork.tvNetwork.isVisible = !it
            }
        }
    }

    private fun observeMotionLayoutStates() {
        binding.constraintLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (motionLayout != null) {
                    binding.swipeRefresh.isEnabled = motionLayout.currentState == R.id.start
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
        })
    }

    private fun observeStates() {
        lifecycleScope.launch {
            viewModel?.state?.flowWithLifecycle(lifecycle)?.distinctUntilChanged()
                ?.collectLatest { state ->
                    with(binding) {
                        when (state) {
                            is UiState.Success -> {
                                setRefreshing(false)
                                initViews(state.data)
                            }
                            is UiState.Loading -> {
                                setRefreshing(true)
                            }
                            is UiState.Error -> {
                                setRefreshing(false)
                                handleErrorState(state.error)
                            }
                            is UiState.Empty -> {
                                setRefreshing(false)
                            }
                        }
                    }
                }
        }
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefreshing
    }

    private fun refreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel?.loadEpisode()
        }
    }

    private fun initViews(episodeDetailsItem: EpisodeDetailsItem) {
        with(binding) {
            tvAirDate.isVisible = true
            tvAirDate.text = episodeDetailsItem.airDate

            tvCreated.text = episodeDetailsItem.created
            tvCreated.isVisible = true

            tvEpisodeCode.text = episodeDetailsItem.episode
            tvEpisodeCode.isVisible = true

            tvName.text = episodeDetailsItem.name
            tvName.isVisible = true

            tvRv.isVisible = (episodeDetailsItem.characters?.size ?: 0) > 0
            rvCharacters.adapter =
                CharacterListAdapter(::onItemClicked, Glide.with(binding.root)).apply {
                    submitList(episodeDetailsItem.characters)
                }
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
