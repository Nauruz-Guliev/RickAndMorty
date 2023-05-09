package ru.example.gnt.episodes.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.example.gnt.common.di.qualifiers.IoDispatcher

@Module
class UtilityModule {

    @Provides
    @IoDispatcher
    fun provideIoCoroutineScheduler(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
