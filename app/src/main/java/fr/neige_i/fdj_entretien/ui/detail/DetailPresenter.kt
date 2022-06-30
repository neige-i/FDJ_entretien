package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) : DetailContract.Presenter {

    private val scope = CoroutineScope(SupervisorJob())

    private var detailView: DetailContract.View? = null

    override fun onCreated(detailView: DetailContract.View) {
        this.detailView = detailView
    }

    override fun onTeamNameRetrieved(teamName: String) {
        scope.launch(Dispatchers.IO) {
            combine(
                // STEP 6: API call
                flowOf(sportRepository.getTeamByName(teamName)).filterNotNull(),
                searchRepository.getSearchedLeagueNameFlow(),
            ) { team, searchedLeagueName ->

                // STEP 7: Handle API response
                if (team.strTeam != null && team.strCountry != null && team.strDescriptionEN != null) {
                    val detailUiModel = DetailUiModel(
                        toolbarTitle = team.strTeam,
                        bannerImageUrl = team.strTeamBanner,
                        country = team.strCountry,
                        league = searchedLeagueName,
                        description = team.strDescriptionEN,
                    )

                    withContext(Dispatchers.Main) {
                        detailView?.showDetailInfo(detailUiModel)
                    }
                }
            }.collect()
        }
    }

    override fun onDestroy() {
        detailView = null // To prevent leaks
        scope.cancel()
    }
}