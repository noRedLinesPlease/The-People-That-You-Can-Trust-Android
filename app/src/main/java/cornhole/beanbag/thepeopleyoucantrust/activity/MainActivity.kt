package cornhole.beanbag.thepeopleyoucantrust.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.navigation.AppNavigation
import cornhole.beanbag.thepeopleyoucantrust.navigation.Screen
import cornhole.beanbag.thepeopleyoucantrust.network.NetworkConnection
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsViewModel
import cornhole.beanbag.thepeopleyoucantrust.ui.companies.CompaniesViewModel
import cornhole.beanbag.thepeopleyoucantrust.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val ONE_SIGNAL_APP_ID = "5f975718-7f3d-43e6-9101-e230069a4526"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONE_SIGNAL_APP_ID)

        val networkConnection = NetworkConnection(this)
        val mainViewModel = ViewModelProvider(
            this, MyViewModelFactory(networkConnection)
        )[MainViewModel::class.java]
        val companiesViewModel = ViewModelProvider(this)[CompaniesViewModel::class.java]
        val aboutUsViewModel = ViewModelProvider(this)[AboutUsViewModel::class.java]

        setContent {
            AppTheme {
                AppRoot(
                    mainViewModel = mainViewModel,
                    companiesViewModel = companiesViewModel,
                    aboutUsViewModel = aboutUsViewModel,
                    onOpenPlayStore = {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=cornhole.beanbag.thepeopleyoucantrust")
                            )
                        )
                    },
                    onOpenWebsite = { url ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                )
            }
        }
    }
}

@Composable
private fun AppRoot(
    mainViewModel: MainViewModel,
    companiesViewModel: CompaniesViewModel,
    aboutUsViewModel: AboutUsViewModel,
    onOpenPlayStore: () -> Unit,
    onOpenWebsite: (String) -> Unit
) {
    val context = LocalContext.current
    val isOnline by mainViewModel.isOnline.collectAsState()
    val appNeedsUpdate by mainViewModel.appNeedsToBeUpdated.observeAsState(false)
    var updateDialogDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(isOnline) {
        if (isOnline == true) {
            OneSignal.Notifications.requestPermission(false)
            mainViewModel.checkForAppUpdate(context)
            companiesViewModel.refresh()
        }
    }

    if (isOnline) {
        AppScaffold(
            companiesViewModel = companiesViewModel,
            aboutUsViewModel = aboutUsViewModel,
            onOpenWebsite = onOpenWebsite
        )
    } else {
        NoInternetScreen()
    }

    if (appNeedsUpdate == true && !updateDialogDismissed) {
        UpdateDialog(
            onUpdate = {
                updateDialogDismissed = true
                onOpenPlayStore()
            },
            onDismiss = { updateDialogDismissed = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    companiesViewModel: CompaniesViewModel,
    aboutUsViewModel: AboutUsViewModel,
    onOpenWebsite: (String) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val companiesOnSale by companiesViewModel.companiesOnSale.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                navController = navController,
                onDestinationSelected = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.navigation_drawer_open)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (companiesOnSale.isNotEmpty()) {
                    ScrollingBanner(
                        text = companiesViewModel.companiesOnSaleWithDesc.joinToString(separator = " "),
                        onClick = { navController.navigate(Screen.Sales.route) }
                    )
                }
                Image(
                    painter = painterResource(R.drawable.app_logo_vector),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                AppNavigation(
                    navController = navController,
                    companiesViewModel = companiesViewModel,
                    aboutUsViewModel = aboutUsViewModel,
                    onCompanyClick = onOpenWebsite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    navController: NavHostController,
    onDestinationSelected: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .width(300.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val items = listOf(
            Screen.Browse to stringResource(R.string.menu_browse_companies),
            Screen.Search to stringResource(R.string.menu_search_companies),
            Screen.Sales to stringResource(R.string.menu_company_sales),
            Screen.AboutUs to stringResource(R.string.menu_about_us),
            Screen.Share to stringResource(R.string.menu_share_app)
        )

        items.forEach { (screen, label) ->
            NavigationDrawerItem(
                label = { Text(label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    onDestinationSelected()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScrollingBanner(
    text: String,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color.Black else colorResource(R.color.custom_tan)
    val textColor = if (isDarkMode) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = textColor,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    repeatDelayMillis = 0,
                    initialDelayMillis = 0,
                    velocity = 80.dp
                )
        )
    }
}

@Composable
private fun UpdateDialog(
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.app_update_title_text),
                fontSize = 24.sp
            )
        },
        text = { Text(stringResource(R.string.app_update_body_text)) },
        confirmButton = {
            TextButton(onClick = onUpdate) {
                Text(
                    stringResource(R.string.pop_up_dialog_update_btn_text),
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.pop_up_dialog_dismiss_btn_text),
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    )
}

@Composable
private fun NoInternetScreen() {
    val isDarkMode = isSystemInDarkTheme()
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.app_logo_vector),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_internet_error_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily(Font(R.font.bungee_font)),
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }

    }
}
