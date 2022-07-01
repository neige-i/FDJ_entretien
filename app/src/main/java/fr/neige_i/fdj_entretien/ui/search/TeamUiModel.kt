package fr.neige_i.fdj_entretien.ui.search

data class TeamUiModel(
    val id: Int,
    val badgeImageUrl: String?,
    val onClicked: () -> Unit,
)
