package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val sportRepository: SportRepository,
) : DetailContract.Presenter {

    private var detailView: DetailContract.View? = null

    override fun onCreated(detailView: DetailContract.View) {
        this.detailView = detailView
    }

    override fun onTeamNameRetrieved(teamName: String) {
        detailView?.showDetailInfo(
            // STEP 6: API call
            sportRepository.getTeamByNameFlow(teamName).filterNotNull().map {
                // STEP 7: Handle API response
                DetailState(
                    toolbarTitle = it.strTeam!!,
                    bannerImageUrl = it.strTeamBanner,
                    country = it.strCountry!!,
                    league = it.strLeague!!,
                    description = it.strDescriptionEN!!,
                )
            }
        )
    }

    override fun onDestroy() {
        detailView = null // To prevent leaks
    }
}