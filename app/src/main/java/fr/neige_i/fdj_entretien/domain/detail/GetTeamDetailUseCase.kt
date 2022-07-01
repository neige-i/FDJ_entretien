package fr.neige_i.fdj_entretien.domain.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class GetTeamDetailUseCase @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) {

    // STEP 6: API call
    suspend operator fun invoke(teamName: String): DataResult<TeamDetail> = withTimeoutOrNull(30_000) {
        sportRepository.getTeamByName(teamName)
    }.let { teamResult ->
        // STEP 7: Handle API response

        when (teamResult) {
            is NetworkResult.Success -> {
                // Do NOT keep collecting the flow, only a snapshot is needed here
                val searchedLeagueName = searchRepository.getSearchedLeagueNameFlow().first()

                val team = teamResult.content

                DataResult.Content(
                    data = TeamDetail(
                        teamResponse = team,
                        leagueToDisplay = searchedLeagueName.ifEmpty { getAllAvailableLeagues(team) }
                    )
                )
            }
            is NetworkResult.Failure.ApiFailure -> DataResult.Error(errorMessage = R.string.team_api_error)
            else -> DataResult.Error(errorMessage = R.string.load_team_error)
        }
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