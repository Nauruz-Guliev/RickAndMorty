package ru.example.gnt.characters.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.domain.usecases.GetAllCharactersUseCase
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

internal class CharactersViewModel @Inject constructor(
    private val getAllCharactersUseCase: GetAllCharactersUseCase,
    //  private val getFilteredCharacters: GetFilteredCharacters,
    private val navigator: CharactersRouter
) : ViewModel() {
    /*

    private val _state: MutableStateFlow<UiState<CharactersUiModel>> =
        MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

     */

    private val _filterState: MutableStateFlow<CharactersFilterModel> = MutableStateFlow(
        CharactersFilterModel()
    )
    val filterState = _filterState.asStateFlow()


    var state: Flow<PagingData<CharactersUiModel.Single>> =
        getAllCharactersUseCase().distinctUntilChanged().cachedIn(viewModelScope)

    /*
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

     */


    /*

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

     */

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
