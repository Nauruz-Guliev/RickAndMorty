package ru.example.gnt.data.di.remote.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.ConnectionException
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.model.Resource
import java.net.ConnectException
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Request.Builder = chain.request().newBuilder()
        if(!context.isNetworkOn()) {
            throw ConnectionException(resource = Resource.String(R.string.no_internet_connection_error))
        }
        return chain.proceed(response.build())
    }
}
