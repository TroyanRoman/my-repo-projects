package com.skillbox.ascent.di.networking

sealed class NetworkStatus {
    data class ConnectSuccess( val successStatus : String) : NetworkStatus()
    data class ConnectError(val error : Int) : NetworkStatus()
}
