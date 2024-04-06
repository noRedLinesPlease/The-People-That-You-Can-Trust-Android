package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompaniesViewModel : ViewModel() {

    var companyList = MutableLiveData<ArrayList<CompanyInfo>?>()
    var isLoading = MutableLiveData<Boolean>(true)
    init {
        viewModelScope.launch {

            val retrofitApi = RetrofitLogic.retrofit
            val service: RetrofitAPI = retrofitApi.create(RetrofitAPI::class.java)
            val call: Call<CompanyList> =
                service.getCompanies("PHYSICAL")

            call.enqueue(object : Callback<CompanyList> {
                override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                    if (response.isSuccessful) {
                        val apiResponse: CompanyList? = response.body()

                        if (apiResponse != null) {
                            companyList.value = apiResponse.companies
                            isLoading.value = false
                        } else {
                            companyList.value = arrayListOf()
                        }
                    }
                }

                override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                    Log.v("BM90", "Parsing error")
                }
            })
        }
    }
}