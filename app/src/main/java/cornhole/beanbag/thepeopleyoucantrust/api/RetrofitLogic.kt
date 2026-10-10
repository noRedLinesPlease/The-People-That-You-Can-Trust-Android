package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import cornhole.beanbag.thepeopleyoucantrust.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitLogic {
    companion object {
        var gson: Gson? = GsonBuilder().setStrictness(Strictness.LENIENT)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

        const val BASE_URL = "https://api.squarespace.com/1.1/commerce/"

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.AUTH_TOKEN}")
                    .build()
                chain.proceed(request)
            }
            .build()

        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}