package ru.example.gnt.data.di.remote.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import okhttp3.Interceptor
import okhttp3.Response
import ru.example.gnt.common.exceptions.ApplicationException
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    context: Context
) : Interceptor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var isConnected: Boolean = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isConnected = false
        }

        override fun onUnavailable() {
            super.onUnavailable()
            isConnected = false
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnected) {
            throw ApplicationException.ConnectionException()
        } else {
            chain.proceed(chain.request())
        }
    }
}
