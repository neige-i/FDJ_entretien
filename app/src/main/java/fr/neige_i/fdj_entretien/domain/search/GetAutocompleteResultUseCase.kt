package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.data.sport_api.model.NetworkResult
import fr.neige_i.fdj_entretien.domain.DataResult
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
                DataResult.Content(data = emptyList())
            } else {
                when (val soccerLeaguesResult = sportRepository.getSoccerLeagues()) {
                    is NetworkResult.Success -> {
                        val soccerLeagueSuggestions = soccerLeaguesResult.content.filter { league ->
                            league.strLeague?.contains(currentQuery, ignoreCase = true) == true ||
                                    league.strLeagueAlternate?.contains(currentQuery, ignoreCase = true) == true
                        }
                        DataResult.Content(data = soccerLeagueSuggestions)
                    }
                    is NetworkResult.Failure.ApiFailure -> DataResult.Error(errorMessage = R.string.leagues_api_error)
                    is NetworkResult.Failure.IoFailure -> DataResult.Error(errorMessage = R.string.load_leagues_error)
                }
            }

            AutocompleteResult(
                currentQuery = currentQuery,
                suggestedLeagues = suggestions,
            )
        }
}