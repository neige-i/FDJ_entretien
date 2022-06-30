package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) : SearchContract.Presenter {

    private val scope = CoroutineScope(SupervisorJob())

    private var searchView: SearchContract.View? = null

    override fun onCreated(searchView: SearchContract.View) {
        this.searchView = searchView

        scope.launch(Dispatchers.IO) {
            searchRepository.getSearchedLeagueNameFlow()
                .flatMapLatest { searchedLeagueName ->
                    // STEP 2: API call
                    flowOf(sportRepository.getSoccerTeamsByLeague(searchedLeagueName))
                }
                .collectLatest { teamResponses ->
                    // STEP 3: Handle API response
                    if (teamResponses == null) {
                        withContext(Dispatchers.Main) {
                            searchView.showErrorToast()
                        }
                    } else {
                        val searchUiModel = SearchUiModel(
                            resultCountText = LocalText.ResWithArgs(
                                stringId = R.string.team_count_in_league,
                                args = listOf(teamResponses.size)
                            ),
                            teamUiModels = teamResponses.mapNotNull { teamResponse ->
                                TeamUiModel(
                                    id = teamResponse.idTeam ?: return@mapNotNull null,
                                    badgeImageUrl = teamResponse.strTeamBadge ?: return@mapNotNull null,
                                    onClicked = teamResponse.strTeam?.let {
                                        { searchView.openTeamDetails(teamName = it) }
                                    } ?: return@mapNotNull null
                                )
                            }
                        )

                        withContext(Dispatchers.Main) {
                            searchView.showSearchResults(searchUiModel)
                        }
                    }
                }
        }
    }

    override fun onMenuCreated() {
        scope.launch(Dispatchers.IO) {
            searchRepository.getCurrentQueryFlow().collectLatest { currentQuery ->

                val autocompleteUiModels = if (currentQuery.isBlank()) {
                    emptyList()
                } else {
                    sportRepository.getSoccerLeagues()
                        .filter { league ->
                            league.strLeague?.contains(currentQuery, ignoreCase = true) == true ||
                                    league.strLeagueAlternate?.contains(currentQuery, ignoreCase = true) == true
                        }
                        .mapNotNull { league ->
                            AutocompleteUiModel(
                                id = league.idLeague ?: return@mapNotNull null,
                                suggestion = league.strLeague ?: return@mapNotNull null,
                                onClicked = { searchView?.setSearchQuery(league.strLeague) }
                            )
                        }
                }

                withContext(Dispatchers.Main) {
                    searchView?.showAutocompleteSuggestions(autocompleteUiModels)

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
        scope.cancel()
        searchView = null // To prevent leaks
    }
}