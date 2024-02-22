package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class CompanyList(
    @SerializedName("products")
    val companies: ArrayList<CompanyInfo>
)
