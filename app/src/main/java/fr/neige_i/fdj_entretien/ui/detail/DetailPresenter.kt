package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DetailPresenter @Inject constructor(
    private val sportRepository: SportRepository,
    private val searchRepository: SearchRepository,
) : DetailContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private var detailView: DetailContract.View? = null

    override fun onCreated(detailView: DetailContract.View) {
        this.detailView = detailView
    }

    override fun onTeamNameRetrieved(teamName: String) {
        launch {
            combine(
                // STEP 6: API call
                flowOf(sportRepository.getTeamByName(teamName)).filterNotNull(),
                searchRepository.getSearchedLeagueNameFlow(),
            ) { team, searchedLeagueName ->

                // STEP 7: Handle API response
                if (team.strTeam != null && team.strCountry != null && team.strDescriptionEN != null) {
                    val detailState = DetailState(
                        toolbarTitle = team.strTeam,
                        bannerImageUrl = team.strTeamBanner,
                        country = team.strCountry,
                        league = searchedLeagueName,
                        description = team.strDescriptionEN,
                    )

                    withContext(Dispatchers.Main) {
                        detailView?.showDetailInfo(detailState)
                    }
                }
            }.collect()
        }
    }

    override fun onDestroy() {
        detailView = null // To prevent leaks
    }
}