package cornhole.beanbag.thepeopleyoucantrust.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NetworkConnection(context: Context) {
        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        private val initiallyConnected = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        private val _isConnected = MutableStateFlow(initiallyConnected)
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

        init {
            // Observe network connectivity changes
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    _isConnected.value = true
                }

                override fun onLost(network: android.net.Network) {
                    _isConnected.value = false
                }
            })
        }

}
