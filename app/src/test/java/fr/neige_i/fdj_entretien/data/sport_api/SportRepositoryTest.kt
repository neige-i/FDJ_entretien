package fr.neige_i.fdj_entretien.data.sport_api

import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.sport_api.model.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SportRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val sportDataSource: SportDataSource = mockk()

    private val subject = SportRepository(sportDataSource)

    @Before
    fun setUp() {
        coEvery { sportDataSource.getTeamsByLeague(any()) } returns
                TeamListResponse(
                    teams = listOf(
                        soccerTeam,
                        rugbyTeam,
                        getTeam(null),
                    )
                )
        coEvery { sportDataSource.getTeamByName(any()) } returns TeamListResponse(teams = listOf(soccerTeam))
        coEvery { sportDataSource.getAllLeagues() } returns
                LeagueListResponse(
                    leagues = listOf(
                        soccerLeague,
                        rugbyLeague,
                        getLeague(null),
                    )
                )
    }

    // region Team list

    @Test
    fun `returns soccer teams only`() = testCoroutineRule.runTest {
        // WHEN
        val soccerTeamsResult = subject.getSoccerTeamsByLeague("Champions league")

        // THEN
        assertEquals(
            NetworkResult.Success(content = listOf(soccerTeam)),
            soccerTeamsResult
        )
    }

    @Test
    fun `returns API error when get null teams`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getTeamsByLeague(any()) } returns TeamListResponse(teams = null)

        // WHEN
        val soccerTeamsResult = subject.getSoccerTeamsByLeague("Champions league")

        // THEN
        assertEquals(
            NetworkResult.Failure.ApiFailure,
            soccerTeamsResult
        )
    }

    @Test
    fun `returns IO error when getting teams throws an exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getTeamsByLeague(any()) } throws Exception("ERR!")

        // WHEN
        val soccerTeamsResult = subject.getSoccerTeamsByLeague("Champions league")

        // THEN
        assertEquals(
            NetworkResult.Failure.IoFailure,
            soccerTeamsResult
        )
    }

    // endregion Team list

    // region Team detail

    @Test
    fun `returns soccer team detail`() = testCoroutineRule.runTest {
        // WHEN
        val teamDetailResult = subject.getTeamByName("Arsenal")

        // THEN
        assertEquals(
            NetworkResult.Success(content = soccerTeam),
            teamDetailResult
        )
    }

    @Test
    fun `returns API error when get null team detail`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getTeamByName(any()) } returns TeamListResponse(teams = null)

        // WHEN
        val teamDetailResult = subject.getTeamByName("Arsenal")

        // THEN
        assertEquals(
            NetworkResult.Failure.ApiFailure,
            teamDetailResult
        )
    }

    @Test
    fun `returns API error when get empty team detail`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getTeamByName(any()) } returns TeamListResponse(teams = emptyList())

        // WHEN
        val teamDetailResult = subject.getTeamByName("Arsenal")

        // THEN
        assertEquals(
            NetworkResult.Failure.ApiFailure,
            teamDetailResult
        )
    }

    @Test
    fun `returns IO error when getting team detail throws an exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getTeamByName(any()) } throws Exception("uh-oh")

        // WHEN
        val teamDetailResult = subject.getTeamByName("Arsenal")

        // THEN
        assertEquals(
            NetworkResult.Failure.IoFailure,
            teamDetailResult
        )
    }

    // endregion Team detail

    // region League detail

    @Test
    fun `returns soccer leagues only`() = testCoroutineRule.runTest {
        // WHEN
        val soccerLeaguesResult = subject.getSoccerLeagues()

        // THEN
        assertEquals(
            NetworkResult.Success(content = listOf(soccerLeague)),
            soccerLeaguesResult
        )
    }

    @Test
    fun `returns API error when get null leagues`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getAllLeagues() } returns LeagueListResponse(leagues = null)

        // WHEN
        val soccerLeaguesResult = subject.getSoccerLeagues()

        // THEN
        assertEquals(
            NetworkResult.Failure.ApiFailure,
            soccerLeaguesResult
        )
    }

    @Test
    fun `returns IO error when getting leagues throws an exception`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { sportDataSource.getAllLeagues() } throws Exception("!!")

        // WHEN
        val soccerLeaguesResult = subject.getSoccerLeagues()

        // THEN
        assertEquals(
            NetworkResult.Failure.IoFailure,
            soccerLeaguesResult
        )
    }

    // endregion League detail

    // region IN

    private fun getTeam(sport: String?): TeamResponse = mockk {
        every { strSport } returns sport
    }

    private fun getLeague(sport: String?): LeagueResponse = mockk {
        every { strSport } returns sport
    }

    private val soccerTeam = getTeam("Soccer")
    private val rugbyTeam = getTeam("Rugby")
    private val soccerLeague = getLeague("Soccer")
    private val rugbyLeague = getLeague("Rugby")

    // endregion IN
}