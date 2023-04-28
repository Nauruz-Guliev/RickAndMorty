package ru.example.gnt.episodes.presentation.episode_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.utils.CustomLoadStateAdapter
import ru.example.gnt.common.utils.TryAgainAction
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.databinding.EpisodesFragmentBinding
import ru.example.gnt.episodes.di.deps.EpisodesComponentViewModel
import ru.example.gnt.episodes.presentation.episode_list.paging_rv.EpisodeListAdapter
import javax.inject.Inject

class EpisodeListFragment : BaseFragment<EpisodesFragmentBinding>(
    EpisodesFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment, RootFragment {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var coordinatorLayout: CoordinatorLayout? = null

    private var adapter: EpisodeListAdapter? = null

    private var footerAdapter: CustomLoadStateAdapter? = null

    private var searchQuery: String? = null

    @Inject
    internal lateinit var episodesViewModel: EpisodeListViewModel
    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<EpisodesComponentViewModel>()
            .episodesComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSearching()
    }

    private fun initSearching() {
        (requireActivity() as? SearchActivity)?.registerSearchFragment(this)
        searchQuery?.let { (requireActivity() as? SearchActivity)?.setSearchText(it) }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EpisodesFragmentBinding.inflate(layoutInflater)

        val coordinatorLayout = binding.coordinatorLayout
        val contentLayout: LinearLayout = coordinatorLayout.findViewById(R.id.contentLayout)

        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        iniCoordinatorLayout()
        return coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSwipeRefreshLayout()

        observeStates()
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            episodesViewModel.applyFilter()
            adapter?.refresh()
        }
    }

    private fun initRecyclerView() {
        adapter = EpisodeListAdapter {

        }
        val tryAgainAction: TryAgainAction = { adapter?.retry() }

        footerAdapter = CustomLoadStateAdapter(tryAgainAction)

        val loadStateAdapter = adapter?.withLoadStateFooter(footerAdapter!!)

        lifecycleScope.launch {
            binding.rvEpisodes.apply {
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeEpisodes()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)?.debounce(400)
                ?.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                    val isEmpty = (adapter?.snapshot()?.items?.size ?: 0) <= 0
                    binding.loadingStateLayout.tryAgainButton.setOnClickListener(::handleFilterReset)
                    when (val res = state.source.refresh) {
                        is LoadState.Error -> {}
                        is LoadState.Loading -> {
                            context.showToastShort(isEmpty)
                        }
                        is LoadState.NotLoading -> {}
                    }
                }
        }
    }

    private fun handleFilterReset(it: View) {
        (requireActivity() as? SearchActivity)?.closeSearchInterface()
        binding.loadingStateLayout.root.children.forEach { it.isVisible = false }
        episodesViewModel.clearAllFilters()
        resetFilters()
        adapter?.refresh()
    }

    private fun resetFilters() {
        binding.filterLayout.root.children.forEach { view ->
            when (view) {
                is TextInputLayout -> view.editText?.text = null
            }
        }
    }

    private suspend fun observeEpisodes() {
        episodesViewModel.state.value.episodesFlow?.flowWithLifecycle(lifecycle)
            ?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
            }
    }


    override fun doSearch(searchQuery: String?) {
        binding.swipeRefresh.isEnabled = false
        this.searchQuery = searchQuery
        episodesViewModel.applyFilter(name = searchQuery)
        adapter?.refresh()
    }

    override fun toggle(): Int {
        val state = sheetBehavior.state
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            with(binding) {
                rvEpisodes.visibility = ViewGroup.GONE
            }
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            with(binding) {
                rvEpisodes.visibility = ViewGroup.VISIBLE
            }
            setUpUiFilterValues()
            adapter?.refresh()
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    private fun setUpUiFilterValues() {
        with(binding.filterLayout) {
            episodesViewModel.applyFilter(
                name = etName.text.toString(),
                episode = etEpisode.text.toString()
            )
        }
    }

    private fun iniCoordinatorLayout() {
        coordinatorLayout = binding.coordinatorLayout
    }

    companion object {
        const val EPISODES_FRAGMENT_TAG = "EPISODES_FRAGMENT_TAG"
        fun createInstance(): EpisodeListFragment = EpisodeListFragment()
    }

}
