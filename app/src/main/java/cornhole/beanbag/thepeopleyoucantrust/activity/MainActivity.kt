package cornhole.beanbag.thepeopleyoucantrust.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkMode = isPhoneInDarkMode(this)

        val networkConnection = NetworkConnection(this)

        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(networkConnection)
        )[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout

        viewModel.isOnline.observe(this) { hasInternetConnection ->
            if (!hasInternetConnection) {
                binding.hasInternetViewId.root.visibility = View.GONE
                binding.noInternetViewId.root.visibility = View.VISIBLE
                binding.appBar.visibility = View.GONE
            } else {
                viewModel.checkForAppUpdate(this)
                viewModel.appNeedsToBeUpdated.observe(this) { updateNeeded ->
                    if (updateNeeded && viewModel.dialogShowedOnce.value == false) {
                        showCustomDialog()
                    }
                }

                binding.appBar.visibility = View.VISIBLE
                binding.noInternetViewId.root.visibility = View.GONE
                binding.hasInternetViewId.root.visibility = View.VISIBLE

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
        val mDisplayWidth = mDisplayMetrics.bounds.width()
        val mDisplayHeight = mDisplayMetrics.bounds.height()
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.round_popup_dialog)

        alertDialog.show()
        val mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams.width = (mDisplayWidth * 0.7f).toInt()
        mLayoutParams.height = (mDisplayHeight * 0.22f).toInt()
        mLayoutParams.dimAmount = 0.7f
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND

        alertDialog.window?.attributes = mLayoutParams

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
}
