package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.util.EquatableCallback

data class TeamUiModel(
    val id: Int,
    val badgeImageUrl: String?,
    val onClicked: EquatableCallback,
)
