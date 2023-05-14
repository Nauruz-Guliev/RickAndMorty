package ru.example.gnt.episodes.presentation.episode_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.interfaces.ToggleActivity
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.utils.CustomLoadStateAdapter
import ru.example.gnt.common.utils.TryAgainAction
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.databinding.EpisodeListFragmentBinding
import ru.example.gnt.episodes.di.deps.EpisodesComponentViewModel
import ru.example.gnt.episodes.presentation.episode_list.paging_rv.EpisodeListAdapter
import javax.inject.Inject

class EpisodeListFragment : BaseFragment<EpisodeListFragmentBinding>(
    EpisodeListFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment, RootFragment {

    private var adapter: EpisodeListAdapter? = null
    private var searchQuery: String? = null

    @Inject
    internal lateinit var episodesViewModel: EpisodeListViewModel
    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<EpisodesComponentViewModel>()
            .episodesComponent.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        searchQuery?.let { (requireActivity() as? SearchActivity)?.setSearchText(it) }
        setExpanded()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EpisodeListFragmentBinding.inflate(layoutInflater)
        setUpCoordinatorLayout(R.id.contentLayout, binding.coordinatorLayout)
        return binding.coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshing()
        initRecyclerView()
        initFilterButtons()
        initSwipeRefreshLayout()
        observePaginationStates()
        observeInternetState()
    }

    private fun initFilterButtons() {
        with(binding.filterLayout.filterButtons) {
            btnApply.setOnClickListener {
                initFilterValues()
                setExpanded()
                adapter?.refresh()
            }
            btnClear.setOnClickListener {
                resetFilters()
                if(episodesViewModel.isFilterOff()) {
                    requireContext().showToastShort(getString(ru.example.gnt.ui.R.string.empty_filters_message))
                } else {
                    episodesViewModel.clearAllFilters()
                }
                adapter?.refresh()
                setExpanded()
            }
        }
    }

    private fun observeInternetState() {
        lifecycleScope.launch {
            networkState.flowWithLifecycle(lifecycle).collectLatest {
                setUpInfoTextView()
            }
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            episodesViewModel.applyFilter()
            adapter?.refresh()
            if(!isInternetOn) {
                binding.root.context.showToastShort(getString(ru.example.gnt.ui.R.string.no_internet_connection))
            }
        }
    }

    private fun onItemClicked(id: Int?) {
        episodesViewModel.navigateToEpisodeDetails(id)
    }

    private fun initRecyclerView() {
        adapter = EpisodeListAdapter(::onItemClicked)
        val tryAgainAction: TryAgainAction = { adapter?.retry() }
        val footerAdapter = CustomLoadStateAdapter(tryAgainAction)
        val loadStateAdapter = adapter?.withLoadStateFooter(footerAdapter)
        lifecycleScope.launch {
            binding.rvEpisodes.apply {
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeEpisodes()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observePaginationStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)?.debounce(1000)
                ?.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                    val isEmpty = (adapter?.snapshot()?.items?.size ?: 0) <= 0
                    binding.loadingStateLayout.btnTryAgain.setOnClickListener(::handleFilterReset)
                    when (val res = state.source.refresh) {
                        is LoadState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            handleErrorState(res.error)
                        }
                        is LoadState.Loading -> {
                            binding.swipeRefresh.isRefreshing = true
                        }
                        is LoadState.NotLoading -> {
                            handleNotLoadingState(isEmpty)
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
        }
    }

    private fun handleErrorState(ex: Throwable) {
        with(binding) {
            when (ex) {
                is ApplicationException -> {
                    val message =
                        ex.resource?.getValue(root.context) ?: ex.cause?.message
                        ?: ex.message
                        ?: getString(ru.example.gnt.ui.R.string.unknown_data_access_error)
                    mainLayout.isVisible = false
                    with(loadingStateLayout) {
                        if (ex is ApplicationException.BackendException) {
                            tvMessage.text = ex.code.toString() + "\n" + message
                        } else {
                            tvMessage.text = message
                        }
                    }
                }
                else -> {
                    root.context.showToastShort(ex.message ?: ex.cause?.message)
                }
            }
        }
    }


    private fun handleNotLoadingState(isEmpty: Boolean) {
        with(binding) {
            with(loadingStateLayout) {
                tvMessage.apply {
                    isVisible = isEmpty
                    text = if(!episodesViewModel.isFilterOff()) getString(ru.example.gnt.ui.R.string.no_filter_results)
                    else getString(ru.example.gnt.ui.R.string.no_data_available_error)
                }
                btnTryAgain.apply {
                    isVisible = isEmpty && !episodesViewModel.isFilterOff()
                    text = getString(ru.example.gnt.ui.R.string.clear_filter)
                }
            }
            swipeRefresh.isVisible = !isEmpty
            swipeRefresh.isEnabled = !isEmpty
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
            ?.distinctUntilChanged()
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

    override fun toggle(): Int? {
        val state = sheetBehavior?.state
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            (requireActivity() as? ToggleActivity)?.setFragmentCollapsed()
            binding.rvEpisodes.alpha = 0.3F

        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            (requireActivity() as? ToggleActivity)?.setFragmentCollapsed()
            binding.rvEpisodes.alpha = 1F
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    private fun setUpInfoTextView() {
        binding.tvInformational.text =
            if (isInternetOn) getString(ru.example.gnt.ui.R.string.characters_welcome_message)
            else getString(ru.example.gnt.ui.R.string.not_connected_ui_message)
    }

    override fun setExpanded() {
        (requireActivity() as? ToggleActivity)?.setFragmentExpanded()
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        binding.rvEpisodes.alpha = 1F
    }

    private fun initFilterValues() {
        with(binding.filterLayout) {
            episodesViewModel.applyFilter(
                name = etName.text.toString(),
                episode = etEpisode.text.toString()
            )
        }
    }

    private fun setRefreshing() {
        binding.swipeRefresh.isRefreshing = true
    }

    companion object {
        const val EPISODES_FRAGMENT_TAG = "EPISODES_FRAGMENT_TAG"
        fun createInstance(): EpisodeListFragment = EpisodeListFragment()
    }

}
