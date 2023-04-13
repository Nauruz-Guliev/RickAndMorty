package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.characters.R
import ru.example.gnt.characters.databinding.CharacterItemBinding
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.common.model.ui.CharactersUiModel
import ru.example.gnt.characters.presentation.characters.recyclerview.BaseRecyclerViewManager
import ru.example.gnt.characters.presentation.characters.recyclerview.RecyclerViewAction
import ru.example.gnt.common.utils.interfaces.LayoutBackDropManager
import ru.example.gnt.common.model.UiState
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import javax.inject.Inject


class CharactersFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
), LayoutBackDropManager {
    @Inject
    internal lateinit var viewModel: CharactersViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var coordinatorLayout: CoordinatorLayout? = null

    private var adapter: BaseRecyclerViewManager<CharactersUiModel.Single, CharacterItemBinding>.RecyclerViewAdapter? =
        null


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
        observeStateChanges()
        initChipGroup()
        listenForFilterChanges()
    }

    private fun listenForFilterChanges() {
        binding.chipStatusGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            for (id in checkedIds) {
                val chip: Chip = group.findViewById(id)
                viewModel.setStatusFilter(CharacterStatusEnum.find(chip.text.toString()))
            }
            if (checkedIds.isEmpty()) {
                viewModel.loadAllCharacters()
            }
        }
        binding.chipGenderGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            for (id in checkedIds) {
                val chip: Chip = group.findViewById(id)
                viewModel.setGenderFilter(CharacterGenderEnum.find(chip.text.toString()))
            }
            if (checkedIds.isEmpty()) {
                viewModel.loadAllCharacters()
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

    private fun observeStateChanges() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter?.submitList(state.data.singles)
                        disableProgressIndicator()
                    }
                    is UiState.Loading -> {
                        enableProgressIndicator()
                    }
                    else -> {
                        disableProgressIndicator()
                    }
                }
            }
        }
    }

    private fun initCoordinatorLayout() {
        coordinatorLayout = binding.coordinatorLayout
        coordinatorContentId = R.id.contentLayout
    }

    private fun initRecyclerView() {
        BaseRecyclerViewManager<CharactersUiModel.Single, CharacterItemBinding>(
            onBindAction = { binding, item ->
                with(binding) {
                    Glide.with(binding.root.context)
                        .load(item.image)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {

                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(binding.ivCharacter)
                    tvStatus.width = ivCharacter.maxWidth
                    tvName.text = item.name
                    tvGender.text = item.gender.n
                    tvSpecies.text = item.species
                    tvStatus.text = item.status.get
                    tvStatus.setBackgroundColor(item.status.color.getValue(binding.root.context).defaultColor)
                }
            },
            actionListener = object : RecyclerViewAction {
                override fun onItemClicked(id: Int) {
                    viewModel.navigateToDetails(id)
                }
            },
            itemContainerId = R.layout.character_item
        ).adapter.also { newAdapter ->
            adapter = newAdapter
            binding.rvCharacters.adapter = newAdapter
            binding.rvCharacters.addItemDecoration(
                MaterialDividerItemDecoration(
                    binding.root.context,
                    DividerItemDecoration.HORIZONTAL
                )
            )
            binding.rvCharacters.addItemDecoration(
                MaterialDividerItemDecoration(
                    binding.root.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
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
