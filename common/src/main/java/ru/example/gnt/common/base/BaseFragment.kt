package ru.example.gnt.common.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.utils.extensions.internetCapabilitiesCallback
import ru.example.gnt.common.utils.extensions.isNetworkOn
import ru.example.gnt.common.utils.extensions.showToastShort

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB,
    @IdRes private val contentLayoutId: Int? = null
) : Fragment() {

    protected var _binding: VB? = null
    protected val binding: VB by lazy { _binding!! }

    protected var coordinatorLayout: CoordinatorLayout? = null

    protected var sheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    protected var isInternetOn = true
    protected var networkState = MutableStateFlow(isInternetOn)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(layoutInflater)
        isInternetOn = requireContext().isNetworkOn()
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
        observeInternetConnectionChanges()
        isInternetOn = requireContext().isNetworkOn()
        return coordinatorLayout
    }


    private fun observeInternetConnectionChanges() {
        lifecycleScope.launch {
            requireActivity().internetCapabilitiesCallback().flowWithLifecycle(lifecycle).collectLatest {
                isInternetOn = context?.isNetworkOn() ?: it
                networkState.emit(isInternetOn)
            }
        }
    }

    protected fun handleErrorState(ex: Throwable) {
        when (ex) {
            is ApplicationException -> {
                val message = ex.resource?.getValue(requireContext())
                if (message != null)
                    context.showToastShort(message)
            }
            else -> {
                Log.e("ERROR", ex.message.toString())
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
