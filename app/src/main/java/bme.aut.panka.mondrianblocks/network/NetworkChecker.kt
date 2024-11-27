package bme.aut.panka.mondrianblocks.network

import android.content.Context
import android.net.NetworkCapabilities

interface INetworkCapabilities {
    fun hasTransport(transportType: Int): Boolean
}
class NetworkCapabilitiesWrapper(private val networkCapabilities: NetworkCapabilities) : INetworkCapabilities {
    override fun hasTransport(transportType: Int): Boolean {
        return networkCapabilities.hasTransport(transportType)
    }
}

class NetworkChecker(private val context: Context, private val networkCapabilitiesFactory: (Context) -> INetworkCapabilities?) {

    fun checkForInternet(): Boolean {
        val networkCapabilities = networkCapabilitiesFactory(context) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}