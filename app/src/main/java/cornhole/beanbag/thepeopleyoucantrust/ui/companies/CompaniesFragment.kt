package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import cornhole.beanbag.thepeopleyoucantrust.ui.OnClickAdapter
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import cornhole.beanbag.thepeopleyoucantrust.databinding.FragmentCompaniesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompaniesFragment : Fragment(), OnClickAdapter {
    private var _binding: FragmentCompaniesBinding? = null
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

        _binding = FragmentCompaniesBinding.inflate(inflater, container, false)

        searchBarLayout = binding.textInputLayout
        searchBarView = binding.searchBarView
        val cancelButton = binding.cancelButton
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchResultsAdapter = SearchResultAdapter(
            filteredList,
            context,
            this
        )

        binding.progressBarView.visibility = View.VISIBLE
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

        //Create an ApiService instance from the Retrofit instance.
        val retrofitApi = RetrofitLogic.retrofit
        val service: RetrofitAPI = retrofitApi.create(RetrofitAPI::class.java)
        val call: Call<CompanyList> =
            service.getCompanies("PHYSICAL")

        call.enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val apiResponse: CompanyList? = response.body()

                    if (apiResponse != null) {
                        companyList = apiResponse.companies
                        binding.progressBarView.visibility = View.GONE
                        binding.companiesLayout.visibility = View.VISIBLE
                        getDefaultView()
                        filterSearchResults()
                    }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                Toast.makeText(context, "Request Fail", Toast.LENGTH_SHORT).show()
                Log.v("BM90", "Parsing error")
            }
        })

        return binding.root
    }

    private fun filterSearchResults() {
        companyList.forEach { company ->
            company.searchTagsAsString = company.companySearchTags.joinToString(",")
        }

        searchBarView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(searchText: Editable?) {
                filteredList = companyList.filter {
                    it.searchTagsAsString.contains(
                        searchText.toString().trim(),
                        ignoreCase = true
                    ) ||
                            it.companyName.contains(searchText.toString().trim(), ignoreCase = true)
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
        binding.recyclerView.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
    }

    override fun navigateToWebsiteOnClick(companyWebsiteLink: String) {
        binding.errorView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        val url = Uri.parse(companyWebsiteLink)
        val sendIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(sendIntent)
    }
}