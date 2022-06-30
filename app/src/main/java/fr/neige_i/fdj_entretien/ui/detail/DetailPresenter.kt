package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import fr.neige_i.fdj_entretien.data.sport_api.SportRepository
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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

            // STEP 6: API call
            val team = withTimeout(30_000) {
                sportRepository.getTeamByName(teamName)
            }

            // Do NOT keep collecting the flow, only a snapshot is needed here
            val searchedLeagueName = searchRepository.getSearchedLeagueNameFlow().first()

            // STEP 7: Handle API response
            if (team != null) {
                val leagueToDisplay = searchedLeagueName.ifEmpty {
                    listOf(
                        team.strLeague,
                        team.strLeague2,
                        team.strLeague3,
                        team.strLeague4,
                        team.strLeague5,
                        team.strLeague6,
                        team.strLeague7,
                    ).filterNot { league ->
                        league.isNullOrEmpty()
                    }.joinToString()
                }

                val detailUiModel = DetailUiModel(
                    toolbarTitle = team.strTeam?.let {
                        LocalText.Simple(content = it)
                    } ?: LocalText.Res(stringId = R.string.unavailable_name),
                    bannerImageUrl = team.strTeamBanner,
                    country = team.strCountry?.let {
                        LocalText.Simple(content = it)
                    } ?: LocalText.Res(stringId = R.string.unavailable_country),
                    league = LocalText.Simple(content = leagueToDisplay),
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
        }
    }

    override fun onDestroy() {
        detailView = null // To prevent leaks
        scope.cancel()
    }
}