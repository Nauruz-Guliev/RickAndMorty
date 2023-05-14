package ru.example.gnt.episodes.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.example.gnt.common.di.viewmodel.ViewModelFactory
import ru.example.gnt.common.di.viewmodel.ViewModelKey
import ru.example.gnt.episodes.presentation.list.EpisodeListViewModel

@Module
internal abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeListViewModel::class)
    abstract fun bindEpisodesViewModel(viewModel: EpisodeListViewModel): ViewModel

}
