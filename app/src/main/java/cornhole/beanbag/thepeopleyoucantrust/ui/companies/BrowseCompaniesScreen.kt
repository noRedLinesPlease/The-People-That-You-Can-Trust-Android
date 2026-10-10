package cornhole.beanbag.thepeopleyoucantrust.ui.companies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cornhole.beanbag.thepeopleyoucantrust.R
import cornhole.beanbag.thepeopleyoucantrust.ui.ErrorView

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowseCompaniesScreen(
    viewModel: CompaniesViewModel,
    onCompanyClick: (String) -> Unit
) {
    val companies by viewModel.companyList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val categories by viewModel.categoryList.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("") }

    val filteredCompanies = remember(companies, selectedCategory) {
        if (selectedCategory.isEmpty()) companies
        else companies.filter { it.companyListingCategoryList.contains(selectedCategory) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            companies.isEmpty() -> {
                ErrorView()
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            categories.forEach { category ->
                                val isSelected = category == selectedCategory
                                Text(
                                    text = category,
                                    textDecoration = TextDecoration.Underline,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF45C0C6) else Color(0xFF2272F1),
                                    modifier = Modifier
                                        .clickable {
                                            selectedCategory =
                                                if (selectedCategory == category) "" else category
                                        }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                    items(filteredCompanies) { company ->
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
    }
}
