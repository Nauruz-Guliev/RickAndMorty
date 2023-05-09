package ru.example.gnt.locations.presentation.details

import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocationDetailsViewModelFactory {
    fun create(id: Int): LocationDetailsViewModel?
}
