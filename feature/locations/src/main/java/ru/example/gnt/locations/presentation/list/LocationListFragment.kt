package ru.example.gnt.locations.presentation.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
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
import ru.example.gnt.locations.R
import ru.example.gnt.locations.databinding.LocationListFragmentBinding
import ru.example.gnt.locations.di.LocationsComponentViewModel
import ru.example.gnt.locations.presentation.list.rv_paging.LocationListAdapter
import javax.inject.Inject

class LocationListFragment : BaseFragment<LocationListFragmentBinding>(
    LocationListFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment, RootFragment {
    private var adapter: LocationListAdapter? = null
    private var searchQuery: String? = null
    @Inject
    internal lateinit var locationsViewModel: LocationListViewModel

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<LocationsComponentViewModel>()
            .locationComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LocationListFragmentBinding.inflate(layoutInflater)
        setUpCoordinatorLayout(R.id.contentLayout, binding.coordinatorLayout)
        return binding.coordinatorLayout
    }

    override fun onResume() {
        super.onResume()
        searchQuery?.let { (requireActivity() as? SearchActivity)?.setSearchText(it) }
        setExpanded()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshing()
        initRecyclerView()
        initFilterButtons()
        initSwipeRefreshLayout()
        observePaginationStates()
        initCoordinatorLayout()
        observeInternetState()
    }

    private fun initCoordinatorLayout() {
        coordinatorLayout = binding.coordinatorLayout
        coordinatorContentId = R.id.contentLayout
    }



    private fun initFilterButtons() {
        with(binding.filterLayout.filterButtons) {
            btnApply.setOnClickListener {
                initFilterValues()
                setExpanded()
                adapter?.refresh()
            }
            btnClear.setOnClickListener {
                if(locationsViewModel.isFilterOff()) {
                    requireContext().showToastShort(getString(ru.example.gnt.ui.R.string.empty_filters_message))
                } else {
                    locationsViewModel.clearAllFilters()
                }
                resetFilters()
                adapter?.refresh()
                setExpanded()
            }
        }
    }
    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            locationsViewModel.applyFilter()
            adapter?.refresh()
            if(!isInternetOn) {
                binding.root.context.showToastShort(getString(ru.example.gnt.ui.R.string.no_internet_connection))
            }
        }
    }

    private fun onItemClicked(id: Int) {
        locationsViewModel.navigateToDetailsFragment(id)
    }

    private fun initRecyclerView() {
        adapter = LocationListAdapter(::onItemClicked)
        val tryAgainAction: TryAgainAction = { adapter?.retry() }
        val footerAdapter = CustomLoadStateAdapter(tryAgainAction)
        val loadStateAdapter = adapter?.withLoadStateFooter(footerAdapter)
        lifecycleScope.launch {
            binding.rvLocations.apply {
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeLocations()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observePaginationStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)?.distinctUntilChanged()?.debounce(400)
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
                    text = if(!locationsViewModel.isFilterOff()) getString(ru.example.gnt.ui.R.string.no_filter_results)
                    else getString(ru.example.gnt.ui.R.string.no_data_available_error)
                }
                btnTryAgain.apply {
                    isVisible = isEmpty && !locationsViewModel.isFilterOff()
                    text = getString(ru.example.gnt.ui.R.string.clear_filter)
                }
            }
            swipeRefresh.isVisible = !isEmpty
            swipeRefresh.isEnabled = !isEmpty
        }
    }
    private suspend fun observeLocations() {
        locationsViewModel.state.value.locationsFlow?.flowWithLifecycle(lifecycle)
            ?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
            }
    }

    private fun handleFilterReset(it: View) {
        (requireActivity() as? SearchActivity)?.closeSearchInterface()
        binding.loadingStateLayout.root.children.forEach { it.isVisible = false }
        locationsViewModel.clearAllFilters()
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

    private fun setUpInfoTextView() {
        binding.tvInfo.text =
            if (isInternetOn) getString(ru.example.gnt.ui.R.string.characters_welcome_message)
            else getString(ru.example.gnt.ui.R.string.not_connected_ui_message)
    }


    override fun doSearch(searchQuery: String?) {
        binding.swipeRefresh.isEnabled = false
        this.searchQuery = searchQuery
        locationsViewModel.applyFilter(name = searchQuery)
        adapter?.refresh()
    }

    private fun observeInternetState() {
        lifecycleScope.launch {
            networkState.flowWithLifecycle(lifecycle).collectLatest {
                setUpInfoTextView()
            }
        }
    }


    override fun toggle(): Int? {
        val state = sheetBehavior?.state
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            (requireActivity() as? ToggleActivity)?.setFragmentCollapsed()
            binding.rvLocations.alpha = 0.3F

        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            (requireActivity() as? ToggleActivity)?.setFragmentExpanded()
            binding.rvLocations.alpha = 1F
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    override fun setExpanded() {
        (requireActivity() as? ToggleActivity)?.setFragmentExpanded()
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        binding.rvLocations.alpha = 1F
    }

    private fun initFilterValues() {
        with(binding.filterLayout) {
            locationsViewModel.applyFilter(
                name = etName.text.toString(),
                dimension = etDimension.text.toString(),
                type = etType.text.toString()
            )
        }
    }

    private fun setRefreshing() {
        binding.swipeRefresh.isRefreshing = true
    }


    companion object {
        const val LOCATION_LIST_FRAGMENT_TAG = "LOCATION_LIST_FRAGMENT_TAG"


        @IdRes
        var coordinatorContentId: Int? = null
        fun createInstance(): LocationListFragment = LocationListFragment().apply {
            arguments = bundleOf()
        }
    }
}
