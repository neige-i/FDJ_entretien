package fr.neige_i.fdj_entretien.ui.detail

import androidx.annotation.StringRes

interface DetailContract {

    interface View {
        fun showDetailInfo(detailUiModel: DetailUiModel)
        fun showErrorToast(@StringRes message: Int)
    }

    interface Presenter {
        fun onCreated(detailView: View?)
        fun onTeamNameRetrieved(teamName: String)
        fun onDestroy()
    }
}