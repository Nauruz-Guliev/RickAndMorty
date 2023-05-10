package ru.example.gnt.rickandmorty.utility.impl


import android.content.Context
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.utils.AppLogger
import ru.example.gnt.common.utils.CommonUi
import ru.example.gnt.common.utils.ErrorHandler
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor(
    private val appLoggerImpl: AppLogger,
    private val commonUiImpl: CommonUi,
    private val context: Context,
) : ErrorHandler {

    override fun handleError(exception: Throwable) {
        appLoggerImpl.err(exception)
        when (exception) {
            is ApplicationException.BackendException -> handleBackendException(exception)
            is ApplicationException.ConnectionException -> handleConnectionException(exception)
            is ApplicationException.ParseException -> handleParseException(exception)
            is ApplicationException.EmptyResultException -> handleEmptyResultException(exception)
            is ApplicationException.DataAccessException -> return
            else -> handleUnknownException(exception)
        }
    }

    private fun handleBackendException(ex: ApplicationException.BackendException) {

    }
    private fun handleConnectionException(ex: ApplicationException.ConnectionException) {

    }

    private fun handleParseException(ex: ApplicationException.ParseException) {

    }

    private fun handleEmptyResultException(ex: ApplicationException.EmptyResultException) {
        commonUiImpl.showToast(ex.resource?.getValue(context))
    }

    private fun handleUnknownException(ex: Throwable) {
        if(ex.message != null) {
            commonUiImpl.showToast(ex.message.toString())
        }
    }
}