package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.databinding.BrowseAllCompaniesLayoutBinding
import cornhole.beanbag.thepeopleyoucantrust.ui.OnClickAdapter
import kotlinx.coroutines.launch


class BrowseCompaniesFragment : Fragment(), OnClickAdapter {
    private var _binding: BrowseAllCompaniesLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriesRV: RecyclerView
    private lateinit var categoriesAdapter: BrowseCompaniesAdapter

    private lateinit var companiesRecyclerView: RecyclerView
    private lateinit var adapter: BusinessRVAdapter

    private val viewModel: CompaniesViewModel by lazy {
        ViewModelProvider(this)[CompaniesViewModel::class.java]
    }

    private var unfilteredCompanyList: ArrayList<CompanyInfo> = arrayListOf()
    private var filteredCompanyList: ArrayList<CompanyInfo> = arrayListOf()
    private var categorySelected = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()

        _binding = BrowseAllCompaniesLayoutBinding.inflate(inflater, container, false)

        categoriesRV = binding.categoriesRv
        adapter = BusinessRVAdapter(
            companyList = filteredCompanyList,
            context = requireContext(),
            onClickVH = this
        )

        companiesRecyclerView = binding.browseCompaniesListRv
        companiesRecyclerView.layoutManager = LinearLayoutManager(context)
        companiesRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                when {
                    isLoading -> {
                        binding.progressBarView.visibility = View.VISIBLE
                        binding.browseCompaniesListRv.visibility = View.GONE
                        binding.categoriesRv.visibility = View.GONE
                    }

                    else -> {
                        if (viewModel.companyList.value.isNullOrEmpty()) {
                            showErrorOnClick()
                        } else {
                            unfilteredCompanyList = viewModel.companyList.value ?: arrayListOf()
                            binding.progressBarView.visibility = View.GONE
                            binding.browseCompaniesListRv.visibility = View.VISIBLE
                            binding.categoriesRv.visibility = View.VISIBLE
                            addCategoryListToRV()
                            filterSearchResults(categorySelected, unfilteredCompanyList)
                        }
                    }
                }
            }

            viewModel.companyList.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.updateCompaniesList(it)
                }
            }
        }
        return binding.root
    }

    private fun addCategoryListToRV() {
        categoriesRV.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(RvItemDecoration(1))
        }
        categoriesRV.layoutManager = GridLayoutManager(requireContext(), 3)
        categoriesAdapter =
            BrowseCompaniesAdapter(
                viewModel.businessCategoryList,
                requireContext(),
            ) { theCategorySelected ->
                categorySelected = theCategorySelected
                filterSearchResults(categorySelected, unfilteredCompanyList)
            }
        categoriesRV.adapter = categoriesAdapter
    }

    private fun filterSearchResults(
        categorySelected: String,
        companyListOriginal: ArrayList<CompanyInfo>
    ) {
        unfilteredCompanyList.forEach { company ->
            company.companyListingCategoryAsString =
                company.companyListingCategoryList.joinToString(" ")
        }
        filteredCompanyList = if (categorySelected == "") {
            viewModel.companyList.value ?: arrayListOf()
        } else {
            companyListOriginal.filter {
                it.companyListingCategoryAsString.contains(
                    categorySelected
                )
            } as ArrayList<CompanyInfo>
        }
        adapter.updateCompaniesList(filteredCompanyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showErrorOnClick() {
        binding.browseCompaniesListRv.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
    }

    override fun navigateToWebsiteOnClick(companyWebsiteLink: String) {
        binding.errorView.visibility = View.GONE
        binding.browseCompaniesListRv.visibility = View.VISIBLE
        val url = Uri.parse(companyWebsiteLink)
        val sendIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(sendIntent)
    }

    override fun onPause() {
        super.onPause()
        categoriesAdapter.clearIsSelectedList()
    }
}
