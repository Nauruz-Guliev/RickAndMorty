package ru.example.gnt.locations.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.example.gnt.locations.LocationsRouter
import ru.example.gnt.locations.domain.usecases.GetLocationFilteredListUseCase
import javax.inject.Inject

class LocationListViewModel @Inject constructor(
    private val getLocationFilteredListUseCase: GetLocationFilteredListUseCase,
    private val router: LocationsRouter
) : ViewModel() {

    private val _state: MutableStateFlow<LocationListState> = MutableStateFlow(LocationListState())
    val state = _state.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                locationsFlow = getLocationFilteredListUseCase(_state.value.filter)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
            )
        }
    }

    fun applyFilter(
        name: String? = _state.value.filter.name,
        dimension: String? = _state.value.filter.dimension,
        type: String? = _state.value.filter.type,
    ) {
        _state.value.filter.apply {
            this.name = name
            this.dimension = dimension
            this.type = type
        }
        loadLocations()
    }

    fun navigateToDetailsFragment(id: Int?) {
        router.navigateToLocationDetails(id)
    }

    fun clearAllFilters() {
        _state.value.filter.apply {
            this.dimension = null
            this.type = null
            this.name = null
        }
    }
}
