package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.util.LocalText

data class DetailUiModel(
    val toolbarTitle: LocalText,
    val bannerImageUrl: String?,
    val country: LocalText,
    val league: LocalText,
    val description: LocalText,
)
