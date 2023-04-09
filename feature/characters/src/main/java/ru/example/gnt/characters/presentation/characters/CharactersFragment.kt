package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
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
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.characters.R
import ru.example.gnt.characters.databinding.CharacterItemBinding
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.presentation.characters.recyclerview.BaseRecyclerViewManager
import ru.example.gnt.characters.presentation.characters.recyclerview.RecyclerViewAction
import ru.example.gnt.common.LayoutBackDropManager
import ru.example.gnt.common.UiState
import ru.example.gnt.common.base.BaseFragment
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
            sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
            binding.rvCharacters.alpha = 0.3F
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            binding.rvCharacters.alpha = 1F
        }
    }
}
