package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.search.GetAutocompleteResultUseCase
import fr.neige_i.fdj_entretien.domain.search.GetSearchResultUseCase
import fr.neige_i.fdj_entretien.domain.search.UpdateSearchUseCase
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val getSearchResultUseCase: GetSearchResultUseCase,
    private val getAutocompleteResultUseCase: GetAutocompleteResultUseCase,
    private val updateSearchUseCase: UpdateSearchUseCase,
) : SearchContract.Presenter {

    private val scope = CoroutineScope(SupervisorJob())

    private var searchView: SearchContract.View? = null

    private var isSearchViewExpanded = false

    override fun onCreated(searchView: SearchContract.View) {
        this.searchView = searchView

        scope.launch(Dispatchers.IO) {
            getSearchResultUseCase.invoke().collectLatest { teamResponses ->
                // STEP 3: Handle API response
                withContext(Dispatchers.Main) {
                    if (teamResponses != null) {
                        searchView.showSearchResults(mapUiModel(teamResponses))
                    } else {
                        searchView.showErrorToast()
                    }
                }
            }
        }
    }

    private fun mapUiModel(teamResponses: List<TeamResponse>): SearchUiModel {
        val teamList = teamResponses.mapNotNull { teamResponse ->
            TeamUiModel(
                id = teamResponse.idTeam ?: return@mapNotNull null,
                badgeImageUrl = teamResponse.strTeamBadge ?: return@mapNotNull null,
                onClicked = teamResponse.strTeam
                    ?.let {
                        { searchView?.openTeamDetails(teamName = it) }
                    }
                    ?: return@mapNotNull null
            )
        }

        return SearchUiModel(
            resultCountText = LocalText.ResWithArgs(
                stringId = R.string.team_count_in_league,
                args = listOf(teamResponses.size)
            ),
            teamUiModels = teamList,
        )
    }

    override fun onMenuCreated() {
        scope.launch(Dispatchers.IO) {
            getAutocompleteResultUseCase.invoke().collectLatest { autocompleteResult ->

                val currentQuery = autocompleteResult.currentQuery

                val autocompleteUiModels = autocompleteResult.suggestedLeagues.mapNotNull { league ->
                    AutocompleteUiModel(
                        id = league.idLeague ?: return@mapNotNull null,
                        suggestion = league.strLeague ?: return@mapNotNull null,
                        onClicked = { searchView?.setSearchQuery(league.strLeague) }
                    )
                }

                withContext(Dispatchers.Main) {
                    searchView?.showAutocompleteSuggestions(autocompleteUiModels)

                    if (currentQuery.isNotEmpty() && !isSearchViewExpanded) {
                        searchView?.expandSearchView(currentQuery)
                    }
                }
            }
        }
    }

    override fun onSearchViewExpanded(isExpanded: Boolean) {
        isSearchViewExpanded = isExpanded
        searchView?.setAutocompleteVisibility(isExpanded)
    }

    override fun onSearchModified(leagueName: String) {
        updateSearchUseCase.setCurrentQuery(leagueName)
        searchView?.setAutocompleteVisibility(true)
    }

    override fun onSearchSubmitted(leagueName: String) {
        updateSearchUseCase.setSubmittedQuery(leagueName)
        searchView?.setAutocompleteVisibility(false)
    }

    override fun onDestroy() {
        scope.cancel()
        searchView = null // To prevent leaks
    }
}