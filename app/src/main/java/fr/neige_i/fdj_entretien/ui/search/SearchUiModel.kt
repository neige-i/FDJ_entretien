package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.util.LocalText

data class SearchUiModel(
    val resultCountText: LocalText,
    val teamUiModels: List<TeamUiModel>,
)
