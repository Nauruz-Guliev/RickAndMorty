package ru.example.gnt.common.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.exceptions.NetworkException
import ru.example.gnt.common.model.Resource

class RetrofitResultWrapper<T, B>(
    private val call: Call<B>,
    private val mapper: BaseMapper<T, B>,
    private inline val apiCallback: (Result<B?>) -> Unit
) : Callback<T> {
    var result =
        Result.failure<B?>(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.unknown_network_exception)))

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.code() == 200) {
            val item = response.body()
            result = if (item != null) {
                Result.success(mapper.mapTo(item))
            } else {
                Result.failure(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.network_empty_result_error)))
            }
        } else if (!response.isSuccessful) {
            result = Result.failure(
                NetworkException(
                    resource = Resource.String(
                        ru.example.gnt.ui.R.string.network_error_with_code,
                        argument = response.code()
                    )
                )
            )
        }
        apiCallback.invoke(result)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        //todo improve error handling
        result =
            Result.failure(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.unknown_network_exception)))
        apiCallback.invoke(result)
    }

}
