package fr.neige_i.fdj_entretien.domain.search

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

    private val subject = GetSearchResultUseCase(sportRepository, searchRepository)

    @Before
    fun setUp() {
        every { searchRepository.getSearchedLeagueNameFlow() } returns flowOf("leagueName")
        coEvery { sportRepository.getSoccerTeamsByLeague("leagueName") } returns NetworkResult.Success(content = defaultList)
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { searchRepository.getSearchedLeagueNameFlow() }
        coVerify { sportRepository.getSoccerTeamsByLeague("leagueName") }
        confirmVerified(sportRepository, searchRepository)
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Content(data = defaultList), result)
    }

    @Test
    fun `edge case - return error when API call fails with IO exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague("leagueName") } returns NetworkResult.Failure.IoFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Error(errorMessage = R.string.load_team_list_error), result)
    }

    @Test
    fun `edge case - return error when API call fails with API exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerTeamsByLeague("leagueName") } returns NetworkResult.Failure.ApiFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(DataResult.Error(errorMessage = R.string.team_list_api_error), result)
    }

    private fun getDefaultTeamsResponse(): List<TeamResponse> = List(5) { mockk() } // Arbitrary content

    private val defaultList = getDefaultTeamsResponse()
}