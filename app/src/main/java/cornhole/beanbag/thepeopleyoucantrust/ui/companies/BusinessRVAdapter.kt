package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.content.ActivityNotFoundException
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cornhole.beanbag.thepeopleyoucantrust.ui.OnClickAdapter
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.R

class BusinessRVAdapter(
    private var companyList: ArrayList<CompanyInfo>,
    private val context: Context,
    val onClickVH: OnClickAdapter
) : RecyclerView.Adapter<BusinessRVViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_viewholder, parent, false)
        return BusinessRVViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessRVViewHolder, position: Int) {
        val companyLogoImage = companyList[position].companyLogoList[0]
        val companyWebsiteString = companyLogoImage.companyWebsite
        holder.companyNameTextView.text = companyList[position].companyName
        Glide.with(context)
            .load(companyLogoImage.logoUrl)
            .into(holder.companyLogoImageView)

        holder.itemView.setOnClickListener {
            try {
                onClickVH.navigateToWebsiteOnClick(companyWebsiteString)
//                val url = Uri.parse(companyWebsiteString)
//                val sendIntent = Intent(Intent.ACTION_VIEW, url)
//                startActivity(context, sendIntent, null)
            } catch (e: ActivityNotFoundException) {
                    onClickVH.showErrorOnClick()
            }

        }
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
}