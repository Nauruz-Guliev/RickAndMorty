package ru.example.gnt.common.utils

interface AppLogger {
    fun log(message: String)
    fun err(exception: Throwable, message: String? = null)
}
