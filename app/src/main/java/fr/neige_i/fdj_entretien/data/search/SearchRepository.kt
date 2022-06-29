package fr.neige_i.fdj_entretien.data.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor() {

    private val searchedLeagueNameMutableSharedFlow = MutableSharedFlow<String>(replay = 1)

    fun getSearchedLeagueNameFlow(): Flow<String> = searchedLeagueNameMutableSharedFlow

    fun setSearchedLeagueName(leagueName: String) {
        searchedLeagueNameMutableSharedFlow.tryEmit(leagueName)
    }
}