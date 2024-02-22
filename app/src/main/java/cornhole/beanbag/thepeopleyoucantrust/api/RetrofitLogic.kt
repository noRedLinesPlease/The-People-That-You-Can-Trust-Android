package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitLogic() {
    companion object {
        var gson = GsonBuilder().setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

        val BASE_URL = "https://api.squarespace.com/1.1/commerce/"

        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}