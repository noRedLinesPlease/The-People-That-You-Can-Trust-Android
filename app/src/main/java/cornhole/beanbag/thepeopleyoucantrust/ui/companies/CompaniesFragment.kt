package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyList
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitAPI
import cornhole.beanbag.thepeopleyoucantrust.api.RetrofitLogic
import cornhole.beanbag.thepeopleyoucantrust.databinding.FragmentCompaniesBinding
import cornhole.beanbag.thepeopleyoucantrust.ui.home.CompaniesViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CompaniesFragment : Fragment() {
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
        val homeViewModel =
            ViewModelProvider(this).get(CompaniesViewModel::class.java)

        val context = requireContext()

        _binding = FragmentCompaniesBinding.inflate(inflater, container, false)
        searchBarLayout = binding.textInputLayout
        searchBarView = binding.searchBarView
        val cancelButton = binding.cancelButton
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchResultsAdapter = SearchResultAdapter(
            filteredList,
            context
        ) { onVHClick() }

        val scale = context.resources.displayMetrics.density
        val pixels = (320 * scale + 0.5f)
        val colorInt: Int = context.getColor(R.color.blue)
        val csl = ColorStateList.valueOf(colorInt)

        searchBarLayout.isEndIconVisible = false

        searchBarView.onFocusChangeListener = View.OnFocusChangeListener { _, focused ->
            if (focused) {
                if(searchBarView.text.isNotEmpty()) {
                    searchBarLayout.isEndIconVisible = true
                }
                searchBarLayout.layoutParams.width = pixels.toInt()
                cancelButton.visibility = View.VISIBLE
            } else {
                if(filteredList.isNotEmpty()) {
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
                        Log.v("BM90", companyList.toString())
                        adapter = BusinessRVAdapter(companyList, context)
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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchBarView.text.isEmpty()) {
                    getDefaultView()
                    adapter.updateCompaniesList(companyList)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                filteredList = companyList.filter {
                    it.searchTagsAsString.contains(s.toString())
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

    private fun onVHClick() {
        recyclerView.adapter = adapter
        adapter.updateCompaniesList(filteredList)
        searchBarView.clearFocus()
        searchBarLayout.isEndIconVisible = false
    }

    fun getDefaultView() {
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

//    override fun onStop() {
//        super.onStop()
//        searchBarView.text.clear()
//        searchBarView.clearFocus()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}