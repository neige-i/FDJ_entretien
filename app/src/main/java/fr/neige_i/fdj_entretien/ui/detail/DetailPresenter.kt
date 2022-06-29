package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) : DetailContract.Presenter {

    private var detailView: DetailContract.View? = null

    override fun onCreated(detailView: DetailContract.View) {
        this.detailView = detailView
    }

    override fun onTeamNameRetrieved(teamName: String) {
        detailView?.showDetailInfo(
            // STEP 6: API call
            combine(
                sportRepository.getTeamByNameFlow(teamName).filterNotNull(),
                searchRepository.getSearchedLeagueNameFlow(),
            ) { team, searchedLeagueName ->
                // STEP 7: Handle API response
                DetailState(
                    toolbarTitle = team.strTeam!!,
                    bannerImageUrl = team.strTeamBanner,
                    country = team.strCountry!!,
                    league = searchedLeagueName,
                    description = team.strDescriptionEN!!,
                )
            }
        )
    }

    override fun onDestroy() {
        detailView = null // To prevent leaks
    }
}