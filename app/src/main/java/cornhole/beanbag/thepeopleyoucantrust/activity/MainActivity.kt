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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.databinding.ActivityMainBinding
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection



class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayoutToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var viewModel: MainViewModel
    private var isInitialLoad = true

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
                R.id.nav_share_app
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


        viewModel.isOnline.observe(this) { hasInternetConnection ->
            if (!isInitialLoad && !lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                return@observe
            }

            if (hasInternetConnection == null) return@observe

            if (hasInternetConnection) {
                binding.appBar.visibility = View.VISIBLE
                binding.noInternetViewId.root.visibility = View.GONE
                binding.hasInternetViewId.root.visibility = View.VISIBLE

                if (isInitialLoad) {
                    viewModel.checkForAppUpdate(this)
                    binding.root.post {
                        askNotificationPermission()
                    }
                }
            } else {
                binding.hasInternetViewId.root.visibility = View.GONE
                binding.noInternetViewId.root.visibility = View.VISIBLE
                binding.appBar.visibility = View.GONE
            }
            isInitialLoad = false
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
        val mDisplayMetrics = windowManager.currentWindowMetrics
//        val mDisplayWidth = mDisplayMetrics.bounds.width()
//        val mDisplayHeight = mDisplayMetrics.bounds.height()
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.round_popup_dialog)

        alertDialog.show()
        val window = alertDialog.window
        window?.let {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)

            val mDisplayMetrics = windowManager.currentWindowMetrics
            val mDisplayWidth = mDisplayMetrics.bounds.width()
            val mDisplayHeight = mDisplayMetrics.bounds.height()

            layoutParams.width = (mDisplayWidth * 0.7f).toInt()
            layoutParams.height = (mDisplayHeight * 0.22f).toInt()
            layoutParams.dimAmount = 0.7f
            layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND

            it.attributes = layoutParams
        }

        val updateButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        with(updateButton)
        {
            setBackgroundColor(
                if (isDarkMode) (resources.getColor(R.color.darkModePopupBackgroundColor, null))
                else resources.getColor(R.color.dayModePopupBackgroundColor, null)
            )
            setPadding(0, 0, 0, 0)
            setTextColor(if (isDarkMode) getColor(R.color.teal_200) else getColor(R.color.custom_blue))
        }

        val dismissButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        with(dismissButton)
        {
            setBackgroundColor(
                if (isDarkMode) resources.getColor(R.color.darkModePopupBackgroundColor, null)
                else resources.getColor(R.color.dayModePopupBackgroundColor, null)
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
}
