package fr.neige_i.fdj_entretien.ui.search

import kotlinx.coroutines.flow.Flow

interface SearchContract {

    interface View {
        fun setSearchQuery(searchQuery: String)
        fun setAutocompleteVisibility(isAutocompleteVisible: Boolean)
        fun showAutocompleteSuggestions(autocompleteStateFlow: Flow<List<AutocompleteState>>)
        fun showSearchResults(searchStateFlow: Flow<SearchState>)
        fun openTeamDetails(teamName: String)
    }

    interface Presenter {
        fun onCreated(searchView: View)
        fun onSearchModified(leagueName: String)
        fun onSearchSubmitted(leagueName: String)
        fun onDestroy()
    }
}