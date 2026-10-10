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
import cornhole.beanbag.thepeopleyoucantrust.ui.aboutUs.AboutUsScreenContent
import org.junit.Assert.assertTrue
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class AboutUsScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loadedState = AboutUsViewModel.UiState(
        isLoading = false,
        failedToLoad = false,
        header = "About Us",
        body = "We connect buyers with trusted sellers.",
        subHeader = "Our Mission",
        subHeaderBody = "Building trust in commerce."
    )

    @Test
    fun loadingState_showsSpinner() {
        composeTestRule.setContent {
            AboutUsScreenContent(
                uiState = loadedState.copy(isLoading = true),
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
            AboutUsScreenContent(
                uiState = loadedState,
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("About Us").assertIsDisplayed()
    }

    @Test
    fun loadedState_showsAllContent() {
        composeTestRule.setContent {
            AboutUsScreenContent(
                uiState = loadedState,
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
            AboutUsScreenContent(
                uiState = loadedState,
                onNavigateToCompanies = { navigated = true }
            )
        }

        composeTestRule.onNodeWithText("Browse companies").performClick()

        assertTrue(navigated)
    }

    @Test
    fun loadingState_doesNotShowContent() {
        composeTestRule.setContent {
            AboutUsScreenContent(
                uiState = loadedState.copy(isLoading = true),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("About Us").assertDoesNotExist()
    }

    @Test
    fun apiError_showsErrorMessage() {
        composeTestRule.setContent {
            AboutUsScreenContent(
                uiState = loadedState.copy(failedToLoad = true),
                onNavigateToCompanies = {}
            )
        }

        composeTestRule.onNodeWithText("About Us").assertDoesNotExist()
        composeTestRule.onNodeWithText(
            "We're having trouble loading this page right now. Please try again."
        ).assertIsDisplayed()
    }
}