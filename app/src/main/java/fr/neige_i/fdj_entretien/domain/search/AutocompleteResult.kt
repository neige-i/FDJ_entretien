package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.data.sport_api.model.LeagueResponse

data class AutocompleteResult(
    val currentQuery: String,
    val suggestedLeagues: List<LeagueResponse>,
)
