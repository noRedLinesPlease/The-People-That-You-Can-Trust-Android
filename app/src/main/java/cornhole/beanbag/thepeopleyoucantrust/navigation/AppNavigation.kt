package cornhole.beanbag.thepeopleyoucantrust.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsScreen
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsViewModel
import cornhole.beanbag.thepeopleyoucantrust.ui.companies.BrowseCompaniesScreen
import cornhole.beanbag.thepeopleyoucantrust.ui.companies.CompanySalesScreen
import cornhole.beanbag.thepeopleyoucantrust.ui.companies.CompaniesViewModel
import cornhole.beanbag.thepeopleyoucantrust.ui.companies.SearchCompaniesScreen
import cornhole.beanbag.thepeopleyoucantrust.ui.shareApp.ShareAppScreen

sealed class Screen(val route: String) {
    object Browse  : Screen("browse")
    object Search  : Screen("search")
    object Sales   : Screen("sales")
    object AboutUs : Screen("about_us")
    object Share   : Screen("share")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    companiesViewModel: CompaniesViewModel,
    aboutUsViewModel: AboutUsViewModel,
    onCompanyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Browse.route,
        modifier = modifier
    ) {
        composable(Screen.Browse.route) {
            BrowseCompaniesScreen(
                viewModel = companiesViewModel,
                onCompanyClick = onCompanyClick
            )
        }
        composable(Screen.Search.route) {
            SearchCompaniesScreen(
                viewModel = companiesViewModel,
                onCompanyClick = onCompanyClick
            )
        }
        composable(Screen.Sales.route) {
            CompanySalesScreen(
                viewModel = companiesViewModel,
                onCompanyClick = onCompanyClick
            )
        }
        composable(Screen.AboutUs.route) {
            AboutUsScreen(
                viewModel = aboutUsViewModel,
                onNavigateToCompanies = {
                    navController.navigate(Screen.Browse.route)
                }
            )
        }
        composable(Screen.Share.route) {
            ShareAppScreen()
        }
    }
}
