package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import fr.neige_i.fdj_entretien.domain.detail.GetTeamDetailUseCase
import fr.neige_i.fdj_entretien.domain.detail.TeamDetail
import fr.neige_i.fdj_entretien.util.LocalText
import io.mockk.*
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getTeamDetailUseCase: GetTeamDetailUseCase = mockk()

    private val subject = DetailPresenter(getTeamDetailUseCase, testCoroutineRule.getCoroutineDispatcherProvider())

    private val view: DetailContract.View = mockk(relaxed = true)

    @Before
    fun setUp() {
        subject.onCreated(view)

        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getDefaultTeamDetailResult()
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // Then
        verify(exactly = 1) {
            view.showDetailInfo(getDefaultDetailUiModel())
        }
        confirmVerified(view)
    }

    @Test
    fun `show detail with unavailable name`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getDefaultTeamDetailResult(
            mockk {
                every { strTeam } returns null // Team name unavailable
                every { strTeamBanner } returns "strTeamBanner"
                every { strCountry } returns "strCountry"
                every { strDescriptionEN } returns "strDescriptionEN"
            }
        )

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            val uiModelWithoutName = getDefaultDetailUiModel().copy(toolbarTitle = LocalText.Res(R.string.unavailable_name))
            view.showDetailInfo(uiModelWithoutName)
        }
        confirmVerified(view)
    }

    @Test
    fun `show toast error when retrieve name with error`() = testCoroutineRule.runTest {
        // GIVEN
        val messageErrorId = 1
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns DataResult.Error(messageErrorId)

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            view.showErrorToast(messageErrorId)
        }
        confirmVerified(view)
    }

    // region IN

    private fun getDefaultTeamResponse(): TeamResponse = mockk {
        every { strTeam } returns "strTeam"
        every { strTeamBanner } returns "strTeamBanner"
        every { strCountry } returns "strCountry"
        every { strDescriptionEN } returns "strDescriptionEN"
    }

    private fun getDefaultTeamDetailResult(teamResponse: TeamResponse = getDefaultTeamResponse()) = DataResult.Content(
        data = TeamDetail(
            teamResponse = teamResponse,
            leagueToDisplay = "UEFA"
        )
    )

    // endregion IN

    // region OUT

    private fun getDefaultDetailUiModel() = DetailUiModel(
        toolbarTitle = LocalText.Simple("strTeam"),
        bannerImageUrl = "strTeamBanner",
        country = LocalText.Simple("strCountry"),
        league = LocalText.Simple("UEFA"),
        description = LocalText.Simple("strDescriptionEN"),
    )

    // endregion OUT
}