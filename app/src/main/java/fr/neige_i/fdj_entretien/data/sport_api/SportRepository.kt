package fr.neige_i.fdj_entretien.data.sport_api

import fr.neige_i.fdj_entretien.data.sport_api.model.LeagueResponse
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportRepository @Inject constructor(
    private val sportDataSource: SportDataSource,
) {

    companion object {
        private const val SOCCER_LEAGUE_FILTER = "Soccer"
    }

    suspend fun getSoccerTeamsByLeague(leagueName: String): NetworkResult<List<TeamResponse>> = try {
        sportDataSource.getTeamsByLeague(leagueName)
            .teams
            ?.filter { team -> team.strSport?.equals(SOCCER_LEAGUE_FILTER) == true }
            ?.let { teams -> NetworkResult.Success(content = teams) }
            ?: NetworkResult.Failure.ApiFailure
    } catch (e: Exception) {
        NetworkResult.Failure.IoFailure
    }

    suspend fun getTeamByName(teamName: String): NetworkResult<TeamResponse> = try {
        sportDataSource.getTeamByName(teamName)
            .teams
            ?.getOrNull(0)
            ?.let { team -> NetworkResult.Success(content = team) }
            ?: NetworkResult.Failure.ApiFailure
    } catch (e: Exception) {
        NetworkResult.Failure.IoFailure
    }

    suspend fun getSoccerLeagues(): NetworkResult<List<LeagueResponse>> = try {
        sportDataSource.getAllLeagues()
            .leagues
            ?.filter { league -> league.strSport?.equals(SOCCER_LEAGUE_FILTER) == true }
            ?.let { leagues -> NetworkResult.Success(content = leagues) }
            ?: NetworkResult.Failure.ApiFailure
    } catch (e: Exception) {
        NetworkResult.Failure.IoFailure
    }
}