package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAutocompleteResultUseCase @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) {

    operator fun invoke(): Flow<AutocompleteResult> = searchRepository.getCurrentQueryFlow()
        .map { currentQuery ->

            val suggestions = if (currentQuery.isBlank()) {
                emptyList()
            } else {
                sportRepository.getSoccerLeagues().filter { league ->
                    league.strLeague?.contains(currentQuery, ignoreCase = true) == true ||
                            league.strLeagueAlternate?.contains(currentQuery, ignoreCase = true) == true
                }
            }

            AutocompleteResult(
                currentQuery = currentQuery,
                suggestedLeagues = suggestions,
            )
        }
}