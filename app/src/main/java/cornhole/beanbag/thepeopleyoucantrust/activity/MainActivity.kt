package cornhole.beanbag.thepeopleyoucantrust.activity

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.api.CompanyInfo
import cornhole.beanbag.thepeopleyoucantrust.databinding.ActivityMainBinding
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayoutToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var viewModel: MainViewModel
    private var isInitialLoad = true

    private var bannerTextString = ""

    private lateinit var companiesList: List<CompanyInfo>

    private val ONE_SIGNAL_APP_ID = "5f975718-7f3d-43e6-9101-e230069a4526"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // TODO: Create var to allow the push notifications to be sent to users phone
            } else {
                // TODO: Don't ask user again upon reopening the app
            }
        }

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkMode = isPhoneInDarkMode(this)

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONE_SIGNAL_APP_ID)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout

        val networkConnection = NetworkConnection(this)
        val factory = MyViewModelFactory(networkConnection)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_about_us,
                R.id.nav_browse_companies,
                R.id.nav_search_companies,
                R.id.nav_share_app,
                R.id.nav_company_sales
            ),
            drawerLayout
        )

        drawerLayoutToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
        companiesList = viewModel.companyList

        lifecycleScope.launch {
            companiesList = viewModel.getCompanies() ?: arrayListOf()
        }


        viewModel.isOnline.observe(this) { hasInternetConnection ->
            if (!isInitialLoad && !lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                return@observe
            }

            if (hasInternetConnection == null) return@observe

            if (hasInternetConnection) {
                binding.appBar.visibility = View.VISIBLE
                binding.noInternetViewId.root.visibility = View.GONE
                binding.hasInternetViewId.root.visibility = View.VISIBLE

                lifecycleScope.launch {
                    OneSignal.Notifications.requestPermission(false)
                }
                    viewModel.checkForAppUpdate(this)
            } else {
                binding.hasInternetViewId.root.visibility = View.GONE
                binding.noInternetViewId.root.visibility = View.VISIBLE
                binding.appBar.visibility = View.GONE
            }
            isInitialLoad = false
        }
        viewModel.companiesOnSale.observe(this) { companiesList ->
            if (!companiesList.isNullOrEmpty()) {
                startBannerAnimation()
            }
        }
        viewModel.appNeedsToBeUpdated.observe(this) { needsUpdate ->
            // This observer will automatically run whenever the value of appNeedsToBeUpdated changes.
            if (needsUpdate == true && viewModel.dialogShowedOnce.value == false) {
                showCustomDialog()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val bundleMain = Bundle()

        return when (item.title) {
            "About Us" -> {
                bundleMain.putBoolean("fromFragment", true)
                navController.navigate(R.id.nav_about_us, bundleMain)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            "Browse Companies" -> {
                bundleMain.putBoolean("fromFragment", true)
                navController.navigate(R.id.nav_browse_companies, bundleMain)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            "Search Companies" -> {
                bundleMain.putBoolean("fromFragment", true)
                navController.navigate(R.id.nav_search_companies, bundleMain)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            "Share App" -> {
                bundleMain.putBoolean("fromFragment", true)
                navController.navigate(R.id.nav_share_app, bundleMain)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            "Company Sales" -> {
                bundleMain.putBoolean("fromFragment", true)
                navController.navigate(R.id.nav_company_sales, bundleMain)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            else -> false
        }
    }

    private fun isPhoneInDarkMode(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun showCustomDialog() {
        viewModel.dialogShowedOnce.value = true
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        with(builder) {
            val titleTextView = TextView(context)
            titleTextView.text = resources.getString(R.string.app_update_title_text)
            titleTextView.textSize = 24F
            setCustomTitle(titleTextView)
            setMessage(resources.getString(R.string.app_update_body_text))
            setPositiveButton(
                R.string.pop_up_dialog_update_btn_text
            ) { _, _ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=cornhole.beanbag.thepeopleyoucantrust")
                )
                startActivity(intent)
            }
            setNegativeButton(R.string.pop_up_dialog_dismiss_btn_text) { dialog, _ ->
                dialog.dismiss()
                val bundleMain = Bundle()
                bundleMain.putBoolean("fromFragment", true)
            }

        }

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.round_popup_dialog)

        alertDialog.show()
        val window = alertDialog.window
        window?.let {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)

            val mDisplayMetrics = windowManager.currentWindowMetrics
            val mDisplayWidth = mDisplayMetrics.bounds.width()

            layoutParams.width = (mDisplayWidth * 0.7f).toInt()
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.dimAmount = 0.7f
            layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND

            it.attributes = layoutParams
        }

        val updateButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        with(updateButton)
        {
            setBackgroundColor(
                if (isDarkMode) ContextCompat.getColor(this@MainActivity, R.color.darkModePopupBackgroundColor)
                else ContextCompat.getColor(this@MainActivity, R.color.dayModePopupBackgroundColor)
            )
            setPadding(0, 0, 0, 0)
            setTextColor(if (isDarkMode) getColor(R.color.teal_200) else getColor(R.color.custom_blue))
        }

        val dismissButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        with(dismissButton)
        {
            setBackgroundColor(
                if (isDarkMode) ContextCompat.getColor(this@MainActivity, R.color.darkModePopupBackgroundColor)
                else ContextCompat.getColor(this@MainActivity, R.color.dayModePopupBackgroundColor)
            )
            setPadding(0, 0, 60, 0)
            setTextColor(if (isDarkMode) getColor(R.color.teal_200) else getColor(R.color.custom_blue))
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level 33 and above (Android 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted. You can proceed with notifications.
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // This case means the user has denied the permission before, but not permanently.
                    // You can show a dialog here explaining why you need the permission
                    // and then launch the permission request.
                    // For simplicity, this example will just directly ask again.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun startBannerAnimation() {
        val banner = binding.hasInternetViewId.bannerText
        val names = arrayListOf<String>()
        viewModel.companiesOnSale.value?.forEach {
            names.add(it.companyName)
        }
        bannerTextString = getString(R.string.scrolling_banner_text)
        banner.text = if (names.isNotEmpty())  bannerTextString  else " "
        banner.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_company_sales)

        }

        banner.ellipsize = null

        // Start from the right side of the screen
        //binding.hasInternetViewId.bannerText.translationX = screenWidth

        if (isDarkMode) {
            binding.hasInternetViewId.bannerText.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            binding.hasInternetViewId.bannerText.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.hasInternetViewId.bannerText.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_tan))
            binding.hasInternetViewId.bannerText.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        // Measure text width to know when to reset
        banner.post {
            val screenWidth = resources.displayMetrics.widthPixels.toFloat()

            val textWidth = banner.paint.measureText(banner.text.toString())

            val params = banner.layoutParams
            params.width = textWidth.toInt()
            banner.layoutParams = params

            val animator = androidx.core.animation.ObjectAnimator.ofFloat(
                binding.hasInternetViewId.bannerText,
                "translationX",
                screenWidth,
                -textWidth
            )
            animator.duration = 10000 // Adjust for speed
            animator.repeatCount = androidx.core.animation.ValueAnimator.INFINITE
            animator.interpolator = androidx.core.animation.LinearInterpolator()
            animator.start()
        }
    }
}
