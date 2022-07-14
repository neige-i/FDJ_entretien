package fr.neige_i.fdj_entretien.domain.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetTeamDetailUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val sportRepository: SportRepository = mockk()
    private val searchRepository: SearchRepository = mockk()

    private val subject = GetTeamDetailUseCase(sportRepository, searchRepository)

    @Before
    fun setUp() {
        coEvery { sportRepository.getTeamByName(any()) } returns NetworkResult.Success(content = defaultTeamDetail)
        every { searchRepository.getSearchedLeagueNameFlow() } returns flowOf("World Cup")
    }

    @After
    fun tearDown() {
        coVerify(exactly = 1) { sportRepository.getTeamByName("Arsenal") }
        confirmVerified(sportRepository, searchRepository)
    }

    @Test
    fun `nominal case - return data content when team result is successful and searched league is not empty`() = testCoroutineRule.runTest {
        // WHEN
        val teamDetail = subject.invoke("Arsenal")

        // THEN
        assertEquals(
            DataResult.Content(
                data = TeamDetail(
                    teamResponse = defaultTeamDetail,
                    leagueToDisplay = "World Cup"
                )
            ),
            teamDetail
        )
        verify(exactly = 1) { searchRepository.getSearchedLeagueNameFlow() }
    }

    @Test
    fun `alt case - return data content with available leagues when team result is successful and searched league is empty`() =
        testCoroutineRule.runTest {
            // GIVEN
            every { searchRepository.getSearchedLeagueNameFlow() } returns flowOf("")

            // WHEN
            val teamDetail = subject.invoke("Arsenal")

            // THEN
            assertEquals(
                DataResult.Content(
                    data = TeamDetail(
                        teamResponse = defaultTeamDetail,
                        leagueToDisplay = "Champions League"
                    )
                ),
                teamDetail
            )
            verify(exactly = 1) { searchRepository.getSearchedLeagueNameFlow() }
        }

    @Test
    fun `edge case - return error when team result fails with API exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getTeamByName(any()) } returns NetworkResult.Failure.ApiFailure

        // WHEN
        val teamDetail = subject.invoke("Arsenal")

        // THEN
        assertEquals(
            DataResult.Error(errorMessage = R.string.team_api_error),
            teamDetail
        )
    }

    @Test
    fun `edge case - return error when team result fails with IO exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getTeamByName(any()) } returns NetworkResult.Failure.IoFailure

        // WHEN
        val teamDetail = subject.invoke("Arsenal")

        // THEN
        assertEquals(
            DataResult.Error(errorMessage = R.string.load_team_error),
            teamDetail
        )
    }

    @Test
    fun `edge case - return error when team result fails with timeout exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getTeamByName(any()) } coAnswers {
            delay(60_000)
            NetworkResult.Success(content = defaultTeamDetail)
        }

        // WHEN
        val teamDetail = subject.invoke("Arsenal")
        advanceTimeBy(30_000)

        // THEN
        assertEquals(
            DataResult.Error(errorMessage = R.string.load_team_error),
            teamDetail
        )
    }

    private val defaultTeamDetail: TeamResponse = mockk(relaxed = true) {
        every { strLeague } returns null
        every { strLeague2 } returns "Champions League"
        every { strLeague3 } returns ""
    }
}