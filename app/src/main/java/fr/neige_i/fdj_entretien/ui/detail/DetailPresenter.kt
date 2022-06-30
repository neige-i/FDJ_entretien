package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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
                flowOf(sportRepository.getTeamByName(teamName)),
                searchRepository.getSearchedLeagueNameFlow(),
            ) { team, searchedLeagueName ->

                // STEP 7: Handle API response
                if (team != null) {
                    val detailUiModel = DetailUiModel(
                        toolbarTitle = team.strTeam?.let {
                            LocalText.Simple(content = it)
                        } ?: LocalText.Res(stringId = R.string.unavailable_name),
                        bannerImageUrl = team.strTeamBanner,
                        country = team.strCountry?.let {
                            LocalText.Simple(content = it)
                        } ?: LocalText.Res(stringId = R.string.unavailable_country),
                        league = LocalText.Simple(content = searchedLeagueName),
                        description = team.strDescriptionEN?.let {
                            LocalText.Simple(content = it)
                        } ?: LocalText.Res(R.string.unavailable_description),
                    )

                    withContext(Dispatchers.Main) {
                        detailView?.showDetailInfo(detailUiModel)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        detailView?.showErrorToast()
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