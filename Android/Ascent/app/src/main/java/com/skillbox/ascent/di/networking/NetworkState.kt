package com.skillbox.ascent.di.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.skillbox.ascent.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkState @Inject constructor(
    private   val isBanned : Boolean,
    private val connectivityManager: ConnectivityManager
) {

    fun changes(connectivityLiveData: MutableLiveData<NetworkStatus>)   {

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityLiveData.postValue(onConnected())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                connectivityLiveData.postValue(onDisconnected())
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun onConnected(): NetworkStatus {
        return NetworkStatus.ConnectSuccess("Connected!")
    }

    private fun onDisconnected(): NetworkStatus {
        val netCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return NetworkStatus.ConnectError(
                    error = R.string.no_internet
                )

        val errorResult: NetworkStatus = when {
            isBanned &&
                    !netCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            ->
                NetworkStatus.ConnectError(
                    error = R.string.no_vpn_status
                )
            else -> NetworkStatus.ConnectError(
                error = R.string.no_internet
            )
        }
        return errorResult
    }
}