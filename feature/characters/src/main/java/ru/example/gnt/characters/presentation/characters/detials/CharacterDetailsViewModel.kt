package ru.example.gnt.characters.presentation.characters.detials

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.usecases.GetCharacterById
import ru.example.gnt.common.Resource
import ru.example.gnt.common.UiState
import javax.inject.Inject


internal class CharacterDetailsViewModel @Inject constructor(
    private val getCharacterById: GetCharacterById
) : ViewModel() {
    private val _state: MutableStateFlow<UiState<CharactersUiModel.Single>> =
        MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

    init {
        _state.value = UiState.Loading
    }


    fun setCharacterId(id: Int?) {
        viewModelScope.launch {
            getCharacterById(id!!).collectLatest { result ->
                result.onFailure {
                    _state.value =
                        UiState.Error(Resource.String(ru.example.gnt.ui.R.string.network_error))
                }
                result.onSuccess { character ->
                    _state.value = UiState.Success(character)
                }
            }

        }
    }
}
