package fr.neige_i.fdj_entretien.domain.search

import app.cash.turbine.test
import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.currentTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSearchResultUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val sportRepository: SportRepository = mockk()
    private val searchRepository: SearchRepository = mockk()
    private val coroutineDispatcherProvider = testCoroutineRule.getCoroutineDispatcherProvider()

    private val subject = GetSearchResultUseCase(sportRepository, searchRepository, coroutineDispatcherProvider)

    @Before
    fun setUp() {
        every { searchRepository.getSearchedLeagueNameFlow() } returns flowOf(DEFAULT_LEAGUE_NAME)
        coEvery { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) } returns NetworkResult.Success(content = DEFAULT_LIST)
    }

    @After
    fun tearDown() {
        verify(exactly = 1) {
            searchRepository.getSearchedLeagueNameFlow()
            coroutineDispatcherProvider.io
        }
        coVerify { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) }
        confirmVerified(sportRepository, searchRepository, coroutineDispatcherProvider)
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Content(data = DEFAULT_LIST), result)
    }

    @Test
    fun `edge case - return error when API call fails with IO exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) } returns NetworkResult.Failure.IoFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Error(errorMessage = R.string.load_team_list_error), result)
    }

    @Test
    fun `edge case - return error when API call fails with API exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) } returns NetworkResult.Failure.ApiFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Error(errorMessage = R.string.team_list_api_error), result)
    }

    @Test
    fun `edge case - connectivity is bad, success after 2 tries`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) } returnsMany listOf(
            NetworkResult.Failure.IoFailure,
            NetworkResult.Failure.IoFailure,
            NetworkResult.Success(content = DEFAULT_LIST)
        )

        // WHEN
        subject.invoke().test {
            // THEN
            assertEquals(DataResult.Content(data = DEFAULT_LIST), awaitItem())
            assertEquals(3_000, currentTime)
            awaitComplete()

            verify(exactly = 1) {
                searchRepository.getSearchedLeagueNameFlow()
                coroutineDispatcherProvider.io
            }
            coVerify(exactly = 3) { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) }
            confirmVerified(searchRepository, sportRepository, coroutineDispatcherProvider)
        }
    }

    @Test
    fun `error case - connectivity is atrocious, failure after 5 tries`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) } returns NetworkResult.Failure.IoFailure

        // WHEN
        subject.invoke().test {
            // THEN
            assertEquals(DataResult.Error(errorMessage = R.string.load_team_list_error), awaitItem())
            assertEquals(10_000, currentTime)
            awaitComplete()

            verify(exactly = 1) {
                searchRepository.getSearchedLeagueNameFlow()
                coroutineDispatcherProvider.io
            }
            coVerify(exactly = 5) { sportRepository.getSoccerTeamsByLeague(DEFAULT_LEAGUE_NAME) }
            confirmVerified(searchRepository, sportRepository, coroutineDispatcherProvider)
        }
    }

    companion object {
        private const val DEFAULT_LEAGUE_NAME = "leagueName"
        private val DEFAULT_LIST = getDefaultTeamsResponse()

        private fun getDefaultTeamsResponse(): List<TeamResponse> = List(5) { mockk() } // Arbitrary content
    }
}