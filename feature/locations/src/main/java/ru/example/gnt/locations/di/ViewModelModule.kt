package ru.example.gnt.locations.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import ru.example.gnt.common.di.viewmodel.ViewModelFactory
import ru.example.gnt.locations.presentation.list.LocationListViewModel

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindLocationListViewModel(viewModel: LocationListViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelFactory

}
