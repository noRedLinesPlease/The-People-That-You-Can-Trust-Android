package cornhole.beanbag.thepeopleyoucantrust.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.databinding.FragmentHomeBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    //private var isLoading = true

    private lateinit var aboutUsHeaderTV: TextView
    private lateinit var aboutUsBodyTV: TextView
    private lateinit var aboutUsSubHeaderTV: TextView
    private lateinit var aboutUsSubHeaderBodyTV: TextView
    private lateinit var navigateToCompaniesButton: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        aboutUsHeaderTV = binding.headerText
        aboutUsBodyTV = binding.bodyText
        aboutUsSubHeaderTV = binding.subHeaderText
        aboutUsSubHeaderBodyTV = binding.subHeaderBody
        navigateToCompaniesButton = binding.navigateToCompaniesPageButton

        getAboutUsPage()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToCompaniesButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigate_to_companies)
        }
    }

    private fun getAboutUsPage() {
        binding.progressBarView.visibility = View.VISIBLE
        binding.homeLayout.visibility = View.GONE

        Thread {
            var header = ""
            var body = ""
            var subHeader = ""
            var subHeaderBody = ""

            try {
                val url = "https://alligator-beige-t5e3.squarespace.com/"
                val doc: Document = Jsoup.connect(url).get()

                header = doc.getElementsByClass("fe-block-52548690efe141736640").text()
                body = doc.getElementsByClass("fe-block-a081cdeabc4137be9f5b").text()
                subHeader = doc.getElementsByClass("fe-block-afea2256dc047051d8ee").text()
                subHeaderBody = doc.getElementsByClass("fe-block-57dc43433ef1172548d6").text()

            } catch (e: Exception) {
                Log.v("BM90", e.message.toString())
            }
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    binding.progressBarView.visibility = View.GONE
                    binding.homeLayout.visibility = View.VISIBLE
                },
                300
            )

            activity?.runOnUiThread {
                aboutUsHeaderTV.text = header
                aboutUsBodyTV.text = body
                aboutUsSubHeaderTV.text = subHeader
                aboutUsSubHeaderBodyTV.text = subHeaderBody
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}