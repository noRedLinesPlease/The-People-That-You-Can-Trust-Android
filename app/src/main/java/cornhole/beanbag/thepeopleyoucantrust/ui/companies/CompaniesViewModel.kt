package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.util.Log
import org.jsoup.Jsoup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompaniesViewModel : ViewModel() {

    private val _companyList = MutableStateFlow<List<CompanyInfo>>(emptyList())
    val companyList: StateFlow<List<CompanyInfo>> = _companyList.asStateFlow()

    private val _categoryList = MutableStateFlow<List<String>>(emptyList())
    val categoryList: StateFlow<List<String>> = _categoryList.asStateFlow()

    private val _companiesOnSale = MutableStateFlow<List<CompanyInfo>>(emptyList())
    val companiesOnSale: StateFlow<List<CompanyInfo>> = _companiesOnSale.asStateFlow()

    var companiesOnSaleWithDesc: ArrayList<String> = arrayListOf()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            val service: RetrofitAPI = RetrofitLogic.retrofit.create(RetrofitAPI::class.java)
            service.getCompanies().enqueue(object : Callback<CompanyList> {
                override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                    val companies = response.body()?.companies ?: emptyList()
                    _companyList.value = companies
                    _categoryList.value = companies
                        .flatMap { it.companyListingCategoryList }
                        .distinct()
                        .sorted()
                    _companiesOnSale.value = companies.filter { company ->
                        company.variantsObject.any { it.pricingObject.isOnSale }
                    }
                    companiesOnSaleWithDesc = getSalesDescription() as ArrayList<String>
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                    Log.v("BM90", "Parsing error: ${t.message}")
                    _isLoading.value = false
                }
            })
        }
    }

    fun getCompaniesOnSale(): List<CompanyInfo> =
        _companyList.value.filter { company ->
            company.variantsObject.any { it.pricingObject.isOnSale }
        }

    fun getSalesDescription() : List<String> {
        companiesOnSaleWithDesc.clear()
        val companiesOnSale = getCompaniesOnSale()
        companiesOnSale.forEach { companyOnSale ->
            val saleDescription = companyOnSale.saleDescription
            val singleBannerText = "** ${companyOnSale.companyName} - ${Jsoup.parse(saleDescription).text()} ** "
            companiesOnSaleWithDesc.add(singleBannerText)
        }
        return companiesOnSaleWithDesc
    }
}
