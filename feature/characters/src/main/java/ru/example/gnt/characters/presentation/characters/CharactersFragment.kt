package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.flowWithLifecycle
import ru.example.gnt.common.scan
import ru.example.gnt.common.utils.extensions.asFlow
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.common.utils.interfaces.LayoutBackDropManager
import javax.inject.Inject


class CharactersFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
), LayoutBackDropManager {
    @Inject
    internal lateinit var viewModel: CharactersViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var coordinatorLayout: CoordinatorLayout? = null

    private var adapter: CharactersAdapter? = null
    private var footerAdapter: CustomLoadStateAdapter? = null

    private var resultsAmount: Int = 0


    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<CharactersComponentViewModel>()
            .charactersComponent.inject(this)
        super.onAttach(context)
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
        handleListVisibility()
        observeStates()
    }



    private fun initSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter?.refresh()
        }
    }

    private fun initRecyclerView() {
        adapter = CharactersAdapter()

        val tryAgainAction: TryAgainAction = { adapter!!.retry() }

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
    private fun observeStates() {
        lifecycleScope.launch {
            adapter?.loadStateFlow?.flowWithLifecycle(lifecycle)?.collectLatest {
                state ->
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                when(val res = state.refresh) {
                    is LoadState.Error -> {
                        context.showToastShort(res.error.localizedMessage)
                    }
                    is LoadState.Loading -> {

                    }
                    is LoadState.NotLoading -> {

                    }
                }
            }
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

    private suspend fun observeCharacters() {
        viewModel.uiState.value.charactersFlow?.flowWithLifecycle(lifecycle)
            ?.collectLatest { pagingData ->
                adapter?.submitData(pagingData)
            }
    }

    private fun observeFilterChanges() {
        with(binding.filterLayout) {
            viewModel.apply {
                etName.asFlow(::setNameFilter)
                etSpecies.asFlow(::setSpeciesFilter)
                etType.asFlow(::setTypeFilter)
            }
            chipStatusGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                for (id in checkedIds) {
                    val chip: Chip = group.findViewById(id)
                    viewModel.setStatusFilter(
                        CharacterStatusEnum.find(chip.text.toString())
                            ?: CharacterStatusEnum.UNKNOWN
                    )
                }
                if (checkedIds.isEmpty()) {
                    viewModel.clearAllFilters()
                }
                adapter?.refresh()
            }
            chipGenderGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                for (id in checkedIds) {
                    val chip: Chip = group.findViewById(id)
                    viewModel.setGenderFilter(
                        CharacterGenderEnum.find(chip.text.toString())
                            ?: CharacterGenderEnum.UNKNOWN
                    )
                }
                if (checkedIds.isEmpty()) {
                    viewModel.clearAllFilters()
                }
                adapter?.refresh()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleListVisibility() = lifecycleScope.launch {
        adapter?.let {
            getRefreshLoadStateFlow(it)
                .scan(count = 3)
                .collectLatest { (beforePrevious, previous, current) ->
                    binding.rvCharacters.isInvisible = current is LoadState.Error
                            || previous is LoadState.Error
                            || (beforePrevious is LoadState.Error
                            && previous is LoadState.NotLoading
                            && current is LoadState.Loading)
                }
        }
    }

    private fun initChipGroup() {
        binding.filterLayout.chipStatusGroup.removeAllViews()
        CharacterStatusEnum.values().forEach {
            binding.filterLayout.chipStatusGroup.addView(createChip(it))
        }
        binding.filterLayout.chipGenderGroup.removeAllViews()
        CharacterGenderEnum.values().forEach {
            binding.filterLayout.chipGenderGroup.addView(createChip(it))
        }
    }

    private fun createChip(enum: Enum<*>): Chip {
        return Chip(binding.root.context).apply {
            when (enum) {
                is CharacterStatusEnum -> {
                    text = enum.get
                    setChipBackgroundColorResource(enum.color.id)
                }
                is CharacterGenderEnum -> {
                    text = enum.n
                    setChipBackgroundColorResource(ru.example.gnt.ui.R.color.blue_rm_secondary)
                    setTextColor(
                        AppCompatResources.getColorStateList(
                            context,
                            ru.example.gnt.ui.R.color.blue_rm
                        ).defaultColor
                    )
                }
            }
            isCloseIconVisible = false
            isCheckable = true
        }
    }

    private fun initCoordinatorLayout() {
        coordinatorLayout = binding.coordinatorLayout
        coordinatorContentId = R.id.contentLayout
    }

    private fun getRefreshLoadStateFlow(adapter: PagingDataAdapter<*, *>): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
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


    override fun toggle() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            with(binding) {
                rvCharacters.visibility = ViewGroup.GONE
            }
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            with(binding) {
                rvCharacters.visibility = ViewGroup.VISIBLE
            }
        }
    }
}
