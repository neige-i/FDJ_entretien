package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.LeagueResponse
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.domain.DataResult
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetAutocompleteResultUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val sportRepository: SportRepository = mockk()
    private val searchRepository: SearchRepository = mockk()

    private val subject = GetAutocompleteResultUseCase(sportRepository, searchRepository)

    @Before
    fun setUp() {
        every { searchRepository.getCurrentQueryFlow() } returns flowOf("lea")
        coEvery { sportRepository.getSoccerLeagues() } returns NetworkResult.Success(content = listOf(league1, league2, league3, league4))
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { searchRepository.getCurrentQueryFlow() }
        confirmVerified(sportRepository, searchRepository)
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(
            AutocompleteResult(
                currentQuery = "lea",
                suggestedLeagues = DataResult.Content(data = listOf(league1, league2))
            ),
            result
        )
        coVerify(exactly = 1) { sportRepository.getSoccerLeagues() }
    }

    @Test
    fun `alt case - returns empty suggestion when query is empty`() = testCoroutineRule.runTest {
        // GIVEN
        every { searchRepository.getCurrentQueryFlow() } returns flowOf("")

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(
            AutocompleteResult(
                currentQuery = "",
                suggestedLeagues = DataResult.Content(data = emptyList())
            ),
            result
        )
    }

    @Test
    fun `edge case - returns error message when API call fails with API exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerLeagues() } returns NetworkResult.Failure.ApiFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(
            AutocompleteResult(
                currentQuery = "lea",
                suggestedLeagues = DataResult.Error(errorMessage = R.string.leagues_api_error)
            ),
            result
        )
        coVerify(exactly = 1) { sportRepository.getSoccerLeagues() }
    }

    @Test
    fun `edge case - returns error message when API call fails with IO exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportRepository.getSoccerLeagues() } returns NetworkResult.Failure.IoFailure

        // WHEN
        val result = subject.invoke().first()

        // THEN
        assertEquals(
            AutocompleteResult(
                currentQuery = "lea",
                suggestedLeagues = DataResult.Error(errorMessage = R.string.load_leagues_error)
            ),
            result
        )
        coVerify(exactly = 1) { sportRepository.getSoccerLeagues() }
    }

    private val league1 = LeagueResponse(
        idLeague = null,
        strLeague = "Champions League",
        strSport = null,
        strLeagueAlternate = null,
    )
    private val league2 = LeagueResponse(
        idLeague = null,
        strLeague = "National",
        strSport = null,
        strLeagueAlternate = "Premier League",
    )
    private val league3 = LeagueResponse(
        idLeague = null,
        strLeague = null,
        strSport = null,
        strLeagueAlternate = "UEFA",
    )
    private val league4 = LeagueResponse(
        idLeague = null,
        strLeague = null,
        strSport = null,
        strLeagueAlternate = null,
    )
}