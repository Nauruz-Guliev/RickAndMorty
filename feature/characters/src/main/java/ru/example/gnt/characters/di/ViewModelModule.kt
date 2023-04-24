package ru.example.gnt.characters.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.example.gnt.characters.presentation.list.CharactersViewModel
import ru.example.gnt.common.di.scope.ApplicationScope
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.common.di.viewmodel.ViewModelFactory
import ru.example.gnt.common.di.viewmodel.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    @ScreenScope
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CharactersViewModel::class)
    internal abstract fun bindCharactersViewModel(viewModel: CharactersViewModel): ViewModel

}
