package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.util.LocalText

data class SearchState(
    val resultCountText: LocalText,
    val teamStates: List<TeamState>,
)
