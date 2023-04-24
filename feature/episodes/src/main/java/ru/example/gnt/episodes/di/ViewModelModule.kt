package ru.example.gnt.episodes.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.example.gnt.common.di.viewmodel.ViewModelFactory
import ru.example.gnt.common.di.viewmodel.ViewModelKey
import ru.example.gnt.episodes.presentation.episodes.EpisodesViewModel

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(EpisodesViewModel::class)
    abstract fun bindEpisodesViewModel(viewModel: EpisodesViewModel): ViewModel

}
