package ru.example.gnt.characters.presentation.characters

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.common.Resource
import ru.example.gnt.common.UiState
import javax.inject.Inject

internal class CharactersViewModel @Inject constructor(
    private val repository: CharactersRepository,
    private val navigator: CharactersRouter
) : ViewModel() {

    private val _state: MutableStateFlow<UiState<CharactersUiModel>> =
        MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

    init {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repository.getAllCharacters().distinctUntilChanged().collectLatest { result ->
                result.onFailure {
                    _state.value =
                        UiState.Error(Resource.String(ru.example.gnt.ui.R.string.network_error))
                    Log.d("EXCEPTION", it.message.toString())
                }.onSuccess { characters ->
                    _state.value = UiState.Success(characters)
                }
            }
        }
    }

    fun navigateToDetails(id: Int) {
        navigator.openCharacterDetails(id)
    }
}
