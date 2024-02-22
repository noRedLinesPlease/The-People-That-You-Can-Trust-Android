package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.R

class BusinessRVAdapter(
    private var companyList: ArrayList<CompanyInfo>,
    private val context: Context
) : RecyclerView.Adapter<BusinessRVViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_viewholder, parent, false)
        return BusinessRVViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessRVViewHolder, position: Int) {
        val companyLogoImageList = companyList[position].companyLogoList[0]
        val companyWebsiteString = companyLogoImageList.companyWebsite
        val companyWebsiteDisplay = companyWebsiteString.substringAfterLast("/")
        holder.companyNameTextView.text = companyList[position].companyName
        Glide.with(context)
            .load(companyLogoImageList.logoUrl)
            .into(holder.companyLogoImageView)
        val companyWebsiteLink = ""
            //"<a href=\"$companyWebsiteString\">$companyWebsiteDisplay</a>"
        holder.companyWebsiteTextView.text = Html.fromHtml(companyWebsiteLink, Html.FROM_HTML_MODE_COMPACT)
        holder.companyWebsiteTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    fun updateCompaniesList(updatedList: ArrayList<CompanyInfo>) {
        companyList = updatedList
        notifyDataSetChanged()
    }
}

class BusinessRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val companyLogoImageView: ImageView = itemView.findViewById(R.id.company_logo_image)
    val companyNameTextView: TextView = itemView.findViewById(R.id.company_name)
    val companyWebsiteTextView: TextView = itemView.findViewById(R.id.company_website_link)
}