package ru.example.gnt.data.di.remote.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.example.gnt.common.R
import ru.example.gnt.common.isNetworkOn
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Request.Builder = chain.request().newBuilder()
        return chain.proceed(response.build())
    }
}
