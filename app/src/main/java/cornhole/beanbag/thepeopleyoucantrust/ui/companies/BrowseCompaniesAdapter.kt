package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.api.BusinessCategories


class BrowseCompaniesAdapter(
    var categoriesList: ArrayList<BusinessCategories>,
    val context: Context,
    val onCategoryClicked: (String) -> Unit,
) : RecyclerView.Adapter<BrowseCompaniesViewHolder>() {
    private var previousSelection: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseCompaniesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.browse_companies_viewholder, parent, false)
        return BrowseCompaniesViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrowseCompaniesViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val listItem = categoriesList[position]
        holder.businessCategoryTV.text = categoriesList[position].businessCategory
        if (!listItem.isSelected) {
            holder.businessCategoryTV.setTextColor(Color.BLUE)
        } else {
            holder.businessCategoryTV.setTextColor(context.resources.getColor(R.color.teal_200, null))
            holder.businessCategoryTV.isEnabled = false
        }

        holder.itemView.setOnClickListener {
            onCategoryClicked(listItem.businessCategory)
            listItem.isSelected = true

            if (previousSelection != -1) {
                categoriesList[previousSelection].isSelected = false
            }
            previousSelection = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    fun clearIsSelectedList() {
        categoriesList.forEach {
            it.isSelected = false
        }
    }
}

class BrowseCompaniesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val businessCategoryTV: TextView = itemView.findViewById(R.id.business_category_tv)
}