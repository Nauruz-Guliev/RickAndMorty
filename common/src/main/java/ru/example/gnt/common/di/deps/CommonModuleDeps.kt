package ru.example.gnt.common.di.deps

import android.content.Context
import ru.example.gnt.common.utils.AppLogger
import ru.example.gnt.common.utils.CommonUi
import ru.example.gnt.common.utils.ErrorHandler


interface CommonModuleDeps {
   val errorHandler : ErrorHandler
   val commonUi: CommonUi
   val logger: AppLogger
}

