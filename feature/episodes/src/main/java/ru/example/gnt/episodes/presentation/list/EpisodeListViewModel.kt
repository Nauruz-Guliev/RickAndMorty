package ru.example.gnt.episodes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.domain.usecases.GetEpisodeFilteredListUseCase
import ru.example.gnt.episodes.presentation.list.model.EpisodesState
import javax.inject.Inject

internal class EpisodeListViewModel @Inject constructor(
    private val getEpisodeFilteredListUseCase: GetEpisodeFilteredListUseCase,
    private val router: EpisodesRouter,
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
        loadEpisodes()
    }

    private fun loadEpisodes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                episodesFlow = getEpisodeFilteredListUseCase(_state.value.filter).distinctUntilChanged()
                    .cachedIn(viewModelScope)
            )
        }
    }

    fun navigateToEpisodeDetails(id: Int?) {
        router.navigateToEpisodeDetails(id)
    }

    fun isFilterOff(): Boolean =
        with(_state.value.filter) {
            name == null && episode == null
        }

}
