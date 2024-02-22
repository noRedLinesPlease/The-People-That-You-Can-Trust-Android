package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class CompanyLogoImage (
    @SerializedName("url")
    val logoUrl: String,
    @SerializedName("altText")
    val companyWebsite: String
)