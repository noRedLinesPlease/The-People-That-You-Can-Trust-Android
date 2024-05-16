package cornhole.beanbag.thepeopleyoucantrust.ui.shareApp

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import cornhole.beanbag.thepeopleyoucantrust.databinding.ShareAppFragmentBinding


class ShareAppFragment : Fragment() {
    private var _binding: ShareAppFragmentBinding? = null
    private val binding get() = _binding!!
    private var toastText = ""
    private var androidAppLink =
        "https://play.google.com/store/apps/details?id=cornhole.beanbag.thepeopleyoucantrust"
    private var iosAppLink = "https://apps.apple.com/us/app/thepeoplethatyoucantrust/id6479319870"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShareAppFragmentBinding.inflate(inflater, container, false)
        binding.iosCopyIcon.setOnClickListener {
            toastText = "Link copied to clipboard: iOS"
            requireContext().copyToClipboard(iosAppLink)
            activity?.showCenteredSnackbar(toastText)
        }

        binding.androidCopyIcon.setOnClickListener {
            toastText = "Link copied to clipboard: Android"
            requireContext().copyToClipboard(androidAppLink)
            activity?.showCenteredSnackbar(toastText)
        }

        return binding.root
    }
    private fun Activity.showCenteredSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.CENTER
        snackbar.view.layoutParams = layoutParams
        snackbar.show()
    }

    private fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

}