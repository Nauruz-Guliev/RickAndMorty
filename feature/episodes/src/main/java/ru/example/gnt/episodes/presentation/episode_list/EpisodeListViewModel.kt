package ru.example.gnt.episodes.presentation.episode_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.domain.usecases.GetEpisodeFilteredListUseCase
import ru.example.gnt.episodes.domain.usecases.GetEpisodeItemByIdUseCase
import ru.example.gnt.episodes.presentation.episode_list.model.EpisodesState
import javax.inject.Inject

internal class EpisodeListViewModel @Inject constructor(
    private val getEpisodeItemByIdUseCase: GetEpisodeItemByIdUseCase,
    private val getEpisodeFilteredListUseCase: GetEpisodeFilteredListUseCase,
    private val router: EpisodesRouter
) : ViewModel() {

    private val _state: MutableStateFlow<EpisodesState> = MutableStateFlow(EpisodesState())
    val state = _state.asStateFlow()

    init {
        applyFilter()
    }

    fun clearAllFilters() {
        _state.value.filter.apply {
            this.episode = null
            this.name = null
        }
    }

    fun applyFilter(
        name: String? = _state.value.filter.name,
        episode: String? = _state.value.filter.episode,
    ) {
        _state.value.filter.apply {
            this.name = name
            this.episode = episode
        }
        viewModelScope.launch {
            loadEpisodes()
        }
    }

    private suspend fun loadEpisodes() {
        _state.value = _state.value.copy(
            episodesFlow = getEpisodeFilteredListUseCase(_state.value.filter).distinctUntilChanged()
                .cachedIn(viewModelScope)
        )
    }
}
