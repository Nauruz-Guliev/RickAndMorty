package ru.example.gnt.rickandmorty.utility.impl

import android.util.Log
import ru.example.gnt.common.utils.AppLogger
import javax.inject.Inject

class AppLoggerImpl  @Inject constructor(): AppLogger {
    override fun log(message: String) {
        Log.d("RICK_AND_MORTY",  message)
    }
    override fun err(exception: Throwable, message: String?) {
        Log.e("RM_ERROR", message, exception)
    }
}