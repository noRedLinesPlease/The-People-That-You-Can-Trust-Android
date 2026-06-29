package cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

open class AboutUsViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val header: String = "",
        val body: String = "",
        val subHeader: String = "",
        val subHeaderBody: String = ""
    )

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch(Dispatchers.IO) {
            var header = ""
            var body = ""
            var subHeader = ""
            var subHeaderBody = ""
            try {
                val doc = Jsoup.connect("https://alligator-beige-t5e3.squarespace.com/").get()
                header = doc.getElementsByClass("fe-block-52548690efe141736640").text()
                body = doc.getElementsByClass("fe-block-a081cdeabc4137be9f5b").text()
                subHeader = doc.getElementsByClass("fe-block-afea2256dc047051d8ee").text()
                subHeaderBody = doc.getElementsByClass("fe-block-57dc43433ef1172548d6").text()
            } catch (e: Exception) {
                Log.v("BM90", e.message.toString())
            }
            withContext(Dispatchers.Main) {
                _uiState.value = UiState(
                    isLoading = false,
                    header = header,
                    body = body,
                    subHeader = subHeader,
                    subHeaderBody = subHeaderBody
                )
            }
        }
    }
}
