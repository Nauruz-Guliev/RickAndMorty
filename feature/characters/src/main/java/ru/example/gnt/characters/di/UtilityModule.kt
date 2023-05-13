package ru.example.gnt.characters.di

import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier

@Module
class UtilityModule {

    @Provides
    @RxIOSchedulerQualifier
    fun provideScheduler() : Scheduler {
        return Schedulers.io()
    }
}
