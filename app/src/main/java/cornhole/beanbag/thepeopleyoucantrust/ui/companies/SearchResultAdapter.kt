package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.R

class SearchResultAdapter(
    private var searchResultList: ArrayList<CompanyInfo>,
    private val context: Context,
    val onClickVH: () -> Unit
) :  RecyclerView.Adapter<SearchResultViewHolder>(){
    private val noResultsFoundList = arrayListOf("No results found")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_results_row_view, parent, false)
        view.setOnClickListener { onClickVH }
        return SearchResultViewHolder(view, onClickVH)
    }

    override fun getItemCount(): Int {
        return if (searchResultList.isEmpty()) {
            noResultsFoundList.size
        } else {
            searchResultList.size
        }
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        if (searchResultList.isEmpty()) {
            holder.searchResultCompanyLogo.visibility = View.GONE
            holder.searchResultCompanyName.visibility = View.GONE
            holder.noSearchResultsFoundTV.text = noResultsFoundList[0]
        } else {
            holder.searchResultCompanyName.text = searchResultList[position].companyName
            Glide.with(context)
                .load(searchResultList[position].companyLogoList[0].logoUrl)
                .into(holder.searchResultCompanyLogo)

        }
    }

    fun updateSearchResultsList(updatedList: ArrayList<CompanyInfo>) {
        searchResultList = updatedList
        notifyDataSetChanged()
    }

    fun useNoResultsFound() {
        searchResultList.clear()
        notifyDataSetChanged()
    }
}

class SearchResultViewHolder(itemView: View, onClickVH: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener { onClickVH() }
    }
    val searchResultCompanyLogo: ImageView = itemView.findViewById(R.id.company_logo_search_result)
    val searchResultCompanyName: TextView = itemView.findViewById(R.id.company_name_search_result)
    val noSearchResultsFoundTV: TextView = itemView.findViewById(R.id.no_results_found_tv)
}