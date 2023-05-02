package ru.example.gnt.episodes.presentation.episode_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.example.gnt.common.model.UiState
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.usecases.GetEpisodeItemByIdUseCase

class EpisodeDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Int?,
    private val getEpisodeItemByIdUseCase: GetEpisodeItemByIdUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<UiState<EpisodeDetailsItem>> =
        MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

    init {
        loadEpisode()
    }

    fun loadEpisode() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            if (id != null) {
                getEpisodeItemByIdUseCase(id)
                    .onFailure { _state.value = UiState.Error(it) }
                    .onSuccess { _state.value = UiState.Success(it) }
            } else {
                UiState.Empty
            }
        }
    }

    @AssistedFactory
    interface EpisodeDetailsViewModelFactory {
        fun create(episodeId: Int?): EpisodeDetailsViewModel
    }
}
