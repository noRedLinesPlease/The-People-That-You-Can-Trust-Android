package cornhole.beanbag.thepeopleyoucantrust

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsViewModel
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsScreen
import org.junit.Assert.assertTrue
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class AboutUsScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Fake ViewModel — prevents the real network call, lets tests inject state
    class FakeAboutUsViewModel(initialState: UiState) : AboutUsViewModel() {
        init {
            _uiState.value = initialState
        }
    }

    private val loadedState = AboutUsViewModel.UiState(
        isLoading = false,
        header = "About Us",
        body = "We connect buyers with trusted sellers.",
        subHeader = "Our Mission",
        subHeaderBody = "Building trust in commerce."
    )

    @Test
    fun loadingState_showsSpinner() {
        composeTestRule.setContent {
            AboutUsScreen(
                viewModel = FakeAboutUsViewModel(AboutUsViewModel.UiState(isLoading = true)),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun loadedState_showsHeader() {
        composeTestRule.setContent {
            AboutUsScreen(
                viewModel = FakeAboutUsViewModel(loadedState),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("About Us").assertIsDisplayed()
    }

    @Test
    fun loadedState_showsAllContent() {
        composeTestRule.setContent {
            AboutUsScreen(
                viewModel = FakeAboutUsViewModel(loadedState),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("We connect buyers with trusted sellers.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Our Mission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Building trust in commerce.").assertIsDisplayed()
    }

    @Test
    fun companiesButton_triggersNavigation() {
        var navigated = false

        composeTestRule.setContent {
            AboutUsScreen(
                viewModel = FakeAboutUsViewModel(loadedState),
                onNavigateToCompanies = { navigated = true }
            )
        }

        composeTestRule.onNodeWithText("Browse Companies").performClick()

        assertTrue(navigated)
    }

    @Test
    fun loadingState_doesNotShowContent() {
        composeTestRule.setContent {
            AboutUsScreen(
                viewModel = FakeAboutUsViewModel(AboutUsViewModel.UiState(isLoading = true)),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("About Us").assertDoesNotExist()
    }
}