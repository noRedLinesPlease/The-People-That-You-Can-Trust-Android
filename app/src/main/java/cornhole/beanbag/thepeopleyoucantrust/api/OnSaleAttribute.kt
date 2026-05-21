package cornhole.beanbag.thepeopleyoucantrust.api

import com.google.gson.annotations.SerializedName

data class OnSaleAttribute(@SerializedName("onSale") val isOnSale: Boolean)