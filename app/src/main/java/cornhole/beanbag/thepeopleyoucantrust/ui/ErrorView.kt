package cornhole.beanbag.thepeopleyoucantrust.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cornhole.beanbag.thepeopleyoucantrust.R

@Composable
fun ErrorView() {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.internal_error_message),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}