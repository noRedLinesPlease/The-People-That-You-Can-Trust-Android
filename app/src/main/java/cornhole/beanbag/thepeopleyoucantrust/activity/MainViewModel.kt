package cornhole.beanbag.thepeopleyoucantrust.activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainViewModel(networkConnection: NetworkConnection) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkConnection.isConnected
    val appNeedsToBeUpdated = MutableLiveData<Boolean>()
    var versionInstalledOnPhone: String? = null
    var thereWasAnError: Boolean? = null

    fun checkForAppUpdate(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var latestVersion = ""

            try {
                val url = "https://alligator-beige-t5e3.squarespace.com/"
                val doc: Document = Jsoup.connect(url).get()

                latestVersion =
                    doc.getElementsByClass("fe-block-yui_3_17_2_1_1715521103669_7199").text()
            } catch (e: Exception) {
                Log.v("BM90", e.message.toString())
                thereWasAnError = true
            }

            try {
                val manager: PackageManager = context.packageManager
                val info: PackageInfo = manager.getPackageInfo(
                    context.packageName, 0
                )
                versionInstalledOnPhone = info.versionName
            } catch (e: Exception) {
                thereWasAnError = true
                Log.v("BM90", e.message.toString())
            }

            withContext(Dispatchers.Main) {
                if (thereWasAnError != null || latestVersion.isEmpty()) {
                    appNeedsToBeUpdated.value = false
                } else {
                    appNeedsToBeUpdated.value = versionInstalledOnPhone != latestVersion
                    Log.v("BM90", "version on phone:  $versionInstalledOnPhone")
                    Log.v("BM90", "latest version: $latestVersion")
                }
            }
        }
    }
}