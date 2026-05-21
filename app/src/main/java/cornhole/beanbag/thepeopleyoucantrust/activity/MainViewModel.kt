package cornhole.beanbag.thepeopleyoucantrust.activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel(networkConnection: NetworkConnection) : ViewModel() {

    val isOnline = networkConnection.isConnected.asLiveData()
    val appNeedsToBeUpdated = MutableLiveData<Boolean>()
    val dialogShowedOnce = MutableLiveData(false)
    var versionInstalledOnPhone: String? = null
    var thereWasAnError: Boolean? = null
    var companyList: List<CompanyInfo> = listOf()

    var companiesOnSale = MutableLiveData<List<CompanyInfo>>()

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

    fun getCompanies() : List<CompanyInfo>? {
        viewModelScope.launch {
            val retrofitApi = RetrofitLogic.retrofit
            val service: RetrofitAPI = retrofitApi.create(RetrofitAPI::class.java)
            val call: Call<CompanyList> =
                service.getCompanies("PHYSICAL")
            call.enqueue(object : Callback<CompanyList> {
                override fun onResponse(
                    call: Call<CompanyList>,
                    response: Response<CompanyList>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse: CompanyList? = response.body()

                        if (apiResponse != null) {
                            companyList = apiResponse.companies
                            companiesOnSale.value = companyList.filter { company ->
                                company.variantsObject.any { variant -> variant.pricingObject.isOnSale }
                            }
//                            isLoading.value = false
//                            updateBrowseCompaniesList()
                        } else {
                            companyList = arrayListOf()
                        }
                    }
                }

                override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                    Log.v("BM90", "Parsing error")
                }
            })
        }
        return companiesOnSale.value
    }
}