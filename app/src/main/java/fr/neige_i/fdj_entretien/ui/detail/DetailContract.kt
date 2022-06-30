package fr.neige_i.fdj_entretien.ui.detail

interface DetailContract {

    interface View {
        fun showDetailInfo(detailUiModel: DetailUiModel)
    }

    interface Presenter {
        fun onCreated(detailView: View)
        fun onTeamNameRetrieved(teamName: String)
        fun onDestroy()
    }
}