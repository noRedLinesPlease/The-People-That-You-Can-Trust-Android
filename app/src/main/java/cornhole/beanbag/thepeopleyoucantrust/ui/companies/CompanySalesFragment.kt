package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.databinding.CompanySalesFragmentBinding
import cornhole.beanbag.thepeopleyoucantrust.ui.OnClickAdapter

class CompanySalesFragment : Fragment(), OnClickAdapter {
    private var _binding: CompanySalesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriesRV: RecyclerView
    private val viewModel: CompaniesViewModel by lazy {
        ViewModelProvider(requireActivity())[CompaniesViewModel::class.java]
    }

    private lateinit var adapter: BusinessRVAdapter

    private lateinit var saleCompaniesRV: RecyclerView

    private var companiesOnSaleList: ArrayList<CompanyInfo> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CompanySalesFragmentBinding.inflate(inflater, container, false)
        savedInstanceState?.putBoolean("fromFragment", true)
        companiesOnSaleList = viewModel.getCompaniesOnSale()

        adapter = BusinessRVAdapter(
            companyList = companiesOnSaleList,
            context = requireContext(),
            onClickVH = this
        )

        saleCompaniesRV = binding.saleCompaniesListRv
        saleCompaniesRV.layoutManager = LinearLayoutManager(requireContext())
        saleCompaniesRV.adapter = adapter

        return binding.root
    }

    override fun showErrorOnClick() {
        binding.saleCompaniesListRv.visibility = View.GONE
        //binding.errorView.visibility = View.VISIBLE
    }

    override fun navigateToWebsiteOnClick(companyWebsiteLink: String) {
        //binding.errorView.visibility = View.GONE
        binding.saleCompaniesListRv.visibility = View.VISIBLE
        val url = companyWebsiteLink.toUri()
        val sendIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(sendIntent)
    }
}