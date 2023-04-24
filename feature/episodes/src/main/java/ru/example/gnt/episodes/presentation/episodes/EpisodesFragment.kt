package ru.example.gnt.episodes.presentation.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.example.gnt.common.base.BaseFragment
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.databinding.EpisodesFragmentBinding

class EpisodesFragment : BaseFragment<EpisodesFragmentBinding>(
    EpisodesFragmentBinding::inflate
), LayoutBackDropManager, SearchFragment, RootFragment {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var coordinatorLayout: CoordinatorLayout? = null


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
        return coordinatorLayout
    }


    override fun doSearch(searchQuery: String?) {
        TODO("Not yet implemented")
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
        }
        context?.hideKeyboard(binding.root)
        return state
    }

    companion object {
        const val EPISODES_FRAGMENT_TAG = "EPISODES_FRAGMENT_TAG"
        fun createInstance(): EpisodesFragment = EpisodesFragment()
    }

}
