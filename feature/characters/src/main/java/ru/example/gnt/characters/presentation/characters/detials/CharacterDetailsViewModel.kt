package ru.example.gnt.characters.presentation.characters.detials

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import ru.example.gnt.characters.domain.usecases.GetCharacterById
import ru.example.gnt.common.model.UiState
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

 /*
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

  */
}
