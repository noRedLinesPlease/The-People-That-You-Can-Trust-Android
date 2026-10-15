package cornhole.beanbag.thepeopleyoucantrust.api

import cornhole.beanbag.thepeopleyoucantrust.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("products")
    fun _getCompanies(
        @Header("Authorization") authorization: String,
        @Query("type") queryType: String
    ): Call<CompanyList>

    fun getCompanies(
        authorization: String = "Bearer ${BuildConfig.AUTH_TOKEN}",
        queryType: String = "PHYSICAL"
        ) : Call<CompanyList> {
            return _getCompanies(authorization, queryType)
    }
}
