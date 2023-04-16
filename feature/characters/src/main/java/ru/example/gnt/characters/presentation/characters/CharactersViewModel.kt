package ru.example.gnt.characters.presentation.characters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.data.CharacterRemoteMediator
import ru.example.gnt.characters.domain.usecases.GetAllCharactersUseCase
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class CharactersViewModel @Inject constructor(
    private val getAllCharactersUseCase: GetAllCharactersUseCase,
    //  private val getFilteredCharacters: GetFilteredCharacters,
    private val navigator: CharactersRouter,
) : ViewModel() {

    private val _uiState: MutableStateFlow<CharactersState> = MutableStateFlow(CharactersState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            charactersFlow = getAllCharactersUseCase(_uiState.value.filter).distinctUntilChanged()
                .cachedIn(viewModelScope)
        )
    }

    fun clearAllFilters() {
        _uiState.value.filter.apply {
            status = null
            gender = null
        }
    }

    fun navigateToDetails(id: Int) {
        navigator.openCharacterDetails(id)
    }

    fun setStatusFilter(status: CharacterStatusEnum) {
        _uiState.value.filter.status = status
    }
    fun setGenderFilter(gender: CharacterGenderEnum) {
        _uiState.value.filter.gender = gender
        Log.d("FILTER_HERE",_uiState.value.filter.gender.toString())
    }

    fun setNameFilter(name: String) {
        _uiState.value.filter.name = name
    }

    fun setSpeciesFilter(species: String){
        _uiState.value.filter.species = species
    }
    fun setTypeFilter(type: String) {
        _uiState.value.filter.type = type
    }
}
