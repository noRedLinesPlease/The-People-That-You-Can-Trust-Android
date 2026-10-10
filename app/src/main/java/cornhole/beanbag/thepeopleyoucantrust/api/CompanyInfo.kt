package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class CompanyInfo(
    @SerializedName("name")
    var companyName: String,
    @SerializedName("description")
    val saleDescription: String,
    @SerializedName("tags")
    var companySearchTags: ArrayList<String>,
    @SerializedName("variantAttributes")
    var companyListingCategoryList: ArrayList<String>,
    @SerializedName("images")
    val companyLogoList: ArrayList<CompanyLogoImage>,
    @SerializedName("variants")
    var variantsObject: List<Variants>
)

