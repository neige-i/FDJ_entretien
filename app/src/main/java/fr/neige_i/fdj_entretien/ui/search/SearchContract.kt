package fr.neige_i.fdj_entretien.ui.search

interface SearchContract {

    interface View {
        fun setSearchQuery(searchQuery: String)
        fun expandSearchView(searchQuery: String)
        fun setAutocompleteVisibility(isAutocompleteVisible: Boolean)
        fun showAutocompleteSuggestions(autocompleteUiModels: List<AutocompleteUiModel>)
        fun showSearchResults(searchUiModel: SearchUiModel)
        fun openTeamDetails(teamName: String)
        fun showErrorToast()
    }

    interface Presenter {
        fun onCreated(searchView: View)
        fun onMenuCreated()
        fun onSearchModified(leagueName: String)
        fun onSearchSubmitted(leagueName: String)
        fun onDestroy()
    }
}