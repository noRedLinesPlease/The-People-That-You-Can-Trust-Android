package cornhole.beanbag.thepeopleyoucantrust.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.google.android.material.navigation.NavigationView.VISIBLE
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayoutToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val networkConnection = NetworkConnection(this)

        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(networkConnection)
        )[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout

        viewModel.isOnline.observe(this) {
            if (!it) {
                binding.hasInternetViewId.root.visibility = View.GONE
                binding.noInternetViewId.root.visibility = View.VISIBLE
                binding.appBar.visibility = View.GONE
            } else {
                binding.appBar.visibility = View.VISIBLE
                binding.noInternetViewId.root.visibility = View.GONE
                binding.hasInternetViewId.root.visibility = View.VISIBLE

                setSupportActionBar(binding.toolbar)

                navView = binding.navView

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                navController = navHostFragment.navController

                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_home,
                        R.id.nav_companies
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
        return when (item.title) {
            "Home" -> {
                navController.navigate(R.id.nav_home)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            "Companies" -> {
                navController.navigate(R.id.nav_companies)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            else -> false

        }
    }
}