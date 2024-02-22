//package cornhole.beanbag.thepeopleyoucantrust
//
//import android.content.Context
//import android.util.Log
//import android.widget.ImageView
//import android.widget.Toast
//import retrofit2.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class ApiClass {
//    fun testSettingVariables(context: Context, callback: (List<CompanyList>) -> Unit) {
//        val retrofit: Retrofit = Retrofit.Builder()
//            .baseUrl("https://api.squarespace.com/1.1/commerce/products")
//            .addConverterFactory(
//                GsonConverterFactory.create()
//            ).build()
//
//        // Create an ApiService instance from the Retrofit instance.
//        val service: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
//        val call: Call<CompanyList> =
//            service.getCompanies("b60b0830-9d66-400a-845e-6838ded5669d")
//
//        call.enqueue(object : Callback<CompanyList> {
//
//            override fun onResponse(
//                call: Call<List<CompanyList>>,
//                response: Response<List<CompanyList>>
//            ) {
//                if (response.isSuccessful) {
//                    // If the response is successful, parse the
//                    // response body to a DataModel object.
//                    val companies: List<CompanyList>? = response.body()
//
//
//                    // Call the callback function with the DataModel
//                    // object as a parameter.
//                    if (companies != null) {
//                        callback(companies)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<CompanyList>>, t: Throwable) {
//                Toast.makeText(context, "Request Fail", Toast.LENGTH_SHORT).show()
//            }
//        }
//        )
//    }
//
//}