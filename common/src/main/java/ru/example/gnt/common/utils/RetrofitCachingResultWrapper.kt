package ru.example.gnt.common.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.example.gnt.common.R
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.exceptions.DatabaseException
import ru.example.gnt.common.exceptions.NetworkException
import ru.example.gnt.common.model.Resource
import kotlin.properties.Delegates

class RetrofitCachingResultWrapper<RemoteResponse, UiResult, Entity> private constructor(
    private val call: Call<RemoteResponse>,
    private val responseMapper: BaseMapper<RemoteResponse, UiResult>,
    private val entityMapper: BaseMapper<Entity, UiResult>? = null,
    private val isNetworkOn: Boolean,
    private inline val cacheSource: suspend () -> Result<Entity?>
) : Callback<RemoteResponse> {
    private var uiResult: MutableStateFlow<RetrofitResult<UiResult>> =
        MutableStateFlow(RetrofitResult.Empty)


    private var entityResult: MutableStateFlow<RetrofitResult<UiResult>> =
        MutableStateFlow(RetrofitResult.Empty)


    companion object {
        fun <RemoteResponse, UiResult, Entity> builder(): RetrofitCall<RemoteResponse, UiResult, Entity> {
            return Builder()
        }
    }

    interface RetrofitCall<T, B, F> {
        fun retrofitCall(call: Call<T>): RetrofitResponseMapper<T, B, F>
    }

    interface RetrofitResponseMapper<T, B, F> {
        fun retrofitResponseMapper(mapper: BaseMapper<T, B>): IsNetworkOn<T, B, F>
    }

    /*
    interface EntityMapper<T, B, F> {
        fun entityMapper(mapper: BaseMapper<F, B>): IsNetworkOn<T, B, F>
    }
    
     */

    interface IsNetworkOn<T, B, F> {
        fun isNetworkOn(isNetworkOn: Boolean): CacheSource<T, B, F>
    }

    interface CacheSource<T, B, F> {
        fun cacheSource(cacheSource: suspend () -> Result<F?>): Builder<T, B, F>
    }

    interface Build<T, B, F> {
        fun entityMapper(mapper: BaseMapper<F, B>): Build<T, B, F>
        suspend fun build(): RetrofitCachingResultWrapper<T, B, F>
    }

    class Builder<T, B, F> : Build<T, B, F>, RetrofitCall<T, B, F>,
        RetrofitResponseMapper<T, B, F>, IsNetworkOn<T, B, F>, CacheSource<T, B, F> {
        private lateinit var call: Call<T>
        private lateinit var responseMapper: BaseMapper<T, B>
        private var entityMapper: BaseMapper<F, B>? = null
        private var isNetworkOn by Delegates.notNull<Boolean>()
        private lateinit var cacheSource: suspend () -> Result<F?>
        override fun retrofitCall(call: Call<T>): RetrofitResponseMapper<T, B, F> = apply {
            this.call = call
        }


        override fun isNetworkOn(isNetworkOn: Boolean): CacheSource<T, B, F> = apply {
            this.isNetworkOn = isNetworkOn
        }

        override fun cacheSource(cacheSource: suspend () -> Result<F?>): Builder<T, B, F> = apply {
            this.cacheSource = cacheSource
        }


        override suspend fun build(): RetrofitCachingResultWrapper<T, B, F> {
            return RetrofitCachingResultWrapper(
                call = call,
                responseMapper = responseMapper,
                entityMapper = entityMapper,
                isNetworkOn = isNetworkOn,
                cacheSource = cacheSource
            )
        }

        override fun retrofitResponseMapper(mapper: BaseMapper<T, B>): IsNetworkOn<T, B, F> =
            apply {
                this.responseMapper = mapper
            }

        override fun entityMapper(mapper: BaseMapper<F, B>): Build<T, B, F> = apply {
            this.entityMapper = mapper
        }
    }

    suspend fun getUiResult(): Flow<RetrofitResult<UiResult>> {
        if (isNetworkOn) {
            call.enqueue(this)
        } else {
            cacheSource().onFailure {
                uiResult.emit(RetrofitResult.Error(it))
            }.onSuccess {
                if (it != null) {
                    uiResult.emit(RetrofitResult.Success(this.entityMapper!!.mapTo(it)))
                } else {
                    uiResult.emit(
                        RetrofitResult.Error(
                            DatabaseException(
                                resource = Resource.String(
                                    R.string.empty_database_result
                                )
                            )
                        )
                    )
                }
            }
        }
        return uiResult
    }

    suspend fun getEntityResult(): Flow<RetrofitResult<UiResult>?> {
        if (isNetworkOn) {
            call.enqueue(this)
        } else {
            cacheSource().onFailure {
                entityResult.emit(RetrofitResult.Error(it))
            }.onSuccess {
                if (it != null && entityMapper != null) {
                    entityResult.emit(RetrofitResult.Success(entityMapper.mapTo(it)))
                } else {
                    uiResult.emit(
                        RetrofitResult.Error(
                            DatabaseException(
                                resource = Resource.String(
                                    R.string.empty_database_result
                                )
                            )
                        )
                    )
                }
            }
        }
        return entityResult
    }


    override fun onResponse(call: Call<RemoteResponse>, response: Response<RemoteResponse>) {
        if (response.code() == 200) {
            val item = response.body()
            uiResult.value = if (item != null) {
                RetrofitResult.Success(responseMapper.mapTo(item))
            } else {
                RetrofitResult.Error(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.network_empty_result_error)))
            }
            entityResult.value = if (item != null) {
                if (entityMapper != null) {
                    RetrofitResult.Success(responseMapper.mapTo(item))
                } else {
                    RetrofitResult.Error(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.network_empty_result_error)))
                }
            } else {
                RetrofitResult.Error(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.network_empty_result_error)))
            }
        } else if (!response.isSuccessful) {
            uiResult.value = RetrofitResult.Error(
                NetworkException(
                    resource = Resource.String(
                        ru.example.gnt.ui.R.string.network_error_with_code,
                        argument = response.code()
                    )
                )
            )
        }
    }

    override fun onFailure(call: Call<RemoteResponse>, t: Throwable) {
        //todo improve error handling
        uiResult =
            MutableStateFlow(RetrofitResult.Error(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.unknown_network_exception))))
    }

}
