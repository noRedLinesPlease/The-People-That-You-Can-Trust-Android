package cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.ui.ErrorView

@Composable
fun AboutUsScreen(
    viewModel: AboutUsViewModel,
    onNavigateToCompanies: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AboutUsScreenContent(
       uiState,
        onNavigateToCompanies
    )
}
@Composable
fun AboutUsScreenContent(
    uiState: AboutUsViewModel.UiState = AboutUsViewModel.UiState(),
    onNavigateToCompanies: () -> Unit = {}
) {
    when {
        uiState.isLoading ->
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

        uiState.failedToLoad -> ErrorView()

        else ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(
                color = Color(0xFF0000FF),
                thickness = 1.dp
            )

            Text(
                text = uiState.header,
                fontSize = 26.sp,
                fontFamily = FontFamily(Font(R.font.bungee_font)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )

            Image(
                painter = painterResource(R.drawable.about_us_image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )

            Text(
                text = uiState.body,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.truculenta_font)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            )

            Text(
                text = uiState.subHeader,
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.protest_guerrilla)),
                color = Color(0xFF45C0C6),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Text(
                text = uiState.subHeaderBody,
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.kurale_regular)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 15.dp)
            )

            Button(
                onClick = onNavigateToCompanies,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF45C0C6)
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.companies_button_text),
                    fontFamily = FontFamily(Font(R.font.permanent_marker_font)),
                    color = Color.White
                )
            }
        }
    }
}
