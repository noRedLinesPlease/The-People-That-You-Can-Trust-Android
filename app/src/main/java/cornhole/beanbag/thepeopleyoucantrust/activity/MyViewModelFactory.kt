package cornhole.beanbag.thepeopleyoucantrust.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection

class MyViewModelFactory constructor(private val networkConnection: NetworkConnection): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(this.networkConnection) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}