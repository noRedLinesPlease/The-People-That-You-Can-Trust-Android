package cornhole.beanbag.thepeopleyoucantrust.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection


class MainViewModel(networkConnection: NetworkConnection) : ViewModel() {

    val isOnline = networkConnection.isConnected.asLiveData()

}