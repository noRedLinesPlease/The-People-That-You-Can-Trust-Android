package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class CompanyInfo(
    @SerializedName("urlSlug")
    val companyId: String,
    @SerializedName("name")
    var companyName: String,
    @SerializedName("tags")
    var companySearchTags: ArrayList<String>,
    var searchMatches: Boolean = false,
    var searchTagsAsString: String = "",
    @SerializedName("images")
    val companyLogoList: ArrayList<CompanyLogoImage>
)

