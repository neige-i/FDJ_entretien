package fr.neige_i.fdj_entretien.ui.search

data class AutocompleteState(
    val id: Int,
    val suggestion: String,
    val onClicked: () -> Unit,
)
