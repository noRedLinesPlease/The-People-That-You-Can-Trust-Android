package cornhole.beanbag.thepeopleyoucantrust.activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainViewModel(networkConnection: NetworkConnection) : ViewModel() {

    val isOnline = networkConnection.isConnected.asLiveData()
    val appNeedsToBeUpdated = MutableLiveData<Boolean>()
    val dialogShowedOnce = MutableLiveData(false)

    fun checkForAppUpdate(context: Context) {
        var latestVersion = ""
        Thread {
            try {
                val url = "https://alligator-beige-t5e3.squarespace.com/"
                val doc: Document = Jsoup.connect(url).get()

                latestVersion =
                    doc.getElementsByClass("fe-block-yui_3_17_2_1_1715521103669_7199").text()
            } catch (e: Exception) {
                Log.v("BM90", e.message.toString())
            }

            val manager: PackageManager = context.packageManager
            val info: PackageInfo = manager.getPackageInfo(
                context.packageName, 0
            )
            val version = info.versionName

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    appNeedsToBeUpdated.value = version != latestVersion
                },
                100
            )
        }.start()
    }
}