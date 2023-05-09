package ru.example.gnt.common.utils.extensions

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.awaitResponse
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.*
import ru.example.gnt.common.model.Resource
import java.io.IOException
import java.net.UnknownHostException

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
        } catch (e: HttpException) {
            query().map {
                Result.failure(
                    NetworkException(
                        code = e.code(),
                        cause = e,
                    )
                )
            }
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

fun <T> wrapRetrofitError(block: () -> T): T {
    return try {
        block()
    } catch (e: AppException) {
        throw e
    } catch (e: JsonDataException) {
        throw ParseException(
            e,
            resource = Resource.String(R.string.unable_to_parse_error)
        )
    } catch (e: JsonEncodingException) {
        throw ParseException(
            e,
            resource = Resource.String(R.string.unable_to_parse_error)
        )
    } catch (e: HttpException) {
        throw NetworkException(
            code = e.code(),
            cause = e,
        )
    } catch (e: ConnectionException) {
        throw e
    } catch (ex: java.net.UnknownHostException) {
        throw ConnectionException(
            cause = ex,
            resource = Resource.String(R.string.connection_error)
        )

    } catch (e: IOException) {
        throw ConnectionException(
            cause = e,
            resource = Resource.String(R.string.connection_error)
        )
    } catch (e: Exception) {
        throw DataAccessException(
            cause = e,
            resource = Resource.String(R.string.data_access_error)
        )
    }
}

