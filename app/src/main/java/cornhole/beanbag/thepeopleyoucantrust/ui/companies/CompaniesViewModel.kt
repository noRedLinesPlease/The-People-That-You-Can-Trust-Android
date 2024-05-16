package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cornhole.beanbag.thepeopleyoucantrust.api.BusinessCategories
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompaniesViewModel : ViewModel() {

    var companyList = MutableLiveData<ArrayList<CompanyInfo>>()
    private var businessCategoryListFromApi = ArrayList<String>()
    var businessCategoryList = ArrayList<BusinessCategories>()
    var isLoading = MutableLiveData(true)
    var navigatedFromFragment = false

    init {
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
                    if (!navigatedFromFragment) {
                        Thread.sleep(1000)
                    }

                    if (response.isSuccessful) {
                        val apiResponse: CompanyList? = response.body()

                        if (apiResponse != null) {
                            companyList.value = apiResponse.companies
                            isLoading.value = false
                            updateBrowseCompaniesList()
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

    fun updateBrowseCompaniesList() {
        companyList.value?.forEach { company ->
            company.companyListingCategoryList.forEach { businessCategory ->
                if (!businessCategoryListFromApi.contains(businessCategory)) {
                    businessCategoryListFromApi.add(businessCategory)
                }
            }
        }
        businessCategoryListFromApi.forEach {
            businessCategoryList.add(
                BusinessCategories(
                    businessCategory = it,
                    isSelected = false
                )
            )
        }
        businessCategoryList.sortBy { it.businessCategory }
    }
}
