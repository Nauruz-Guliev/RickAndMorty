package ru.example.gnt.characters.presentation.characters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.usecases.GetAllCharactersUseCase
import ru.example.gnt.characters.domain.usecases.GetFilteredCharacters
import ru.example.gnt.common.Resource
import ru.example.gnt.common.UiState
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import javax.inject.Inject

internal class CharactersViewModel @Inject constructor(
    private val getAllCharactersUseCase: GetAllCharactersUseCase,
    private val getFilteredCharacters: GetFilteredCharacters,
    private val navigator: CharactersRouter
) : ViewModel() {

    private val _state: MutableStateFlow<UiState<CharactersUiModel>> =
        MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

    private val _filterState: MutableStateFlow<CharactersFilterModel> = MutableStateFlow(
        CharactersFilterModel()
    )
    val filterState = _filterState.asStateFlow()


    init {
        loadAllCharacters()
        viewModelScope.launch {
            _filterState.collectLatest {
                getFilteredCharacters(
                    gender = it.gender?.n,
                    status = it.status?.get
                ).distinctUntilChanged().collectLatest { result ->
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
    }

    fun loadAllCharacters() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            getAllCharactersUseCase().distinctUntilChanged().collectLatest { result ->
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

    fun setStatusFilter(status: CharacterStatusEnum?) {
        _filterState.value = _filterState.value.copy(
            status = status
        )
    }

    fun setGenderFilter(gender: CharacterGenderEnum?) {
        _filterState.value = _filterState.value.copy(
            gender = gender
        )
    }

}
