package fr.neige_i.fdj_entretien.ui.search

data class AutocompleteUiModel(
    val id: Int,
    val suggestion: String,
    val onClicked: () -> Unit,
)
