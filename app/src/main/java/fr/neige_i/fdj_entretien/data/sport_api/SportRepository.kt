package fr.neige_i.fdj_entretien.data.sport_api

import fr.neige_i.fdj_entretien.data.sport_api.model.LeagueResponse
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

    suspend fun getSoccerTeamsByLeague(leagueName: String): List<TeamResponse>? = sportDataSource.getTeamsByLeague(leagueName)
        .teams
        ?.filter { team -> team.strSport?.equals(SOCCER_LEAGUE_FILTER) == true }

    suspend fun getTeamByName(teamName: String): TeamResponse? = sportDataSource.getTeamByName(teamName)
        .teams
        ?.getOrNull(0)

    suspend fun getSoccerLeagues(): List<LeagueResponse> = sportDataSource.getAllLeagues()
        .leagues
        ?.filter { league -> league.strSport?.equals(SOCCER_LEAGUE_FILTER) == true }
        ?: emptyList()
}