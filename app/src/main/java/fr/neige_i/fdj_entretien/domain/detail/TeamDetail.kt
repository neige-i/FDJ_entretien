package fr.neige_i.fdj_entretien.domain.detail

import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse

data class TeamDetail(
    val teamResponse: TeamResponse,
    val leagueToDisplay: String,
)
