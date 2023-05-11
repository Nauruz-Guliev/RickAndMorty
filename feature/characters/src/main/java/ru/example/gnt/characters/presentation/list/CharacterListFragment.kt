package ru.example.gnt.characters.presentation.list

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.core.Scheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.example.gnt.characters.R
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
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
import ru.example.gnt.common.utils.extensions.internetCapabilitiesCallback
import ru.example.gnt.common.utils.CustomLoadStateAdapter
import ru.example.gnt.common.utils.TryAgainAction
import ru.example.gnt.common.utils.extensions.createChip
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.showToastShort
import javax.inject.Inject


class CharacterListFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
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
        (requireActivity() as? SearchActivity)?.registerSearchFragment(this)
        (requireActivity() as? ToggleActivity)?.registerToggleFragment(this)
        searchQuery?.let { (requireActivity() as? SearchActivity)?.setSearchText(it) }
        setExpanded()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CharactersFragmentBinding.inflate(layoutInflater)
        setUpCoordinatorLayout(R.id.contentLayout, binding.coordinatorLayout)
        return binding.coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initCoordinatorLayout()
        initChipGroup()
        initSwipeRefreshLayout()
        observeDataStates()
        observeInternetConnectionChanges()
        setUpInfoTextView()
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.applyFilter()
            adapter?.refresh()
        }
    }

    private fun initRecyclerView() {
        adapter = CharactersAdapter(viewModel::navigateToDetails)
        val tryAgainAction: TryAgainAction = { adapter?.retry() }
        val footerAdapter = CustomLoadStateAdapter(tryAgainAction)
        val loadStateAdapter = adapter?.withLoadStateFooter(footerAdapter)
        lifecycleScope.launch {
            binding.rvCharacters.apply {
                adapter = loadStateAdapter
                (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            }
            observeCharacters()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeDataStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)
                ?.debounce(400)?.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                    val isEmpty = (adapter?.snapshot()?.items?.size ?: 0) <= 0
                    binding.loadingStateLayout.tryAgainButton.setOnClickListener(::handleFilterReset)
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


    private fun handleNotLoadingState(isEmpty: Boolean) {
        with(binding) {
            with(loadingStateLayout) {
                messageTextView.apply {
                    isVisible = isEmpty
                    text =
                        if (viewModel.isFilterOn() && isInternetOn) getString(ru.example.gnt.ui.R.string.no_filter_results) else getString(
                            ru.example.gnt.ui.R.string.no_internet_connection
                        )
                }
                tryAgainButton.apply {
                    isVisible = isEmpty && viewModel.isFilterOn()
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
        lifecycleScope.launch(Dispatchers.Main) {
            networkState.collectLatest {
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
            setUpFilterValues()
            adapter?.refresh()
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    override fun setExpanded() {
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setUpFilterValues() {
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
        }
    }

    override fun doSearch(searchQuery: String?) {
        binding.swipeRefresh.isEnabled = false
        this.searchQuery = searchQuery
        viewModel.applyFilter(name = searchQuery)
        adapter?.refresh()
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
