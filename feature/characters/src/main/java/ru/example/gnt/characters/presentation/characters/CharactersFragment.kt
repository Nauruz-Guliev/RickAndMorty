package ru.example.gnt.characters.presentation.characters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.characters.databinding.CharactersFragmentBinding
import ru.example.gnt.characters.di.CharactersComponent
import ru.example.gnt.characters.di.DaggerCharactersComponent
import ru.example.gnt.characters.di.provider.CharactersComponentViewModel
import ru.example.gnt.common.UiState
import ru.example.gnt.common.base.BaseFragment
import javax.inject.Inject

class CharactersFragment : BaseFragment<CharactersFragmentBinding>(
    CharactersFragmentBinding::inflate
) {
    @Inject
    internal lateinit var viewModel: CharactersViewModel

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<CharactersComponentViewModel>()
            .charactersComponent.inject(this)
        super.onAttach(context)
    }

    private lateinit var charactersComponent: CharactersComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest {state ->
                when(state) {
                    is UiState.Success -> {
                       // Toast.makeText(context, state.data.singles[0].name, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initRecyclerView() {

    }

    companion object {
        const val CHARACTERS_FRAGMENT_TAG: String = "CHARACTERS_FRAGMENT_TAG"
        fun createInstance() = CharactersFragment().apply {
            //пока сюда передавать нечего
            arguments = Bundle()
        }
    }
}

fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}
