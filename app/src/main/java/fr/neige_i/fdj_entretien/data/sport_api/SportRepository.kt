package fr.neige_i.fdj_entretien.data.sport_api

import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportRepository @Inject constructor(
    private val sportDataSource: SportDataSource,
) {

    fun getTeamsByLeagueFlow(leagueName: String): Flow<List<TeamResponse>?> = flow {
        emit(sportDataSource.getTeamsByLeague(leagueName).teams)
    }

    fun getTeamByNameFlow(teamName: String): Flow<TeamResponse?> = flow {
        emit(sportDataSource.getTeamByName(teamName).teams?.get(0))
    }
}