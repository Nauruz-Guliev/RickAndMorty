package ru.example.gnt.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.sample
import ru.example.gnt.common.R


import ru.example.gnt.common.di.deps.CommonModuleDepsProvider
import ru.example.gnt.common.utils.AppLogger
import ru.example.gnt.common.utils.CommonUi

@OptIn(FlowPreview::class)
open class BaseViewModel : ViewModel() {

    private val deps = CommonModuleDepsProvider.deps

    protected val commonUi: CommonUi get() = deps.commonUi
    protected val logger: AppLogger get() = deps.logger

    /**
     * Скоуп вьюмодели, в котором автоматически будут обрабатываться исключения
     * согласно логике ErrorHandler, реализация которого лежит в App модуле.
     */
    protected val viewModelScope: CoroutineScope by lazy {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            deps.errorHandler.handleError(exception)
        }
        CoroutineScope(SupervisorJob() + Dispatchers.Main + errorHandler)
    }


    private val debounceFlow = MutableSharedFlow<() -> Unit>(
        replay = 1,
        extraBufferCapacity = 42
    )

    init {
        viewModelScope.launch {
            debounceFlow.sample(400).collect {
                it()
            }
        }
    }

    protected fun debounce(block: () -> Unit) {
        debounceFlow.tryEmit(block)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
