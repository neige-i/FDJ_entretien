package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchPresenter @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) : SearchContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private var searchView: SearchContract.View? = null

    override fun onCreated(searchView: SearchContract.View) {
        this.searchView = searchView

        launch {
            searchRepository.getSearchedLeagueNameFlow().collectLatest { searchedLeagueName ->
                // STEP 2: API call
                sportRepository.getTeamsByLeagueFlow(searchedLeagueName).collectLatest { teamResponses ->
                    // STEP 3: Handle API response
                    if (teamResponses == null) {
                        withContext(Dispatchers.Main) {
                            searchView.showErrorToast()
                        }
                    } else {
                        val searchState = SearchState(
                            resultCountText = LocalText.ResWithArgs(
                                stringId = R.string.team_count_in_league,
                                args = listOf(teamResponses.size, searchedLeagueName)
                            ),
                            teamStates = teamResponses.mapNotNull { teamResponse ->
                                if (teamResponse.idTeam != null && teamResponse.strTeamBadge != null && teamResponse.strTeam != null) {
                                    TeamState(
                                        id = teamResponse.idTeam,
                                        badgeImageUrl = teamResponse.strTeamBadge,
                                        onClicked = { searchView.openTeamDetails(teamName = teamResponse.strTeam) }
                                    )
                                } else {
                                    null
                                }
                            }
                        )

                        withContext(Dispatchers.Main) {
                            searchView.showSearchResults(searchState)
                        }
                    }
                }
            }
        }
    }

    override fun onMenuCreated() {
        launch {
            searchRepository.getCurrentQueryFlow().collectLatest { currentQuery ->

                val autocompleteStates = if (currentQuery.isBlank()) {
                    emptyList()
                } else {
                    sportRepository.getSoccerLeaguesFlow().first()
                        .filter { league ->
                            league.strLeague?.contains(currentQuery, ignoreCase = true) == true ||
                                    league.strLeagueAlternate?.contains(currentQuery, ignoreCase = true) == true
                        }
                        .mapNotNull { league ->
                            if (league.idLeague != null && league.strLeague != null) {
                                AutocompleteState(
                                    id = league.idLeague,
                                    suggestion = league.strLeague,
                                    onClicked = { searchView?.setSearchQuery(league.strLeague) }
                                )
                            } else {
                                null
                            }
                        }
                }

                withContext(Dispatchers.Main) {
                    searchView?.showAutocompleteSuggestions(autocompleteStates)

                    if (currentQuery.isNotEmpty()) {
                        searchView?.expandSearchView(currentQuery)
                    }
                }
            }
        }
    }

    override fun onSearchModified(leagueName: String) {
        searchRepository.setCurrentQuery(leagueName)
        searchView?.setAutocompleteVisibility(true)
    }

    override fun onSearchSubmitted(leagueName: String) {
        searchRepository.setSearchedLeagueName(leagueName)
        searchView?.setAutocompleteVisibility(false)
    }

    override fun onDestroy() {
        job.cancel()
        searchView = null // To prevent leaks
    }
}