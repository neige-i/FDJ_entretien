package fr.neige_i.fdj_entretien.ui.search

import kotlinx.coroutines.flow.Flow

interface SearchContract {

    interface View {
        fun showSearchResults(searchStateFlow: Flow<SearchState>)
        fun openTeamDetails(teamName: String)
    }

    interface Presenter {
        fun onCreated(searchView: View)
        fun onSearchSubmitted(leagueName: String)
        fun onDestroy()
    }
}