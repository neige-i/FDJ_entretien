package fr.neige_i.fdj_entretien.ui.detail

interface DetailContract {

    interface View {
        fun showDetailInfo(detailState: DetailState)
    }

    interface Presenter {
        fun onCreated(detailView: View)
        fun onTeamNameRetrieved(teamName: String)
        fun onDestroy()
    }
}