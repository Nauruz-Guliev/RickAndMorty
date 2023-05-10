package ru.example.gnt.rickandmorty.di.app

import android.content.Context
import dagger.Binds
import dagger.Module
import ru.example.gnt.common.utils.AppLogger
import ru.example.gnt.common.utils.CommonUi
import ru.example.gnt.common.utils.ErrorHandler
import ru.example.gnt.rickandmorty.App
import ru.example.gnt.rickandmorty.utility.impl.AppLoggerImpl
import ru.example.gnt.rickandmorty.utility.impl.CommonUiImpl
import ru.example.gnt.rickandmorty.utility.impl.ErrorHandlerImpl

@Module
abstract class UtilityModule {

    @Binds
    abstract fun bindAppLogger(logger: AppLoggerImpl): AppLogger

    @Binds
    abstract fun bindCommonUi(common: CommonUiImpl): CommonUi

    @Binds
    abstract fun bindErrorHandler(handler: ErrorHandlerImpl): ErrorHandler
}