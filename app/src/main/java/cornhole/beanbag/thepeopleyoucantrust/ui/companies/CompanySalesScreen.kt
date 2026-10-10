package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cornhole.beanbag.thepeopleyoucantrust.R

@Composable
fun CompanySalesScreen(
    viewModel: CompaniesViewModel,
    onCompanyClick: (String) -> Unit
) {
    val companies by viewModel.companyList.collectAsStateWithLifecycle()

    val salesCompanies = remember(companies) {
        companies.filter { company ->
            company.variantsObject.any { it.pricingObject.isOnSale }
        }
    }

    if (salesCompanies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.no_companies_on_sale_message),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(salesCompanies) { company ->
                val logo = company.companyLogoList.firstOrNull()
                BusinessViewholderCompose(
                    companyName = company.companyName,
                    logoUrl = logo?.logoUrl ?: "",
                    onClick = { onCompanyClick(logo?.companyWebsite ?: "") }
                )
            }
        }
    }
}
