package ru.example.gnt.characters.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.domain.usecases.GetCharacterListUseCase
import ru.example.gnt.characters.presentation.list.model.CharactersState
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import javax.inject.Inject


internal class CharacterListViewModel @Inject constructor(
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val navigator: CharactersRouter,
) : ViewModel() {

    private val _uiState: MutableStateFlow<CharactersState> = MutableStateFlow(CharactersState())
    val uiState = _uiState.asStateFlow()

    init {
        applyFilter()
    }

    fun clearAllFilters() {
        _uiState.value.filter.apply {
            type = null
            gender = null
            name = null
            status = null
            species = null
        }
        applyFilter()
    }

    fun navigateToDetails(id: Int) {
        navigator.navigateToCharacterDetails(id)
    }

    fun applyFilter(
        status: CharacterStatusEnum? = _uiState.value.filter.status,
        gender: CharacterGenderEnum? = _uiState.value.filter.gender,
        name: String? = _uiState.value.filter.name,
        type: String? = _uiState.value.filter.type,
        species: String? = _uiState.value.filter.species
    ) {
        _uiState.value.filter.apply {
            this.type = type
            this.status = status
            this.species = species
            this.gender = gender
            this.name = name
        }
        viewModelScope.launch {
            loadCharacters()
        }
    }

    fun isFilterOn(): Boolean {
        with(_uiState.value.filter) {
            return type == null && status == null && species == null && gender == null && name == null
        }
    }

    private suspend fun loadCharacters() {
        _uiState.value = _uiState.value.copy(
            charactersFlow = getCharacterListUseCase(_uiState.value.filter).distinctUntilChanged()
                .cachedIn(viewModelScope)
        )
    }

}
