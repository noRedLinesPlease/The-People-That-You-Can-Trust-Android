package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class Variants(@SerializedName("pricing") val pricingObject: OnSaleAttribute)