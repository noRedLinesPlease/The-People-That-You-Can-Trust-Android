package cornhole.beanbag.thepeopleyoucantrust.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("products")
    fun getCompanies(@Query("type") queryType: String): Call<CompanyList>
}
