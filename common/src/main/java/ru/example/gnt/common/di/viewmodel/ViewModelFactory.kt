package ru.example.gnt.common.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.example.gnt.common.di.scope.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>,
        @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        viewModels[modelClass]?.get() as T
}
