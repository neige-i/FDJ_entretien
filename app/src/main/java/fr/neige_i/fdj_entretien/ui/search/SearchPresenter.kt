package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
            searchRepository.getSearchedLeagueNameFlow().collect { searchedLeagueName ->
                // STEP 2: API call
                sportRepository.getTeamsByLeagueFlow(searchedLeagueName).filterNotNull().collectLatest { teamResponses ->
                    // STEP 3: Handle API response
                    val searchState = SearchState(
                        resultCountText = LocalText.ResWithArgs(
                            stringId = R.string.team_count_in_league,
                            args = listOf(teamResponses.size, searchedLeagueName)
                        ),
                        teamStates = teamResponses.map { teamResponse ->
                            TeamState(
                                id = teamResponse.idTeam!!,
                                badgeImageUrl = teamResponse.strTeamBadge,
                                onClicked = { searchView.openTeamDetails(teamName = teamResponse.strTeam!!) }
                            )
                        }
                    )

                    withContext(Dispatchers.Main) {
                        searchView.showSearchResults(searchState)
                    }
                }
            }
        }
    }

    override fun onMenuCreated() {
        launch {
            val currentQuery = searchRepository.getCurrentQueryFlow().first()

            if (currentQuery.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    searchView?.expandSearchView(currentQuery)
                }
            }
        }
    }

    override fun onSearchModified(leagueName: String) {
        searchRepository.setCurrentQuery(leagueName)

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
        searchRepository.setSearchedLeagueName(leagueName)

        searchView?.setAutocompleteVisibility(false)
    }

    override fun onDestroy() {
        job.cancel()
        searchView = null // To prevent leaks
    }
}