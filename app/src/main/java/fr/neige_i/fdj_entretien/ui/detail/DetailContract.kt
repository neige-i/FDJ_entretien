package fr.neige_i.fdj_entretien.ui.detail

import kotlinx.coroutines.flow.Flow

interface DetailContract {

    interface View {
        fun showDetailInfo(detailStateFlow: Flow<DetailState>)
    }

    interface Presenter {
        fun onCreated(detailView: View)
        fun onTeamNameRetrieved(teamName: String)
        fun onDestroy()
    }
}