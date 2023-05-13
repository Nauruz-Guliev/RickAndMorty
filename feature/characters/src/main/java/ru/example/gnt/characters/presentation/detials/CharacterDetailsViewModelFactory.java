package ru.example.gnt.characters.presentation.detials;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface CharacterDetailsViewModelFactory {
    CharacterDetailsViewModel create(int id);
}
