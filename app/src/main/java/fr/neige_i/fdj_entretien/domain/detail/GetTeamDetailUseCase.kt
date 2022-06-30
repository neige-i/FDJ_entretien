package fr.neige_i.fdj_entretien.domain.detail

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class GetTeamDetailUseCase @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) {

    // STEP 6: API call
    suspend operator fun invoke(teamName: String): TeamDetail? = withTimeoutOrNull(30_000) {
        sportRepository.getTeamByName(teamName)
    }?.let { team ->
        // STEP 7: Handle API response

        // Do NOT keep collecting the flow, only a snapshot is needed here
        val searchedLeagueName = searchRepository.getSearchedLeagueNameFlow().first()

        TeamDetail(
            teamResponse = team,
            leagueToDisplay = searchedLeagueName.ifEmpty { getAllAvailableLeagues(team) }
        )
    }

    private fun getAllAvailableLeagues(team: TeamResponse): String {
        return listOf(
            team.strLeague,
            team.strLeague2,
            team.strLeague3,
            team.strLeague4,
            team.strLeague5,
            team.strLeague6,
            team.strLeague7,
        ).filterNot { league ->
            league.isNullOrEmpty()
        }.joinToString()
    }
}