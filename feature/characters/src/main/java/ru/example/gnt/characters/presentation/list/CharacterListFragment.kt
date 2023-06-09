package ru.example.gnt.characters.presentation.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.example.gnt.characters.R
import ru.example.gnt.characters.databinding.CharacterListFragmentBinding
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.characters.presentation.list.paging_recyclerview.CharactersAdapter
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.interfaces.ToggleActivity
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.utils.CustomLoadStateAdapter
import ru.example.gnt.common.utils.TryAgainAction
import ru.example.gnt.common.utils.extensions.createChip
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.showToastShort
import javax.inject.Inject


class CharacterListFragment : BaseFragment<CharacterListFragmentBinding>(
    CharacterListFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment, RootFragment {

    @Inject
    internal lateinit var viewModel: CharacterListViewModel
    private var adapter: CharactersAdapter? = null
    private var searchQuery: String? = null


    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<CharactersComponentViewModel>()
            .charactersComponent.inject(this)
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
        _binding = CharacterListFragmentBinding.inflate(layoutInflater)
        setUpCoordinatorLayout(R.id.contentLayout, binding.coordinatorLayout)
        return binding.coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshing()
        initRecyclerView()
        initCoordinatorLayout()
        initChipGroup()
        initSwipeRefreshLayout()
        initFilterButtons()
        observeDataStates()
        observeInternetConnectionChanges()
        setUpInfoTextView()
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.applyFilter()
            adapter?.refresh()
            if (!isInternetOn) {
                binding.root.context.showToastShort(getString(ru.example.gnt.ui.R.string.no_internet_connection))
            }
        }
    }

    private fun initFilterButtons() {
        with(binding.filterLayout.filterButtons) {
            btnApply.setOnClickListener {
                initFilterValues()
                setExpanded()
                adapter?.refresh()
            }
            btnClear.setOnClickListener {
                if(viewModel.isFilterOff()) {
                    requireContext().showToastShort(getString(ru.example.gnt.ui.R.string.empty_filters_message))
                } else {
                    viewModel.clearAllFilters()
                }
                resetAllUiFilters()
                adapter?.refresh()
                setExpanded()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = CharactersAdapter(viewModel::navigateToDetails)
        val tryAgainAction: TryAgainAction = { adapter?.retry() }
        val footerAdapter = CustomLoadStateAdapter(tryAgainAction)
        val loadStateAdapter = adapter?.withLoadStateFooter(footerAdapter)
        lifecycleScope.launch {
            binding.rvCharacters.apply {
                addItemDecoration(DividerItemDecoration(context, MaterialDividerItemDecoration.HORIZONTAL))
                addItemDecoration(DividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeCharacterList()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeDataStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)?.distinctUntilChanged()
                ?.debounce(1000)?.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                    val isEmpty = (adapter?.snapshot()?.items?.size ?: 0) <= 0
                    binding.loadingStateLayout.btnTryAgain.setOnClickListener(::handleFilterReset)
                    when (val res = state.source.refresh) {
                        is LoadState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            handleErrorState(res.error)
                        }
                        is LoadState.Loading -> {
                            setRefreshing()
                        }
                        is LoadState.NotLoading -> {
                            handleNotLoadingState(isEmpty)
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
        }
    }




    private fun handleNotLoadingState(isEmpty: Boolean) {
        with(binding) {
            with(loadingStateLayout) {
                tvMessage.apply {
                    isVisible = isEmpty
                    text = if(!viewModel.isFilterOff()) getString(ru.example.gnt.ui.R.string.no_filter_results)
                    else getString(ru.example.gnt.ui.R.string.no_data_available_error)
                }
                btnTryAgain.apply {
                    isVisible = isEmpty && !viewModel.isFilterOff()
                    text = getString(ru.example.gnt.ui.R.string.clear_filter)
                }
            }
            swipeRefresh.isVisible = !isEmpty
            swipeRefresh.isEnabled = !isEmpty
        }
    }


    private fun handleFilterReset(it: View) {
        (requireActivity() as? SearchActivity)?.closeSearchInterface()
        binding.loadingStateLayout.root.children.forEach { child -> child.isVisible = false }
        viewModel.clearAllFilters()
        resetAllUiFilters()
        adapter?.refresh()
    }

    private suspend fun observeCharacterList() {
        viewModel.uiState.value.charactersFlow?.flowWithLifecycle(lifecycle)
            ?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
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
            networkState.flowWithLifecycle(lifecycle).collectLatest {
                setUpInfoTextView()
            }
        }
    }

    private fun setUpInfoTextView() {
        binding.tvInfo.text =
            if (isInternetOn) getString(ru.example.gnt.ui.R.string.characters_welcome_message)
            else getString(ru.example.gnt.ui.R.string.not_connected_ui_message)
    }


    override fun toggle(): Int? {
        val state = sheetBehavior?.state
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    override fun setExpanded() {
        (requireActivity() as? ToggleActivity)?.setFragmentExpanded()
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initFilterValues() {
        with(binding.filterLayout) {
            viewModel.applyFilter(
                name = etName.text.toString(),
                species = etSpecies.text.toString(),
                type = etType.text.toString(),
                gender = CharacterGenderEnum.find(
                    ((chipGenderGroup.children.toList()
                        .firstOrNull { (it as Chip).isChecked }) as? Chip)?.text.toString()
                ),
                status = CharacterStatusEnum.find(
                    ((chipStatusGroup.children.toList()
                        .firstOrNull { (it as Chip).isChecked }) as? Chip)?.text.toString()
                ),
            )
            setRefreshing()
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
                        root.isVisible = true
                        btnTryAgain.isVisible = false
                        tvInfo.text = message
                    }
                }
                else -> {
                    root.context.showToastShort(ex.message ?: ex.cause?.message)
                }
            }
        }
    }

    override fun doSearch(searchQuery: String?) {
        binding.swipeRefresh.isEnabled = false
        this.searchQuery = searchQuery
        viewModel.applyFilter(name = searchQuery)
        adapter?.refresh()
    }

    private fun setRefreshing() {
        binding.swipeRefresh.isRefreshing = true
    }


    companion object {
        const val CHARACTERS_FRAGMENT_TAG: String = "CHARACTERS_FRAGMENT_TAG"

        @IdRes
        var coordinatorContentId: Int? = null

        fun createInstance() = CharacterListFragment().apply {
            //пока сюда передавать нечего
            arguments = Bundle()
        }
    }
}
