package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.example.gnt.characters.R
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.characters.presentation.characters.paging_recyclerview.CharactersAdapter
import ru.example.gnt.characters.presentation.characters.paging_recyclerview.CustomLoadStateAdapter
import ru.example.gnt.characters.presentation.characters.paging_recyclerview.TryAgainAction
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.exceptions.NetworkConnectionException
import ru.example.gnt.common.flowWithLifecycle
import ru.example.gnt.common.internetCapabilitiesCallback
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.scan
import ru.example.gnt.common.utils.extensions.asFlow
import ru.example.gnt.common.utils.extensions.createChip
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.common.utils.interfaces.LayoutBackDropManager
import javax.inject.Inject


class CharactersFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment {
    @Inject
    internal lateinit var viewModel: CharactersViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var coordinatorLayout: CoordinatorLayout? = null

    private var adapter: CharactersAdapter? = null
    private var footerAdapter: CustomLoadStateAdapter? = null

    private var isInternetOn: Boolean = false

    private var searchQuery: String? = null

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<CharactersComponentViewModel>()
            .charactersComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isInternetOn = requireContext().isNetworkOn()

        (requireActivity() as? SearchActivity)?.registerSearchFragment(this)
        searchQuery?.let { (requireActivity() as? SearchActivity)?.setSearchText(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CharactersFragmentBinding.inflate(layoutInflater)

        val coordinatorLayout = binding.coordinatorLayout
        val contentLayout: LinearLayout = coordinatorLayout.findViewById(R.id.contentLayout)

        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        return coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initCoordinatorLayout()
        initChipGroup()
        initSwipeRefreshLayout()

        observeFilterChanges()
        handleScrollingToTop()
        observeStates()
        observeInternetConnectionChanges()
        setUpUiState()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? SearchActivity)?.showSearchView(true)
    }



    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.applyFilter()
            adapter?.refresh()
        }
    }

    private fun initRecyclerView() {
        adapter = CharactersAdapter()
        val tryAgainAction: TryAgainAction = { adapter?.retry() }
        footerAdapter = CustomLoadStateAdapter(tryAgainAction)
        val loadStateAdapter = adapter!!.withLoadStateFooter(footerAdapter!!)
        lifecycleScope.launch {
            binding.rvCharacters.apply {
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeCharacters()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)
                ?.debounce(400)?.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                    val isEmpty = (adapter?.snapshot()?.items?.size ?: 0) <= 0
                    binding.loadingStateLayout.tryAgainButton.setOnClickListener(::handleFilterReset)
                    when (val res = state.source.refresh) {
                        is LoadState.Error -> handleErrorState(res.error)
                        is LoadState.Loading -> { context.showToastShort(isEmpty) }
                        is LoadState.NotLoading -> handleNotLoadingState(isEmpty)
                    }
                }
        }
    }

    private fun handleErrorState(ex: Throwable) {
        var i = 0
        when (ex) {
            is NetworkConnectionException -> {

            }
        }
    }

    private fun handleNotLoadingState(isEmpty: Boolean) {
        with(binding) {
            with(loadingStateLayout) {
                messageTextView.apply {
                    isVisible = isEmpty
                    text = getString(R.string.no_filter_results)
                }
                tryAgainButton.apply {
                    isVisible = isEmpty
                    text = getString(R.string.clear_filter)
                }
            }
            swipeRefresh.isVisible = !isEmpty
            swipeRefresh.isEnabled = !isEmpty
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleScrollingToTop() = lifecycleScope.launch {
        adapter?.let {
            getRefreshLoadStateFlow(it)
                .scan(count = 2)
                .collect { (previousState, currentState) ->
                    if (previousState is LoadState.Loading
                        && currentState is LoadState.NotLoading
                    ) {
                        delay(200)
                        binding.rvCharacters.scrollToPosition(0)
                    }
                }
        }
    }

    private fun handleFilterReset(it: View) {
        (activity as SearchActivity).setSearchText("")
        binding.loadingStateLayout.root.children.forEach { it.isVisible = false }
        viewModel.clearAllFilters()
        resetAllUiFilters()
        adapter?.refresh()
    }

    private suspend fun observeCharacters() {
        viewModel.uiState.value.charactersFlow?.flowWithLifecycle(lifecycle)
            ?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
            }
    }

    //TODO cleanup
    private fun observeFilterChanges() {
        with(binding.filterLayout) {
            viewModel.apply {
                etName.asFlow {
                    applyFilter(name = it)
                    adapter?.refresh()
                }
                etSpecies.asFlow {
                    applyFilter(species = it)
                    adapter?.refresh()
                }
                etType.asFlow {
                    applyFilter(type = it)
                    adapter?.refresh()
                }
            }
            chipStatusGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                for (id in checkedIds) {
                    val chip: Chip = group.findViewById(id)
                    viewModel.applyFilter(
                        status = CharacterStatusEnum.find(chip.text.toString())
                            ?: CharacterStatusEnum.UNKNOWN
                    )
                }
                if (checkedIds.isEmpty()) {
                    viewModel.applyFilter(status = null)
                }
                adapter?.refresh()
            }
            chipGenderGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                for (id in checkedIds) {
                    val chip: Chip = group.findViewById(id)
                    viewModel.applyFilter(
                        gender = CharacterGenderEnum.find(chip.text.toString())
                            ?: CharacterGenderEnum.UNKNOWN
                    )
                }
                if (checkedIds.isEmpty()) {
                    viewModel.applyFilter(gender = null)
                }
                adapter?.refresh()
            }
        }
    }


    private fun initChipGroup() {
        with(binding.filterLayout) {
            chipStatusGroup.removeAllViews()
            CharacterStatusEnum.values().forEach {
                chipStatusGroup.addView(chipGenderGroup.createChip(it))
            }
            chipGenderGroup.removeAllViews()
            CharacterGenderEnum.values().forEach {
                chipGenderGroup.addView(chipStatusGroup.createChip(it))
            }
        }
    }

    private fun initCoordinatorLayout() {
        coordinatorLayout = binding.coordinatorLayout
        coordinatorContentId = R.id.contentLayout
    }

    private fun resetAllUiFilters() {
        binding.filterLayout.root.children.forEach { view ->
            when (view) {
                is TextInputLayout -> view.editText?.text = null
                is ChipGroup -> view.clearCheck()
            }
        }
    }

    private fun observeInternetConnectionChanges() {
        lifecycleScope.launch {
            context?.internetCapabilitiesCallback()?.flowWithLifecycle(lifecycle)?.collectLatest {
                isInternetOn = it
                setUpUiState()
                when (it) {
                    true -> {
                        viewModel.applyFilter()
                        adapter?.refresh()
                    }
                    false -> {

                    }
                }
            }
        }
    }

    private fun setUpUiState() {
        binding.swipeRefresh.isEnabled = isInternetOn
        binding.tvInformational.text =
            if (isInternetOn) getString(ru.example.gnt.ui.R.string.characters_welcome_message)
            else getString(R.string.not_connected_ui_message)
    }

    private fun getRefreshLoadStateFlow(adapter: PagingDataAdapter<*, *>): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }


    override fun toggle(): Int {
        val state = sheetBehavior.state
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            with(binding) {
                rvCharacters.visibility = ViewGroup.GONE
            }
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            with(binding) {
                rvCharacters.visibility = ViewGroup.VISIBLE
            }
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    override fun doSearch(searchQuery: String) {
        binding.swipeRefresh.isEnabled = false
        this.searchQuery = searchQuery
        viewModel.applyFilter(name = searchQuery)
        adapter?.refresh()
    }


    companion object {
        const val CHARACTERS_FRAGMENT_TAG: String = "CHARACTERS_FRAGMENT_TAG"

        @IdRes
        var coordinatorContentId: Int? = null

        fun createInstance() = CharactersFragment().apply {
            //пока сюда передавать нечего
            arguments = Bundle()
        }
    }
}
