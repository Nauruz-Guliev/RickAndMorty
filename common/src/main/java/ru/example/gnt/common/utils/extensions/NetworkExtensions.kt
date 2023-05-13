package ru.example.gnt.common.utils.extensions

import android.util.Log
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.awaitResponse
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import java.io.IOException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Call<RequestType>,
    noinline saveFetchResult: (suspend (RequestType) -> Unit),
    crossinline shouldFetch: () -> Boolean = { true }
) = flow {
    val flow: Flow<Result<ResultType>> = if (shouldFetch()) {
        try {
            val remoteData = fetch().awaitResponse()
            if (remoteData.isSuccessful) {
                saveFetchResult(remoteData.body()!!)
                query().map {
                        Result.success(it)
                }
            } else {
                query().map {
                    Result.failure(
                        ApplicationException.BackendException(remoteData.code()),
                    )
                }
            }
        } catch (e: ApplicationException) {
            flowOf(Result.failure(e))
        } catch (ex: java.lang.NullPointerException) {
            flowOf(
                Result.failure(
                    ApplicationException.EmptyResultException(
                        resource = Resource.String(ru.example.gnt.ui.R.string.no_data_available_error)
                    )
                )
            )
        } catch (e: JsonDataException) {
            flowOf(
                Result.failure(
                    ApplicationException.ParseException(
                        e,
                        resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: JsonEncodingException) {
            flowOf(
                Result.failure(
                    ApplicationException.ParseException(
                        e,
                        resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: HttpException) {
            query().map {
                Result.failure(
                    ApplicationException.BackendException(
                        code = e.code(),
                        cause = e,
                    )
                )
            }
        } catch (e: IOException) {
            query().map {
                Result.success(it)
            }
        } catch (e: Exception) {
            flowOf(
                Result.failure(
                    ApplicationException.DataAccessException(
                        e,
                        Resource.String(ru.example.gnt.ui.R.string.data_access_error)
                    )
                )
            )
        }
    } else {
        query().map { Result.success(it) }
    }
    emitAll(flow)
}

suspend fun <T> wrapRetrofitErrorSuspending(block:suspend () -> T): T {
    return try {
        block()
    } catch (e: ApplicationException) {
        throw e
    } catch (e: JsonDataException) {
        throw ApplicationException.ParseException(
            e,
            resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
        )
    } catch (e: JsonEncodingException) {
        throw ApplicationException.ParseException(
            e,
            resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
        )
    } catch (e: HttpException) {
        throw ApplicationException.BackendException(
            code = e.code(),
            cause = e,
        )
    } catch (e: ApplicationException.ConnectionException) {
        throw e
    } catch (ex: java.net.UnknownHostException) {
        throw ApplicationException.ConnectionException(
            cause = ex,
            resource = Resource.String(ru.example.gnt.ui.R.string.connection_error)
        )

    } catch (e: IOException) {
        throw ApplicationException.ConnectionException(
            cause = e,
            resource = Resource.String(ru.example.gnt.ui.R.string.connection_error)
        )
    } catch (e: Exception) {
        Log.d("ERROR_MESSAGE_SUSPEND", e.message.toString() + " " + e.javaClass.name.toString())
        throw ApplicationException.DataAccessException(
            cause = e,
            resource = Resource.String(ru.example.gnt.ui.R.string.data_access_error)
        )
    }
}

fun <T> wrapRetrofitErrorRegular(block:() -> T): T {
    return try {
        block()
    } catch (e: ApplicationException) {
        throw e
    } catch (e: JsonDataException) {
        throw ApplicationException.ParseException(
            e,
            resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
        )
    } catch (e: JsonEncodingException) {
        throw ApplicationException.ParseException(
            e,
            resource = Resource.String(ru.example.gnt.ui.R.string.unable_to_parse_error)
        )
    } catch (e: HttpException) {
        throw ApplicationException.BackendException(
            code = e.code(),
            cause = e,
        )
    } catch (e: ApplicationException.ConnectionException) {
        throw e
    } catch (ex: java.net.UnknownHostException) {
        throw ApplicationException.ConnectionException(
            cause = ex,
            resource = Resource.String(ru.example.gnt.ui.R.string.connection_error)
        )

    } catch (e: IOException) {
        throw ApplicationException.ConnectionException(
            cause = e,
            resource = Resource.String(ru.example.gnt.ui.R.string.connection_error)
        )
    } catch (e: Exception) {
        Log.d("ERROR_MESSAGE", e.message.toString() + " " + e.javaClass.name.toString())
        throw ApplicationException.DataAccessException(
            cause = e,
            resource = Resource.String(ru.example.gnt.ui.R.string.data_access_error)
        )
    }
}


