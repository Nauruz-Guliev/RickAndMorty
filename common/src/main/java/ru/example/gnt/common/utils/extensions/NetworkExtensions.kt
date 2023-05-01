package ru.example.gnt.common.utils.extensions

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.awaitResponse
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.*
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.utils.DataResource
import java.io.IOException

inline fun <ResultType, RequestType> networkResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Call<RequestType>,
    noinline saveFetchResult: (suspend (RequestType) -> Unit),
    crossinline shouldFetch: () -> Boolean = { true }
) = flow {
    val flow: Flow<DataResource<ResultType>> = if (shouldFetch()) {

        val remoteData = fetch().awaitResponse()
        try {
            if (remoteData.isSuccessful) {
                saveFetchResult(remoteData.body()!!)

                query().map { DataResource.Success(it) }
            } else {
                query().map {
                    DataResource.Error(
                        message = BackendException(remoteData.code()),
                        it
                    )
                }
            }
        } catch (e: AppException) {
            query().map { DataResource.Error(e, it) }
        } catch (e: JsonDataException) {
            query().map {
                DataResource.Error(
                    message = ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    ),
                    it
                )
            }
        } catch (e: JsonEncodingException) {
            query().map {
                DataResource.Error(
                    message = ParseException(
                        e,
                        resource = Resource.String(R.string.unable_to_parse_error)
                    ),
                    it
                )
            }
        } catch (e: IOException) {
            query().map {
                DataResource.Error(
                    message = ConnectionException(e, Resource.String(R.string.connection_error)),
                    it
                )
            }
        } catch (e: Exception) {
            query().map {
                DataResource.Error(
                    message = DataAccessException(e, Resource.String(R.string.data_access_error)),
                    it
                )
            }
        }
    } else {
        query().map { DataResource.Success(it) }
    }
    emitAll(flow)
}

