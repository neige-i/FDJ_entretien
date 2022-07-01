package fr.neige_i.fdj_entretien.ui.detail

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.domain.DataResult
import fr.neige_i.fdj_entretien.domain.detail.GetTeamDetailUseCase
import fr.neige_i.fdj_entretien.domain.detail.TeamDetail
import fr.neige_i.fdj_entretien.util.LocalText
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val getTeamDetailUseCase: GetTeamDetailUseCase,
) : DetailContract.Presenter {

    private val scope = CoroutineScope(SupervisorJob())

    private var detailView: DetailContract.View? = null

    override fun onCreated(detailView: DetailContract.View) {
        this.detailView = detailView
    }

    override fun onTeamNameRetrieved(teamName: String) {
        scope.launch(Dispatchers.IO) {

            val teamDetailResult = getTeamDetailUseCase.invoke(teamName)

            withContext(Dispatchers.Main) {
                when (teamDetailResult) {
                    is DataResult.Content -> detailView?.showDetailInfo(mapUiModel(teamDetail = teamDetailResult.data))
                    is DataResult.Error -> detailView?.showErrorToast(teamDetailResult.errorMessage)
                }
            }
        }
    }

    private fun mapUiModel(teamDetail: TeamDetail) = DetailUiModel(
        toolbarTitle = teamDetail.teamResponse.strTeam
            ?.let { LocalText.Simple(content = it) }
            ?: LocalText.Res(stringId = R.string.unavailable_name),
        bannerImageUrl = teamDetail.teamResponse.strTeamBanner,
        country = teamDetail.teamResponse.strCountry
            ?.let { LocalText.Simple(content = it) }
            ?: LocalText.Res(stringId = R.string.unavailable_country),
        league = LocalText.Simple(content = teamDetail.leagueToDisplay),
        description = teamDetail.teamResponse.strDescriptionEN
            ?.let { LocalText.Simple(content = it) }
            ?: LocalText.Res(R.string.unavailable_description),
    )

    override fun onDestroy() {
        detailView = null // To prevent leaks
        scope.cancel()
    }
}