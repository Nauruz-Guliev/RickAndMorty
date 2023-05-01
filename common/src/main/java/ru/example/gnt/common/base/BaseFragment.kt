package ru.example.gnt.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB,
    @IdRes private val contentLayoutId: Int? = null
) : Fragment() {

    protected var _binding: VB? = null
    protected val binding: VB by lazy { _binding!! }

    protected var coordinatorLayout: CoordinatorLayout? = null

    protected var sheetBehavior: BottomSheetBehavior<LinearLayout>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = bindingInflater.invoke(layoutInflater)
        return binding.root
    }

    protected fun setUpCoordinatorLayout(
        id: Int,
        coordinatorLayout: CoordinatorLayout
    ): CoordinatorLayout {
        _binding = bindingInflater.invoke(layoutInflater)
        val contentLayout: LinearLayout = coordinatorLayout.findViewById(id)
        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior?.isFitToContents = false
        sheetBehavior?.isHideable = false
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        this.coordinatorLayout = coordinatorLayout
        return coordinatorLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
