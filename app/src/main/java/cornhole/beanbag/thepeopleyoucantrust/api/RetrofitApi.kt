package cornhole.beanbag.thepeopleyoucantrust.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("products")
    @Headers(
        "Authorization: Bearer dca6ca82-937d-4796-9ed2-0e1d16c4be60"
    )
    fun getCompanies(@Query("type") queryType: String): Call<CompanyList>
}