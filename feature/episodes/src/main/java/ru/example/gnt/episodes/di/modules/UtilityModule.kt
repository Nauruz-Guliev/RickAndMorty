package ru.example.gnt.episodes.di.modules

import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.example.gnt.common.di.qualifiers.IoDispatcher
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier

@Module
class UtilityModule {

    @Provides
    @IoDispatcher
    fun provideIoCoroutineScheduler(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @RxIOSchedulerQualifier
    fun provideRxIoScheduler() : Scheduler {
        return Schedulers.io()
    }
}
