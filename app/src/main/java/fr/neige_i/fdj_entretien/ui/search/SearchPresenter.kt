package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val sportRepository: SportRepository,
) : SearchContract.Presenter {

    private var searchView: SearchContract.View? = null

    override fun onCreated(searchView: SearchContract.View) {
        this.searchView = searchView
    }

    override fun onSearchModified(leagueName: String) {
        searchView?.setAutocompleteVisibility(true)

        val autocompleteStatesFlow = if (leagueName.isBlank()) {
            flowOf(emptyList())
        } else {
            sportRepository.getSoccerLeaguesFlow().map { soccerLeagues ->
                soccerLeagues
                    .filter { league ->
                        league.strLeague?.contains(leagueName, ignoreCase = true) == true ||
                                league.strLeagueAlternate?.contains(leagueName, ignoreCase = true) == true
                    }
                    .map { league ->
                        AutocompleteState(
                            id = league.idLeague!!,
                            suggestion = league.strLeague!!,
                            onClicked = { searchView?.setSearchQuery(league.strLeague) }
                        )
                    }
            }
        }

        searchView?.showAutocompleteSuggestions(autocompleteStatesFlow)
    }

    override fun onSearchSubmitted(leagueName: String) {
        searchView?.setAutocompleteVisibility(false)

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