package ru.example.gnt.common.utils.extensions

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.awaitResponse
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.AppException
import ru.example.gnt.common.exceptions.BackendException
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.exceptions.ParseException
import ru.example.gnt.common.model.Resource
import java.io.IOException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Call<RequestType>,
    noinline saveFetchResult: (suspend (RequestType) -> Unit),
    noinline transformResult: (suspend (ResultType, RequestType) -> ResultType)? = null,
    crossinline shouldFetch: () -> Boolean = { true }
) = flow {
    val flow: Flow<Result<ResultType>> = if (shouldFetch()) {
        val remoteData = fetch().awaitResponse()
        try {
            if (remoteData.isSuccessful) {
                saveFetchResult(remoteData.body()!!)
                query().map {
                    if (transformResult != null) {
                        Result.success(transformResult(it, remoteData.body()!!))
                    } else {
                        Result.success(it)
                    }
                }
            } else {
                query().map {
                    Result.failure(
                        BackendException(remoteData.code()),
                    )
                }
            }
        } catch (e: AppException) {
            flowOf(Result.failure(e))
        } catch (e: JsonDataException) {
            flowOf(
                Result.failure(
                    ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: JsonEncodingException) {
            flowOf(
                Result.failure(
                    ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: IOException) {
            query().map { Result.success(it) }
        } catch (e: Exception) {
            flowOf(
                Result.failure(
                    DataAccessException(
                        e,
                        Resource.String(R.string.data_access_error)
                    )
                )
            )
        }
    } else {
        query().map { Result.success(it) }
    }
    emitAll(flow)
}

fun <T> wrapRetrofitError(block: () -> Unit, onNoConnectionAction: () -> Flow<T>): Flow<Result<T>> =
    flow {
        try {
            block()
        } catch (e: AppException) {
            emit(Result.failure(e))
        } catch (e: JsonDataException) {
            emit(
                Result.failure(
                    ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: JsonEncodingException) {
            emit(
                Result.failure(
                    ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    )
                )
            )
        } catch (e: IOException) {
            onNoConnectionAction().map { Result.success(it) }
        } catch (e: Exception) {
            emit(
                Result.failure(
                    DataAccessException(
                        e,
                        Resource.String(R.string.data_access_error)
                    )
                )
            )
        }
    }

