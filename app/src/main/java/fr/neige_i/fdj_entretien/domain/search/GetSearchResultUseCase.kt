package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSearchResultUseCase @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) {

    operator fun invoke(): Flow<List<TeamResponse>?> = searchRepository.getSearchedLeagueNameFlow()
        .map { searchedLeagueName ->
            // STEP 2: API call
            sportRepository.getSoccerTeamsByLeague(searchedLeagueName)
        }
}