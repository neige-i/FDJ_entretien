package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSearchResultUseCase @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) {

    operator fun invoke(): Flow<DataResult<List<TeamResponse>>> = searchRepository.getSearchedLeagueNameFlow()
        .map { searchedLeagueName ->
            // STEP 2: API call
            when (val teamsResult = sportRepository.getSoccerTeamsByLeague(searchedLeagueName)) {
                is NetworkResult.Success -> DataResult.Content(data = teamsResult.content)
                is NetworkResult.Failure.IoFailure -> DataResult.Error(errorMessage = R.string.load_team_list_error)
                is NetworkResult.Failure.ApiFailure -> DataResult.Error(errorMessage = R.string.team_list_api_error)
            }
        }
}