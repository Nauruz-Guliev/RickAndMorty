package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
        listenForFilterChanges()
    }

    private fun initRecyclerView() {
        adapter = CharactersAdapter()

        val tryAgainAction: TryAgainAction = { adapter!!.retry() }

        val footerAdapter = CustomLoadStateAdapter(tryAgainAction)

        val adapterWithLoadState = adapter!!.withLoadStateFooter(footerAdapter)

        binding.rvCharacters.apply {
            adapter = adapterWithLoadState
        }
        observeCharacters()
    }

    private fun observeCharacters() {
        lifecycleScope.launch {
            viewModel.state
                .collectLatest { pagingData ->
                    adapter?.submitData(pagingData)
                }
        }
    }

    private fun listenForFilterChanges() {
        binding.chipStatusGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            for (id in checkedIds) {
                val chip: Chip = group.findViewById(id)
                viewModel.setStatusFilter(CharacterStatusEnum.find(chip.text.toString()))
            }
            if (checkedIds.isEmpty()) {
                //    viewModel.loadAllCharacters()
            }
        }
        binding.chipGenderGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            for (id in checkedIds) {
                val chip: Chip = group.findViewById(id)
                viewModel.setGenderFilter(CharacterGenderEnum.find(chip.text.toString()))
            }
            if (checkedIds.isEmpty()) {
                //    viewModel.loadAllCharacters()
            }
        }
    }

    private fun initChipGroup() {
        binding.chipStatusGroup.removeAllViews()
        CharacterStatusEnum.values().forEach {
            binding.chipStatusGroup.addView(createChip(it))
        }
        binding.chipGenderGroup.removeAllViews()
        CharacterGenderEnum.values().forEach {
            binding.chipGenderGroup.addView(createChip(it))
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
                    setTextColor(AppCompatResources.getColorStateList(context,ru.example.gnt.ui.R.color.blue_rm).defaultColor)
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


    private fun disableProgressIndicator() {
        binding.progressIndicator.apply {
            isIndeterminate = false
            visibility = ViewGroup.GONE
        }

    }

    private fun enableProgressIndicator() {
        binding.progressIndicator.apply {
            isIndeterminate = true
            visibility = ViewGroup.VISIBLE
        }
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
            binding.rvCharacters.visibility = ViewGroup.GONE
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            binding.rvCharacters.visibility = ViewGroup.VISIBLE
        }
    }
}
