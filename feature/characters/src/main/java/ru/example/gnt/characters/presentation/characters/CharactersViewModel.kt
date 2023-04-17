package ru.example.gnt.characters.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.example.gnt.characters.CharactersRouter
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
       loadCharacters()
    }

    fun clearAllFilters() {
        _uiState.value.filter.apply {
            type = null
            gender = null
            name = null
            status = null
            species = null
        }
    }

    fun navigateToDetails(id: Int) {
        navigator.openCharacterDetails(id)
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
        loadCharacters()
    }

    private fun loadCharacters() {
        _uiState.value = _uiState.value.copy(
            charactersFlow = getAllCharactersUseCase(_uiState.value.filter).distinctUntilChanged()
                .cachedIn(viewModelScope)
        )
    }
}
