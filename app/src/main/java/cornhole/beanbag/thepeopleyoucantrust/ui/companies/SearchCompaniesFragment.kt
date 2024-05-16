package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.databinding.SearchCompaniesFragmentBinding
import cornhole.beanbag.thepeopleyoucantrust.ui.OnClickAdapter
import kotlinx.coroutines.launch

class SearchCompaniesFragment : Fragment(), OnClickAdapter {
    private var _binding: SearchCompaniesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchBarView: EditText
    private lateinit var searchBarLayout: TextInputLayout
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: BusinessRVAdapter
    private lateinit var searchResultsAdapter: SearchResultAdapter
    lateinit var companyList: ArrayList<CompanyInfo>
    private var filteredList: ArrayList<CompanyInfo> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val viewModel: CompaniesViewModel by lazy {
            ViewModelProvider(this)[CompaniesViewModel::class.java]
        }

        _binding = SearchCompaniesFragmentBinding.inflate(inflater, container, false)
        val args = arguments?.getBoolean("fromFragment")
        if (args != null) {
            viewModel.navigatedFromFragment = args
        }

        searchBarLayout = binding.textInputLayout
        searchBarView = binding.searchBarView
        val cancelButton = binding.cancelButton
        recyclerView = binding.companiesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchResultsAdapter = SearchResultAdapter(
            filteredList,
            context,
            this
        )

        binding.companiesLayout.visibility = View.GONE

        val scale = context.resources.displayMetrics.density
        val pixels = (320 * scale + 0.5f)

        searchBarLayout.isEndIconVisible = false

        searchBarView.onFocusChangeListener = View.OnFocusChangeListener { _, focused ->
            if (focused) {
                if (searchBarView.text.isNotEmpty()) {
                    searchBarLayout.isEndIconVisible = true
                }
                searchBarLayout.layoutParams.width = pixels.toInt()
                cancelButton.visibility = View.VISIBLE
            } else {
                if (filteredList.isNotEmpty()) {
                    getDefaultView()
                    adapter.updateCompaniesList(filteredList)
                } else {
                    getSearchView()
                }
                cancelButton.visibility = View.GONE
                searchBarLayout.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                hideKeyboard()
            }
        }

        searchBarView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBarView.clearFocus()
                searchBarLayout.isEndIconVisible = false
            }
            true
        }

        cancelButton.setOnClickListener {
            searchBarView.text.clear()
            searchBarView.clearFocus()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (!isLoading) {
                    binding.progressBarView.visibility = View.GONE
                    binding.companiesRecyclerView.visibility = View.VISIBLE
                    binding.companiesLayout.visibility = View.VISIBLE
                    companyList = viewModel.companyList.value ?: arrayListOf()

                    if (viewModel.companyList.value.isNullOrEmpty()) {
                        binding.companiesRecyclerView.visibility = View.GONE
                        binding.companiesLayout.visibility = View.GONE
                        binding.errorView.visibility = View.VISIBLE
                    } else {
                        binding.companiesRecyclerView.visibility = View.VISIBLE
                        binding.companiesLayout.visibility = View.VISIBLE
                        getDefaultView()
                        filterSearchResults()
                    }
                } else {
                    binding.progressBarView.visibility = View.VISIBLE
                    binding.companiesLayout.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    private fun filterSearchResults() {
        if (searchBarView.text.isEmpty()) {
            binding.companiesRecyclerView.visibility = View.GONE
        }
        companyList.forEach { company ->
            company.searchTagsAsString = company.companySearchTags.joinToString(",")
        }

        searchBarView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchBarView.text.isEmpty() || searchBarView.text.length < 3) {
                    binding.companiesRecyclerView.visibility = View.GONE
                } else {
                    binding.companiesRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(searchText: Editable?) {
                filteredList = companyList.filter {
                    it.searchTagsAsString.contains(
                        searchText.toString().trim(),
                        ignoreCase = true
                    ) || it.companyName.contains(searchText.toString().trim(), ignoreCase = true)
                } as ArrayList<CompanyInfo>

                if (searchBarView.text.isNotEmpty()) {
                    getSearchView()
                } else {
                    getDefaultView()
                }
            }
        })
    }

    private fun getSearchView() {
        recyclerView.adapter = searchResultsAdapter
        if (filteredList.isEmpty()) {
            searchResultsAdapter.useNoResultsFound()
        } else {
            searchResultsAdapter.updateSearchResultsList(filteredList)
        }
    }

    fun getDefaultView() {
        adapter = BusinessRVAdapter(companyList, requireContext(), this)
        recyclerView.adapter = adapter
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        searchBarView.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showErrorOnClick() {
        binding.companiesRecyclerView.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
    }

    override fun navigateToWebsiteOnClick(companyWebsiteLink: String) {
        binding.errorView.visibility = View.GONE
        binding.companiesRecyclerView.visibility = View.VISIBLE
        val url = Uri.parse(companyWebsiteLink)
        val sendIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(sendIntent)
    }
}