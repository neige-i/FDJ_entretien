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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getTeamDetailUseCase: GetTeamDetailUseCase = mockk()
    private val coroutineDispatcherProvider = testCoroutineRule.getCoroutineDispatcherProvider()

    private val subject = DetailPresenter(getTeamDetailUseCase, coroutineDispatcherProvider)

    private val view: DetailContract.View = mockk(relaxed = true)

    @Before
    fun setUp() {
        subject.onCreated(view)

        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getTeamDetailResult(getTeamResponse())
    }

    @After
    fun tearDown() {
        coVerify(exactly = 1) {
            getTeamDetailUseCase.invoke("teamName")
        }
        verify(exactly = 1) {
            coroutineDispatcherProvider.io
            coroutineDispatcherProvider.main
        }
        confirmVerified(getTeamDetailUseCase, coroutineDispatcherProvider, view)
    }

    @Test
    fun `nominal case - show default detail`() = testCoroutineRule.runTest {
        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            view.showDetailInfo(getDefaultDetailUiModel())
        }
    }

    @Test
    fun `alt case - show detail with unavailable name`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getTeamDetailResult(getTeamResponse(name = null))

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            val uiModelWithoutName = getDefaultDetailUiModel().copy(toolbarTitle = LocalText.Res(R.string.unavailable_name))
            view.showDetailInfo(uiModelWithoutName)
        }
    }

    @Test
    fun `alt case - show detail with unavailable country`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getTeamDetailResult(getTeamResponse(country = null))

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            val uiModelWithoutCountry = getDefaultDetailUiModel().copy(country = LocalText.Res(R.string.unavailable_country))
            view.showDetailInfo(uiModelWithoutCountry)
        }
    }

    @Test
    fun `alt case - show detail with unavailable description`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns getTeamDetailResult(getTeamResponse(description = null))

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 1) {
            val uiModelWithoutDescription = getDefaultDetailUiModel().copy(description = LocalText.Res(R.string.unavailable_description))
            view.showDetailInfo(uiModelWithoutDescription)
        }
    }

    @Test
    fun `do not show info when correctly retrieve detail but with null view`() = testCoroutineRule.runTest {
        // GIVEN
        subject.onCreated(null)

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 0) {
            view.showDetailInfo(getDefaultDetailUiModel())
        }
    }

    @Test
    fun `error case - show error when retrieve info with error`() = testCoroutineRule.runTest {
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
    }

    @Test
    fun `do not show error when incorrectly retrieve detail with null view`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { getTeamDetailUseCase.invoke("teamName") } returns DataResult.Error(1)
        subject.onCreated(null)

        // WHEN
        subject.onTeamNameRetrieved("teamName")
        runCurrent()

        // THEN
        verify(exactly = 0) {
            view.showErrorToast(any())
        }
    }

    // region IN

    private fun getTeamResponse(
        name: String? = "strTeam",
        banner: String? = "strTeamBanner",
        country: String? = "strCountry",
        description: String? = "strDescriptionEN",
    ): TeamResponse = mockk {
        every { strTeam } returns name
        every { strTeamBanner } returns banner
        every { strCountry } returns country
        every { strDescriptionEN } returns description
    }

    private fun getTeamDetailResult(teamResponse: TeamResponse) = DataResult.Content(
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