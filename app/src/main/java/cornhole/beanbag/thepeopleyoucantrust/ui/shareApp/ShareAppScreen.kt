package cornhole.beanbag.thepeopleyoucantrust.ui.shareApp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cornhole.beanbag.thepeopleyoucantrust.R
import kotlinx.coroutines.launch

@Composable
fun ShareAppScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val androidAppLink = "https://play.google.com/store/apps/details?id=cornhole.beanbag.thepeopleyoucantrust"
    val iosAppLink = "https://apps.apple.com/us/app/thepeoplethatyoucantrust/id6479319870"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.share_app_header_text),
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.bungee_spice_regular)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            HorizontalDivider(
                color = colorResource(R.color.blue),
                thickness = 2.dp
            )

            Text(
                text = stringResource(R.string.share_app_body_text),
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.sansita_swashed_semi_bold)),
                color = Color(0xFF45C0C6),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // iOS row
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 24.dp, bottom = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ios_icon_app),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = stringResource(R.string.ios_text),
                    fontSize = 54.sp,
                    fontFamily = FontFamily(Font(R.font.truculenta_semi_bold)),
                    color = colorResource(R.color.blue)
                )
                IconButton(
                    onClick = {
                        copyToClipboard(context, iosAppLink)
                        scope.launch {
                            snackbarHostState.showSnackbar("Link copied to clipboard: iOS")
                        }
                    },
                    modifier = Modifier.size(45.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.copy_icon),
                        contentDescription = "Copy iOS link"
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.ios_qr_code),
                contentDescription = "iOS QR code",
                modifier = Modifier.size(220.dp)
            )

            // Android row
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.android_icon_app),
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
                Text(
                    text = stringResource(R.string.android_text),
                    fontSize = 50.sp,
                    fontFamily = FontFamily(Font(R.font.truculenta_semi_bold)),
                    color = colorResource(R.color.androidIconColor)
                )
                IconButton(
                    onClick = {
                        copyToClipboard(context, androidAppLink)
                        scope.launch {
                            snackbarHostState.showSnackbar("Link copied to clipboard: Android")
                        }
                    },
                    modifier = Modifier.size(45.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.copy_icon),
                        contentDescription = "Copy Android link"
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.android_qr_code),
                contentDescription = "Android QR code",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("app_link", text))
}
