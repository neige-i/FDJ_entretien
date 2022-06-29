package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val sportRepository: SportRepository,
) : SearchContract.Presenter {

    private var searchView: SearchContract.View? = null

    override fun onCreated(searchView: SearchContract.View) {
        this.searchView = searchView
    }

    override fun onSearchSubmitted(leagueName: String) {
        searchView?.showSearchResults(
            // STEP 2: API call
            sportRepository.getTeamsByLeagueFlow(leagueName).filterNotNull().map { teamResponses ->
                // STEP 3: Handle API response
                SearchState(
                    resultCountText = LocalText.ResWithArgs(
                        stringId = R.string.team_count_in_league,
                        args = listOf(teamResponses.size, leagueName)
                    ),
                    teamStates = teamResponses.map { teamResponse ->
                        TeamState(
                            id = teamResponse.idTeam!!,
                            badgeImageUrl = teamResponse.strTeamBadge,
                            onClicked = { searchView?.openTeamDetails(teamName = teamResponse.strTeam!!) }
                        )
                    }
                )
            }
        )
    }

    override fun onDestroy() {
        searchView = null // To prevent leaks
    }
}